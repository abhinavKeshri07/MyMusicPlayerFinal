package com.example.musicappfinal;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicappfinal.database.DatabaseRepository;
import com.example.musicappfinal.database.FavSong;
import com.example.musicappfinal.database.Song;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FavSongsViewModel extends AndroidViewModel {
    private DatabaseRepository databaseRepository;
    private MutableLiveData<Integer> CurrentSong ;
    private LiveData<List<FavSong>> allFavSongs;                          // this we get from database.
    private  MutableLiveData<List<Song>> songs ;
    public ArrayList<Song> allSongs;
    public MediaPlayer mediaPlayer;
    public MutableLiveData<Integer> currentPlayingTime;
    public boolean Shuffle = false;
    public boolean repeat = false;
    private static final String TAG = "FavSongsViewModel";
    public FavSongsViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        CurrentSong = new MutableLiveData<>(0);
        allFavSongs = databaseRepository.getAllFavSongs();
        currentPlayingTime = new MutableLiveData<>(0);
    }

    public void insertFavSong(FavSong favSong) {
        databaseRepository.insert(favSong);
    }

    public void deleteFavSong(FavSong favSong) {
        databaseRepository.delete(favSong);
    }

    public void deleteAllFavSong() {
        databaseRepository.deleteAllFavSongs();
    }

    public void setCurrentSong(Integer songNumber) {
        CurrentSong.setValue(songNumber);
    }
    public Song getSongAtIndex(int i ){ return allSongs.get(i); }

    public LiveData<Integer> getCurrentSong() {
        return CurrentSong;
    }

    public void refreshSongs() {
        new GetAllSongsFromPhoneAysncTask(this).execute();
    }

    public LiveData<List<Song>> getSongs() {
        if(songs == null){
            songs = new MutableLiveData<>();
            new GetAllSongsFromPhoneAysncTask(this).execute();
        }
        Log.d(TAG, "getSongs: ");
        return songs;
    }
    public void incrementSong(){
        CurrentSong.setValue((CurrentSong.getValue()+ 1)%allSongs.size());
    }
    public void ShuffleSong(){
        if(allSongs != null && allSongs.size() != 0) {
            CurrentSong.setValue(new Random().nextInt(allSongs.size()));
        }
    }


    static class GetAllSongsFromPhoneAysncTask extends AsyncTask<Void , Void, ArrayList<Song>>{
        private WeakReference<FavSongsViewModel> myWeakRef;
        GetAllSongsFromPhoneAysncTask(FavSongsViewModel favSongsViewModel){
            myWeakRef = new WeakReference<>(favSongsViewModel);
        }
        @Override
        protected void onPostExecute(ArrayList<Song> songsResult) {
            super.onPostExecute(songsResult);
            FavSongsViewModel favSongsViewModel = myWeakRef.get();
            favSongsViewModel.songs.postValue(songsResult);
            favSongsViewModel.allSongs = songsResult;


        }
        public ArrayList<File> findSong(File root){
            ArrayList<File> at = new ArrayList<>();
            File[] files = root.listFiles();
            for(File singleFile: files){
                if(singleFile.isDirectory() && !singleFile.isHidden()){
                    at.addAll(findSong(singleFile));
                }
                else{
                    if(singleFile.getName().endsWith(".mp3")){
                        at.add(singleFile);
                    }
                }
            }
            return at;
        }
        @Override
        protected ArrayList<Song> doInBackground(Void... voids) {
            ArrayList<Song> list = new ArrayList<>();
            FavSongsViewModel viewModel = myWeakRef.get();
            ArrayList<File> al = findSong(Environment.getExternalStorageDirectory());
            for(int i= 0 ;i < al.size(); i++){
                Song song = new Song();
                song.SongData = al.get(i).toString();
                song.SongTitle = al.get(i).getName();
                song.SongArtist = al.get(i).getParent();
                list.add(song);
            }

            return list;

        }
    }

}
