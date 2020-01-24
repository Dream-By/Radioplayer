package com.example.radiogomelfm;

public class Track {

    private String name;
    private Album album;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [album = "+album+", name = "+name+"]";
    }

}
