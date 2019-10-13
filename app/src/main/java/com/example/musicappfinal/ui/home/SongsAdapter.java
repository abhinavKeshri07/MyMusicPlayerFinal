package com.example.musicappfinal.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappfinal.FavSongsViewModel;
import com.example.musicappfinal.R;
import com.example.musicappfinal.database.Song;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    FavSongsViewModel favSongsViewModel;
    private List<Song> allSongs = new LinkedList<Song>();
    protected OnItemClickListenerAbhinav mListener;
    Fragment fragment;
    int CurrentSong;
    public SongsAdapter(Fragment fragment){
        this.fragment = fragment;
        try {
            favSongsViewModel = ViewModelProviders.of(fragment.getActivity()).get(FavSongsViewModel.class);
            favSongsViewModel.getCurrentSong().observe(fragment.getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    CurrentSong = integer;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_song_in_recyclerview,parent, false);
        SongsViewHolder svh = new SongsViewHolder(v, mListener);
        return svh;
    }


    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        Song song = allSongs.get(position);
        holder.title.setText(song.SongTitle);
        holder.artist.setText(song.SongArtist);
        holder.songNumber.setText(String.valueOf(position));
        if(position == CurrentSong){
            holder.cardView.setCardBackgroundColor(fragment.getResources().getColor(R.color.colorAccent));
        }else{
            holder.cardView.setCardBackgroundColor(fragment.getResources().getColor(R.color.white));
        }
    }

    public void setOnItemClickListenerAbhinav(OnItemClickListenerAbhinav onItemClickListenerAbhinav){
        this.mListener = onItemClickListenerAbhinav;
    }
    public void setAllSongs(List<Song> songs){
        this.allSongs = songs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }

    public interface OnItemClickListenerAbhinav{
        void onItemClick(int position);
    }

    class SongsViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView artist;
        public TextView songNumber;
        public CardView cardView;
        public SongsViewHolder(@NonNull View itemView, final OnItemClickListenerAbhinav listener) {
            super(itemView);
            songNumber = itemView.findViewById(R.id.songNumber);
            title = itemView.findViewById(R.id.trackTitle);
            artist = itemView.findViewById(R.id.trackArtist);
            cardView = itemView.findViewById(R.id.card_view_home);
            favSongsViewModel.getCurrentSong().observe(fragment.getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer == getAdapterPosition()){
                        cardView.setCardBackgroundColor(fragment.getResources().getColor(R.color.colorAccent));
                    }else{
                        cardView.setCardBackgroundColor(fragment.getResources().getColor(R.color.white));
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }

                    }
                }
            });
        }

    }
}
