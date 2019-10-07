package com.example.musicappfinal.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavDao {
    @Insert
    void insert(FavSong favSong);
    @Delete
    void delete(FavSong favSong);
    @Query("DELETE FROM fav_table")
    void deleteAllFavSongs();
    @Query("SELECT * FROM  fav_table ORDER BY title")
    LiveData<List<FavSong>> getAllFavSongs();

}
