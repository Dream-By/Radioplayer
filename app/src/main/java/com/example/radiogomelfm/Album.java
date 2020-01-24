package com.example.radiogomelfm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {

    @SerializedName("artist")
    @Expose
    private String artist;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("image")
    @Expose
    private List<Image> image = null;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List <Image> image) {
        this.image = image;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [image = "+image+", artist = "+artist+", title = "+title+"]";
    }



}
