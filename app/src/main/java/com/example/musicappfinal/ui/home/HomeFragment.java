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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappfinal.FavSongsViewModel;
import com.example.musicappfinal.R;
import com.example.musicappfinal.database.Song;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private SongsAdapter songsAdapter;

    private RecyclerView recyclerView;
    private AndroidViewModel viewModel;
    private LinearLayout songInfoHome;
    private TextView songInfoHomeTextView;
    List<Song> songs;

    LinkedList<Song> ll;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_home, container, false);
        songInfoHomeTextView = root.findViewById(R.id.songInfoHomeTextView);
        songInfoHome = root.findViewById(R.id.songInfoHome);
        recyclerView = root.findViewById(R.id.homeRecyclerView);
        LinearLayout songInfoHome = root.findViewById(R.id.songInfoHome);
        songInfoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_nav_home_to_nav_gallery);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        songsAdapter = new SongsAdapter(this);
        songsAdapter.setOnItemClickListenerAbhinav(new SongsAdapter.OnItemClickListenerAbhinav() {
            @Override
            public void onItemClick(int position) {

                ((FavSongsViewModel) viewModel).setCurrentSong(position);
                Uri u = Uri.parse(((FavSongsViewModel) viewModel).allSongs.get(((FavSongsViewModel) viewModel).getCurrentSong().getValue()).SongData);
                if (((FavSongsViewModel) viewModel).mediaPlayer == null) {
                    Log.d(TAG, "onItemClick: if block");
                } else if (((FavSongsViewModel) viewModel).mediaPlayer.isPlaying()) {
                    ((FavSongsViewModel) viewModel).mediaPlayer.stop();
                    ((FavSongsViewModel) viewModel).mediaPlayer.release();
                    Log.d(TAG, "onItemClick: else if block");
                }
                ((FavSongsViewModel) viewModel).mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), u);
                ((FavSongsViewModel) viewModel).mediaPlayer.start();
                ((FavSongsViewModel) viewModel).mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        setOnCompletionListenerToMediaplayer();
                    }
                });
                setupBottomInfo();

                //Toast.makeText(getActivity(), "this position = " + String.valueOf(position), Toast.LENGTH_LONG).show();
            }
        });
        recyclerView.setAdapter(songsAdapter);


        return root;
    }

    private void setOnCompletionListenerToMediaplayer() {
        MediaPlayer mediaPlayer = ((FavSongsViewModel) viewModel).mediaPlayer;
        if (mediaPlayer != null) {
            if (((FavSongsViewModel) viewModel).repeat) {

            } else if (((FavSongsViewModel) viewModel).Shuffle) {
                ((FavSongsViewModel) viewModel).ShuffleSong();
            } else {
                ((FavSongsViewModel) viewModel).incrementSong();
            }
        }
        Uri u = Uri.parse(((FavSongsViewModel)viewModel).allSongs.get(((FavSongsViewModel)viewModel).getCurrentSong().getValue()).SongData);
        if(((FavSongsViewModel)viewModel).mediaPlayer != null){
            ((FavSongsViewModel)viewModel).mediaPlayer.stop();
            ((FavSongsViewModel)viewModel).mediaPlayer.release();
        }
        ((FavSongsViewModel)viewModel).mediaPlayer = MediaPlayer.create(getActivity().getApplication(), u);
        ((FavSongsViewModel)viewModel).mediaPlayer.start();
        ((FavSongsViewModel) viewModel).mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setOnCompletionListenerToMediaplayer();
            }
        });
    }


    private void setupBottomInfo() {
        songInfoHome.setVisibility(View.VISIBLE);
        try {
            songInfoHomeTextView.setText(((FavSongsViewModel) viewModel).allSongs.get(((FavSongsViewModel) viewModel).getCurrentSong().getValue()).SongTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("OnActivityCreated", "onActivityCreated: was called");
        viewModel = ViewModelProviders.of(getActivity()).get(FavSongsViewModel.class);
        ((FavSongsViewModel) viewModel).getCurrentSong().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setupBottomInfo();
            }
        });
        ((FavSongsViewModel) viewModel).getSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                songsAdapter.setAllSongs(songs);
            }
        });


    }


}