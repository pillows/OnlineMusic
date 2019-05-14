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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
    String personId;
    /*
    getFileName(Uri) code taken from here
    https://stackoverflow.com/a/27926504
    by cinthiaro
     */
    public String getFileName(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());

        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
              personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
        }

        Log.d("accountid ", personId);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        for(int i = 0; i < files.size(); i++){
            Log.d("uris ", files.get(i).toString());
            Log.d("names ", names.get(i));

            Log.d("path ", personId + "/" + names.get(i));

        }
        final StorageReference riversRef = mStorageRef.child(personId + "/music.mp3");

//        riversRef.putFile(uri)
//            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Get a URL to the uploaded content
////                                Uri downloadUrl = taskSnapshot.getDownloadUri();
//                    Log.d("asda", riversRef.getDownloadUrl().toString());
//                }
//            })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    // ...
//                }
//            });
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
                else{



                    for(int i = 0; i < data.getClipData().getItemCount(); i++){

                        uri = data.getClipData().getItemAt(i).getUri();
                        String filename = getFileName(uri);
                        Log.d("URI ", getFileName(uri));
                        files.add(i, uri);
                        names.add(i, filename);
                    }
                }

                uploadFiles(files, names);

                //                Log.d("URI ", getFileName(uri));



                mStorageRef = FirebaseStorage.getInstance().getReference();
                final StorageReference riversRef = mStorageRef.child("music/music.mp3");


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