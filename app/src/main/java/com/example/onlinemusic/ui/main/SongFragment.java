package com.example.onlinemusic.ui.main;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.onlinemusic.MusicController;
import com.example.onlinemusic.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.widget.MediaController.MediaPlayerControl;

import com.example.onlinemusic.SongList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SongFragment extends Fragment{
    private StorageReference mStorageRef;
    private static final int FILE_SELECT_CODE = 0;
    String personId = "";
    private DatabaseReference mDatabase;
    SongList song;
    int counter = 0;
    ArrayList<String> urls = new ArrayList<String>();

    ArrayList<Uri> filesArray = new ArrayList<Uri>();
    ArrayList<String> namesArray = new ArrayList<String>();
    ArrayList<SongList> songsList = new ArrayList<SongList>();
    ListView list;

//
//    private MusicController controller;
//
//    //play next
//    private void playNext(){
//        musicSrv.playNext();
//        controller.show(0);
//    }
//
//    //play previous
//    private void playPrev(){
//        musicSrv.playPrev();
//        controller.show(0);
//    }
//
//    @Override
//    public void pause() {
//        musicSrv.pausePlayer();
//    }
//
//    @Override
//    public void seekTo(int pos) {
//        musicSrv.seek(pos);
//    }
//
//    @Override
//    public void start() {
//        musicSrv.go();
//    }

    public void getAllSongs(){

        Log.d("personid ", personId);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference songRef = rootRef.child(personId+"/songs");
        songsList.clear();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("array ", songsList.toString());
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    SongList sl = new SongList();

                    String songName = ds.child("songName").getValue().toString();

                    String url = ds.child("url").getValue().toString();
                    String playlistName = ds.child("playlistName").getValue().toString();

                    sl.songName = songName;
                    sl.url = url;
                    sl.playlistName = playlistName;
                    songsList.add(sl);
                    Log.d("url song", url);
                }

                ArrayAdapter adapter = new ArrayAdapter<SongList>(getActivity(),R.layout.songlist, songsList);
                ListView list = (ListView)getView().findViewById(R.id.songlist);
                list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        songRef.addListenerForSingleValueEvent(eventListener);

        Log.d("array ", songsList.toString());
//        return songsList;
    }
    /*
    getFileName(Uri) code taken from here
    https://stackoverflow.com/a/27926504
    by cinthiaro
     */
    public String getFileName(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    return displayName;
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
            return displayName;
        }
        return null;
    }


    public void uploadFiles(ArrayList<Uri> files, ArrayList<String> names){



        // Loop through all the files and upload them
        for(int i = 0; i < files.size(); i++){


            // Use for storage
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            namesArray = names;
            filesArray = files;

            final StorageReference riversRef = mStorageRef.child(personId + "/" + names.get(i));

            riversRef.putFile(files.get(i))


                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    song = new SongList();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    mDatabase = FirebaseDatabase.getInstance().getReference("/"+personId+"/songs").push();

                                    // Creating the song object with the name, url, and playlist name
                                    song.songName = namesArray.get(counter);
                                    song.playlistName = "";
                                    song.url = uri.toString();
                                    counter++;

                                    // Finally adding the song object to Realtime Database
                                    mDatabase.setValue(song);
                                    getAllSongs();
                                }
                            });
                        }
                    });


        }
        //  It's stupid but resetting the counter to 0 is neccessary
        //  i cannot be accessed in the call abovee
        getAllSongs();
        counter = 0;
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK) {
                ArrayList<Uri> files = new ArrayList<Uri>();
                ArrayList<String> names = new ArrayList<String>();
                Uri uri;
                // Check to see if multiple files. If it's singular file then it will return null
                if(data.getClipData() == null){
                    uri = data.getData();
                    String filename = getFileName(uri);
                    files.add(0, uri);
                    names.add(0, filename);
                }

                // In this case we know there are multiple files and we'll have to retrieve
                // differently.
                // In a single file we can just getData() but for multiple files
                // we have to use getClipData()
                else{


                    // Populating the array with file uri and names of the songs
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        uri = data.getClipData().getItemAt(i).getUri();
                        String filename = getFileName(uri);
                        files.add(i, uri);
                        names.add(i, filename);
                    }
                }

                uploadFiles(files, names);


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void stopPlaying(MediaPlayer mp) {
        mp.stop();
        mp.release();
        mp = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());

        // Going to use the ID of the account as the identifier for songs and playlists
        if (acct != null) {
            personId = acct.getId();
        }
        // Populate the list of songs as soon as view is created
        getAllSongs();

        ListView list = (ListView)view.findViewById(R.id.songlist);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("pos" , songsList.get(position).getURL());

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);





                try {
                    mediaPlayer.setDataSource(songsList.get(position).getURL());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                else{
                    mediaPlayer.start();
                }

            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fab);

        // Making an intent to start going to the file explorer and picking songs
        // There's the choice of single or multiple files
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent_upload.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intent_upload,1);

            }
        });



        return view;
    }

}