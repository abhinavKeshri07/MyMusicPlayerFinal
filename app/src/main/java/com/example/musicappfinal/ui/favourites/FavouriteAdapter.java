package com.example.musicappfinal.ui.favourites;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappfinal.R;
import com.example.musicappfinal.ui.home.SongsAdapter;

public class FavouriteAdapter {
    class SongsViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView artist;
        public TextView songNumber;
        public SongsViewHolder(@NonNull View itemView, final SongsAdapter.OnItemClickListenerAbhinav listener) {
            super(itemView);
            songNumber = itemView.findViewById(R.id.songNumber);
            title = itemView.findViewById(R.id.trackTitle);
            artist = itemView.findViewById(R.id.trackArtist);
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
