package com.example.musicappfinal.database;

public class Song {
    public long SONG_ID;
    public String SongTitle;
    public String SongArtist;
    public String SongData;
    public long DateAdded;
    public String Album;
    public Song(){}
    public Song(long SONG_ID, String songTitle, String songArtist, long dateAdded) {
        this.SONG_ID = SONG_ID;
        SongTitle = songTitle;
        SongArtist = songArtist;
        DateAdded = dateAdded;


    }

    public String getSongTitle() {
        return SongTitle;
    }

    public void setSongTitle(String songTitle) {
        SongTitle = songTitle;
    }

    public String getSongArtist() {
        return SongArtist;
    }

    public void setSongArtist(String songArtist) {
        SongArtist = songArtist;
    }

    public String getSongData() {
        return SongData;
    }

    public long getDateAdded() {
        return DateAdded;
    }

    public void setDateAdded(long dateAdded) {
        DateAdded = dateAdded;
    }

    public String getAlbum() {
        return Album;
    }

    //For some reasong Contructor did not allow for SongData and Album to be added in constructor, hece i added the setters.
    public void setSongData(String songData) {
        SongData = songData;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public Song(long SONG_ID, String songTitle, String songArtist, String songData, long dateAdded, String album) {
        this.SONG_ID = SONG_ID;
        SongTitle = songTitle;
        SongArtist = songArtist;
        SongData = songData;
        DateAdded = dateAdded;
        Album = album;
    }
}

//    val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
//    val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//    val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
//    val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
//    val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)