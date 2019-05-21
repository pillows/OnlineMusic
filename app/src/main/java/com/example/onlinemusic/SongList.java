package com.example.onlinemusic;


// Model taken from here
// https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#creating-a-model-class

public class SongList {
    public String url; // URL for song streaming
    public String songName; // Used for displaying the name to the user
    public String playlistName; // The name of the playlist


    public SongList() {}  // Needed for Firebase


    public String toString() {
        return songName;
    }
    public String getURL() { return url; }

}