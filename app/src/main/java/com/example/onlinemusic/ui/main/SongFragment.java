package com.example.onlinemusic.ui.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SongFragment extends Fragment {
    private StorageReference mStorageRef;
    private static final int FILE_SELECT_CODE = 0;

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK) {





                Uri uri_test = data.getData();
//                Log.d("URI ", getFileName(uri));

                ArrayList<Uri> files = new ArrayList<Uri>();
                ArrayList<String> names = new ArrayList<String>();

                // Check to see if multiple files. If it's singular file then it will return null
                // if(data.getClipData() == null)
                for(int i = 0; i < data.getClipData().getItemCount(); i++){

                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    files.add(i, uri);
                    Log.d("URI ", getFileName(uri));
                }

//                mStorageRef = FirebaseStorage.getInstance().getReference();
//                final StorageReference riversRef = mStorageRef.child("music/music.mp3");
//
//                riversRef.putFile(uri)
//                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                // Get a URL to the uploaded content
////                                Uri downloadUrl = taskSnapshot.getDownloadUri();
//                                Log.d("asda", riversRef.getDownloadUrl().toString());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                // Handle unsuccessful uploads
//                                // ...
//                            }
//                        });
            }
//            }
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
                intent_upload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent_upload.setAction(Intent.ACTION_OPEN_DOCUMENT);
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);



                startActivityForResult(intent_upload,1);






            }
        });

        return view;
    }

}