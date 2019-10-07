package com.example.musicappfinal.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_table")
public class FavSong {
    @PrimaryKey
    public long song_id;
    public String artist;
    public String title;
    public String path;
    public FavSong(long song_id, String artist, String title, String path){
        this.song_id = song_id;
        this.artist = artist;
        this.title = title;
        this.path = path;
    }

}
