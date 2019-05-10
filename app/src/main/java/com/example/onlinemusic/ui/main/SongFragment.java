package com.example.onlinemusic.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.onlinemusic.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class SongFragment extends Fragment {
    private StorageReference mStorageRef;
    private static final int FILE_SELECT_CODE = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();

                Log.d("test", uri.toString());
                mStorageRef = FirebaseStorage.getInstance().getReference();
                final StorageReference riversRef = mStorageRef.child("music/music.mp3");

                riversRef.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
//                                Uri downloadUrl = taskSnapshot.getDownloadUri();
                                Log.d("asda", riversRef.getDownloadUrl().toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        FloatingActionButton tambahpendor = view.findViewById(R.id.fab);

        tambahpendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,1);



                Log.d("asdsa", " asdasd");


//                Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//                try {
//                    startActivityForResult(
//                            Intent.createChooser(intent, "Select a File to Upload"),
//                            FILE_SELECT_CODE);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    // Potentially direct the user to the Market with a Dialog
////                    Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        return view;
    }

}