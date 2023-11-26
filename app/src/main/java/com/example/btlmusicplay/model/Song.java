package com.example.btlmusicplay.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String artist;
    private String path;
    private String duration;

    public Song(String title, String artist, String path, String duration) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
    }

    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        path = in.readString();
        duration = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(path);
        parcel.writeString(duration);
    }
}

