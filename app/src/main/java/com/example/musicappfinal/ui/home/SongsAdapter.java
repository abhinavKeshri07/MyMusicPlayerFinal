package com.example.musicappfinal.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappfinal.R;
import com.example.musicappfinal.database.Song;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    List<Song> allSongs = new LinkedList<Song>();

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_song_in_recyclerview,parent, false);
        SongsViewHolder svh = new SongsViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        Song song = allSongs.get(position);
        holder.title.setText(song.SongTitle);
        holder.artist.setText(song.SongArtist);

    }

    public void setAllSongs(List<Song> songs){
        this.allSongs = songs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }

    class SongsViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView artist;
        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trackTitle);
            artist = itemView.findViewById(R.id.trackArtist);

        }
    }
}
