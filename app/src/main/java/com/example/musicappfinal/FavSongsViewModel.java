package com.example.musicappfinal;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicappfinal.database.DatabaseRepository;
import com.example.musicappfinal.database.FavSong;
import com.example.musicappfinal.database.Song;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FavSongsViewModel extends AndroidViewModel {
    private DatabaseRepository databaseRepository;
    private MutableLiveData<Song> CurrentSong = new MutableLiveData<>();
    private LiveData<List<FavSong>> allFavSongs;                          // this we get from database.
    private static MutableLiveData<List<Song>> songs ;

    public FavSongsViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        allFavSongs = databaseRepository.getAllFavSongs();
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

    public void setCurrentSong(Song song) {
        CurrentSong.setValue(song);
    }

    public LiveData<Song> getCurrentSong() {
        return CurrentSong;
    }

    public void refreshSongs() {
        if(songs == null){
            songs = new MutableLiveData<>();
            new GetAllSongsFromPhoneAysncTask(this).execute();
        }
    }

    public LiveData<List<Song>> getSongs() {
        if(songs == null){
            songs = new MutableLiveData<>();
            new GetAllSongsFromPhoneAysncTask(this).execute();
        }
        return songs;
    }
    static class GetAllSongsFromPhoneAysncTask extends AsyncTask<Void , Void, ArrayList<Song>>{
        private WeakReference<FavSongsViewModel> myWeakRef;
        GetAllSongsFromPhoneAysncTask(FavSongsViewModel favSongsViewModel){
            myWeakRef = new WeakReference<>(favSongsViewModel);
        }
        @Override
        protected void onPostExecute(ArrayList<Song> songsResult) {
            super.onPostExecute(songsResult);
            songs.postValue(songsResult);
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
            /*
            ContentResolver contentResolver = viewModel.getApplication().getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor songCursor = contentResolver.query(songUri,null,null,null,null);
            if(songCursor!= null && songCursor.moveToFirst()){
                int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int songIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
                int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                do{
                    Song song = new Song(songCursor.getLong(songId), songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getLong(songIndex));
                    song.setSongData(songCursor.getString(songData));
                    song.setAlbum(songCursor.getString(songAlbum));
                    list.add(song);

                }while (songCursor.moveToNext());
            }
            if(songCursor != null) songCursor.close();

             */

            ArrayList<File> al = findSong(Environment.getExternalStorageDirectory());
            for(int i= 0 ;i < al.size(); i++){
                Song song = new Song();
                song.SongData = al.toString();
            }
            return list;

        }
    }
    /*
    public static class GetAllSongsFromPhoneAsyncTask extends AsyncTask<Context, Void, List<Song>>{
        private WeakReference<HomeFragment> homeFrag;

        GetAllSongsFromPhoneAsyncTask(HomeFragment homeFragment){
            homeFrag = new WeakReference<>(homeFragment);
        }
        protected void onPreExecute(){

        }
        protected void onPostExecute(List<Song> songs){
            super.onPostExecute(songs);
            HomeFragment hf = homeFrag.get();
            if(hf == null){
                return;
            }
            hf.songsAdapter.setAllSongs(songs);
            hf.recyclerView.setAdapter(hf.songsAdapter);
            hf.songs = songs;
            if(hf.viewModel instanceof FavSongsViewModel){
                ((FavSongsViewModel) hf.viewModel).setSongs(songs);
            }


        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected List<Song> doInBackground(Context... context) {
            List<Song> list = new LinkedList<>();
            ContentResolver contentResolver;
            contentResolver = context[0].getContentResolver();
            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor songCursor = contentResolver.query(songUri,null,null,null,null);
            if(songCursor!= null && songCursor.moveToFirst()){
                int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int songIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
                int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                do{
                    Song song = new Song(songCursor.getLong(songId), songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getLong(songIndex));
                    song.setSongData(songCursor.getString(songData));
                    list.add(song);

                }while (songCursor.moveToNext());
            }
            ArrayList<Song> arrayList = new ArrayList<>();
            arrayList.addAll(list);
            arrayList.sort(new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.SongTitle.compareTo(o2.SongTitle);
                }
            });
            list = new LinkedList<>();
            list.addAll(arrayList);

            return list;
        }
    }

     */
}
