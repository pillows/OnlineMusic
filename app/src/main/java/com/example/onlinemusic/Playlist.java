package com.example.onlinemusic;

import java.lang.reflect.Array;
import java.util.ArrayList;

class SongList
{
    public String url; // URL for song streaming
    public String songName; // Used for displaying the name to the user
    public String name; // The name of the playlist
};

public class Playlist {
    public void createPlayList(){

    }

    public ArrayList<SongList> getPlayList(){
        ArrayList<SongList> list = new ArrayList<>();
        return list;
    }

    public boolean deletePlayList(){
        return true;
    }

    public ArrayList<SongList> updatePlayList(){
        ArrayList<SongList> list = new ArrayList<>();
        return list;
    }
}
