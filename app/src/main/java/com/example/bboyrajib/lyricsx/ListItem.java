package com.example.bboyrajib.lyricsx;

/**
 * Created by bboyrajib on 15/12/17.
 */

public class ListItem {

    private String song;
    private String artist;
    private String album;
    private String ID;
    private String has_lyric;
    private String DB_ID;
    private String imageURL;
    private String TimeStamp;

    public ListItem(String song, String artist, String album, String ID, String has_lyric,String DB_ID, String imageURL,String TimeStamp) {
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.ID= ID;
        this.has_lyric=has_lyric;
        this.DB_ID=DB_ID;
        this.imageURL=imageURL;
        this.TimeStamp=TimeStamp;
    }


    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getID() {
        return ID;
    }

    public String getHas_lyric() {
        return has_lyric;
    }

    public String getDB_ID() {
        return DB_ID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }
}


