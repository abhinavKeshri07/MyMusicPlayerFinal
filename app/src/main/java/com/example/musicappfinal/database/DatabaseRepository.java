package com.example.musicappfinal.database;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicappfinal.MainActivity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class DatabaseRepository {
    private FavDao favDao;
    private LiveData<List<FavSong>> allFavSongs;
    private MutableLiveData<List<Song>> allSong = new MutableLiveData<>();
    public DatabaseRepository(Application application){
        Database database = Database.getInstance(application);
        favDao = database.favDao();
        allFavSongs = favDao.getAllFavSongs();
    }
    public void insert(FavSong favSong){new InsertFavSongAsyncTask(favDao).execute(favSong);}
    public void delete(FavSong favSong){new DeleteFavSongAsyncTask(favDao).execute(favSong);}
    public void deleteAllFavSongs(){new DeleteAllFavSongAsyncTask(favDao).execute();}
    public LiveData<List<FavSong>> getAllFavSongs(){return this.allFavSongs;}

    private static class InsertFavSongAsyncTask extends AsyncTask<FavSong, Void, Void>{
        private FavDao favDao;
        private InsertFavSongAsyncTask(FavDao favDao){
            this.favDao = favDao;
        }
        @Override
        protected Void doInBackground(FavSong... favSongs) {
            favDao.insert(favSongs[0]);
            return null;
        }
    }

    private static class DeleteFavSongAsyncTask extends AsyncTask<FavSong, Void, Void>{
        private FavDao favDao;
        private DeleteFavSongAsyncTask(FavDao favDao){
            this.favDao = favDao;
        }
        @Override
        protected Void doInBackground(FavSong... favSongs){
            favDao.delete(favSongs[0]);
            return null;
        }
    }

    private static class DeleteAllFavSongAsyncTask extends AsyncTask<Void, Void, Void>{
        private FavDao favDao;
        private DeleteAllFavSongAsyncTask(FavDao favDao){
            this.favDao = favDao;
        }
        @Override
        protected Void doInBackground(Void... n){
            favDao.deleteAllFavSongs();
            return null;
        }
    }
    private static class GetAllSongsFromPhoneAsyncTask extends AsyncTask<Context, Void, List<Song>>{
        private WeakReference<DatabaseRepository> DatabaseRepo;
        GetAllSongsFromPhoneAsyncTask(DatabaseRepository DR){
            DatabaseRepo = new WeakReference<DatabaseRepository>(DR);
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            DatabaseRepository dr = DatabaseRepo.get();
            if(dr == null){
                return;
            }
            dr.allSong.postValue(songs);
        }

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
                do{
                    list.add(new Song(songCursor.getLong(songId), songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getLong(songIndex)));
                }while (songCursor.moveToNext());
            }
            return list;
        }
    }
}
