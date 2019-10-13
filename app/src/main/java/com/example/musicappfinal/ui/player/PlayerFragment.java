package com.example.musicappfinal.ui.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.musicappfinal.FavSongsViewModel;
import com.example.musicappfinal.R;
import com.example.musicappfinal.database.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class PlayerFragment extends Fragment {
    private ImageButton playPause, next, prev, repeat, shuffle;
    private TextView title, startTime, endTime;
    private SeekBar seekBar;
    private static final String TAG = "PlayerFragment";
    private FavSongsViewModel favSongsViewModel;
    FloatingActionButton fab;
    private int CurrentSong;
    private boolean exitThread = false;
    private boolean mUserIsSeeking = false;
    //View root;
    Thread thread;
    private int currentPosition = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_player, container, false);
        playPause = root.findViewById(R.id.playPause);
        next = root.findViewById(R.id.skipNext);
        prev = root.findViewById(R.id.skipPrev);
        shuffle = root.findViewById(R.id.shuffle);
        repeat = root.findViewById(R.id.repeat);
        title = root.findViewById(R.id.titlePlayer);

        startTime = root.findViewById(R.id.startTime);
        endTime = root.findViewById(R.id.endTime);

        seekBar = root.findViewById(R.id.seekbar);
        fab = root.findViewById(R.id.saveAsFavourite);

        //this.root = root;
        Log.d(TAG, "onCreateView: ");
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        favSongsViewModel = ViewModelProviders.of(getActivity()).get(FavSongsViewModel.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song = favSongsViewModel.allSongs.get(CurrentSong);

            }
        });
        try {
            if (favSongsViewModel.mediaPlayer != null && favSongsViewModel.mediaPlayer.isPlaying()) {
                final int Duration = favSongsViewModel.mediaPlayer.getDuration();
                seekBar.setMax(Duration);
                int minute = Duration / 60000;
                final int seconds = (Duration % 60000) / 1000;
                endTime.setText(minute + ":" + seconds);
                thread = new Thread(new Runnable() {
                    int refreshTime = 1000;

                    @Override
                    public void run() {
                        while (!exitThread) {
                            seekBar.setProgress(currentPosition);
                            try {
                                sleep(refreshTime);
                                favSongsViewModel.currentPlayingTime.postValue(favSongsViewModel.mediaPlayer.getCurrentPosition());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
                thread.start();
                favSongsViewModel.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        currentPosition = 0;
                        setupOnCompletionListener(favSongsViewModel);
                    }
                });
            }
            favSongsViewModel.currentPlayingTime.observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    startTime.setText(integer / 60000 + ":" + (integer % 60000) / 1000);
                    currentPosition = integer;
                }
            });
            // seekBar seeking functionality.
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int userSelectedPosition = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        userSelectedPosition = progress;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mUserIsSeeking = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mUserIsSeeking = false;
                    if (favSongsViewModel.mediaPlayer != null && favSongsViewModel.mediaPlayer.isPlaying()) {
                        favSongsViewModel.mediaPlayer.seekTo(userSelectedPosition);
                    }
                }
            });


            //Repeate functionality
            if (favSongsViewModel.repeat) {
                repeat.setBackgroundResource(R.drawable.ic_repeat_one_black_24dp);
            } else {
                repeat.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
            }
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favSongsViewModel.repeat = !favSongsViewModel.repeat;
                    if (favSongsViewModel.repeat) {
                        repeat.setBackgroundResource(R.drawable.ic_repeat_one_black_24dp);
                    } else {
                        repeat.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
                    }
                }
            });
            //Set title
            if (favSongsViewModel.getCurrentSong().getValue() != null)
                title.setText(favSongsViewModel.allSongs.get(favSongsViewModel.getCurrentSong().getValue()).getSongTitle());
            //Shuffle setting will be updated
            if (!favSongsViewModel.Shuffle) {
                shuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
            } else {
                shuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
            }
            //shuffle functionality
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favSongsViewModel.Shuffle = !favSongsViewModel.Shuffle;
                    if (favSongsViewModel.Shuffle) {
                        shuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow_24dp);
                    } else {
                        shuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
                    }
                }
            });

            //Current song will be always updated
            favSongsViewModel.getCurrentSong().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    CurrentSong = integer;
                    title.setText(favSongsViewModel.allSongs.get(CurrentSong).getSongTitle());
                }
            });
            //play prev functionality
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CurrentSong == 0) {
                        favSongsViewModel.setCurrentSong(favSongsViewModel.allSongs.size() - 1);
                    } else {
                        favSongsViewModel.setCurrentSong(CurrentSong - 1);
                    }
                    if (favSongsViewModel.mediaPlayer != null) {
                        favSongsViewModel.mediaPlayer.stop();
                        favSongsViewModel.mediaPlayer.release();
                    }
                    favSongsViewModel.mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), Uri.parse(favSongsViewModel.allSongs.get(CurrentSong).SongData));
                    favSongsViewModel.mediaPlayer.start();
                    final int Duration = favSongsViewModel.mediaPlayer.getDuration();
                    favSongsViewModel.currentPlayingTime.setValue(0);
                    int minute = Duration / 60000;
                    final int seconds = (Duration % 60000) / 1000;
                    endTime.setText(minute + ":" + seconds);
                    seekBar.setMax(Duration);
                    if (thread == null) {
                        thread = new Thread(new Runnable() {
                            int refreshTime = 1000;

                            @Override
                            public void run() {
                                while (!exitThread) {

                                    seekBar.setProgress(currentPosition);

                                    try {
                                        favSongsViewModel.currentPlayingTime.postValue(favSongsViewModel.mediaPlayer.getCurrentPosition());
                                        sleep(refreshTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        thread.start();
                    }

                    favSongsViewModel.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currentPosition = 0;
                            setupOnCompletionListener(favSongsViewModel);
                        }
                    });
                    playPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
            });
            //play next functionality
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (favSongsViewModel.Shuffle && !favSongsViewModel.repeat) {
                        favSongsViewModel.ShuffleSong();
                    } else if (favSongsViewModel.repeat) {

                    } else {
                        favSongsViewModel.incrementSong();
                    }

                    if (favSongsViewModel.mediaPlayer != null) {
                        favSongsViewModel.mediaPlayer.stop();
                        favSongsViewModel.mediaPlayer.release();
                    }
                    try {
                        favSongsViewModel.mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), Uri.parse(favSongsViewModel.allSongs.get(CurrentSong).SongData));
                        favSongsViewModel.mediaPlayer.start();
                    } catch (NullPointerException e) {
                        favSongsViewModel.refreshSongs();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_nav_gallery_to_nav_home);
                    }
                    final int Duration = favSongsViewModel.mediaPlayer.getDuration();
                    favSongsViewModel.currentPlayingTime.setValue(0);
                    int minute = Duration / 60000;
                    final int seconds = (Duration % 60000) / 1000;
                    endTime.setText(minute + ":" + seconds);
                    seekBar.setMax(Duration);
                    if (thread == null) {


                        thread = new Thread(new Runnable() {
                            int refreshTime = 1000;

                            @Override
                            public void run() {
                                while (!exitThread) {
                                    seekBar.setProgress(currentPosition);

                                    try {
                                        favSongsViewModel.currentPlayingTime.postValue(favSongsViewModel.mediaPlayer.getCurrentPosition());
                                        sleep(refreshTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        thread.start();
                    }
                    favSongsViewModel.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            favSongsViewModel.currentPlayingTime.setValue(0);
                            setupOnCompletionListener(favSongsViewModel);
                        }
                    });
                    playPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
            });
            //Play pause functionality
            if (favSongsViewModel.mediaPlayer != null && favSongsViewModel.mediaPlayer.isPlaying()) {
                playPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
            }
            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (favSongsViewModel.mediaPlayer != null) {
                        if (favSongsViewModel.mediaPlayer.isPlaying()) {
                            favSongsViewModel.mediaPlayer.pause();
                            playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                        } else {
                            favSongsViewModel.mediaPlayer.start();
                            playPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        }
                    } else {

                        favSongsViewModel.mediaPlayer = MediaPlayer.create(getContext().getApplicationContext(), Uri.parse(favSongsViewModel.getSongAtIndex(CurrentSong).SongData));
                        favSongsViewModel.mediaPlayer.start();
                        final int Duration = favSongsViewModel.mediaPlayer.getDuration();
                        favSongsViewModel.currentPlayingTime.setValue(0);
                        int minute = Duration / 60000;
                        final int seconds = (Duration % 60000) / 1000;
                        endTime.setText(minute + ":" + seconds);
                        seekBar.setMax(Duration);
                        if (thread == null) {
                            thread = new Thread(new Runnable() {
                                int refreshTime = 1000;

                                @Override
                                public void run() {
                                    while (!exitThread) {

                                        seekBar.setProgress(currentPosition);

                                        try {
                                            favSongsViewModel.currentPlayingTime.postValue(favSongsViewModel.mediaPlayer.getCurrentPosition());
                                            sleep(refreshTime);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            thread.start();
                        }
                        favSongsViewModel.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                favSongsViewModel.currentPlayingTime.setValue(0);
                                setupOnCompletionListener(favSongsViewModel);
                            }
                        });
                        playPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);

                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupOnCompletionListener(final FavSongsViewModel favSongsViewModel) {
        if (favSongsViewModel.mediaPlayer != null) {
            if ((favSongsViewModel).repeat) {

            } else if (favSongsViewModel.Shuffle) {
                favSongsViewModel.ShuffleSong();
            } else {
                favSongsViewModel.incrementSong();
            }
        }
        try {
            Uri u = Uri.parse(favSongsViewModel.allSongs.get(favSongsViewModel.getCurrentSong().getValue()).SongData);
            if (favSongsViewModel.mediaPlayer != null) {
                favSongsViewModel.mediaPlayer.stop();
                favSongsViewModel.mediaPlayer.release();
            }
            favSongsViewModel.mediaPlayer = MediaPlayer.create(getActivity().getApplication(), u);
            favSongsViewModel.mediaPlayer.start();
            seekBar.setMax(favSongsViewModel.mediaPlayer.getDuration());
            favSongsViewModel.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setupOnCompletionListener(favSongsViewModel);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitThread = true;

    }
}