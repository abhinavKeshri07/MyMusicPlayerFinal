package com.example.musicappfinal.ui.home;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappfinal.FavSongsViewModel;
import com.example.musicappfinal.R;
import com.example.musicappfinal.database.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {
    private SongsAdapter songsAdapter;
    private RecyclerView recyclerView;
    private AndroidViewModel viewModel;
    List<Song> songs;

    LinkedList<Song> ll;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = root.findViewById(R.id.homeRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        songsAdapter = new SongsAdapter();//this adapter will be set in GetAllSongsFromPhoneAsyncTask

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(FavSongsViewModel.class);
        ((FavSongsViewModel) viewModel).getSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                songsAdapter.setAllSongs(songs);
                songsAdapter.notifyDataSetChanged();
            }
        });

        ((FavSongsViewModel) viewModel).getCurrentSong().observe(getViewLifecycleOwner(), new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                Toast.makeText(getActivity(), "Song was changed", Toast.LENGTH_LONG).show();
            }
        });

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