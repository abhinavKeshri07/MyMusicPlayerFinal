package com.example.musicappfinal.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {FavSong.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database instance;
    public abstract FavDao favDao();
    public static synchronized Database getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "database")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }

}
