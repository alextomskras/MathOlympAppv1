package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.regex.Pattern;

public class FirebaseImageLoader {

    private final String searchImagesPath;
    private final ImageView imageView;
    private final String zadachaId;
    private final StorageReference storageRef;
    private final ScrollingFragment scrollingFragment = new ScrollingFragment();
    private List<String> localListFiles;

    public FirebaseImageLoader(String searchImagesPath, ImageView imageView, List<String> localListFiles, String zadachaId) {
        this.searchImagesPath = searchImagesPath;
        this.imageView = imageView;
        this.localListFiles = localListFiles;
        this.zadachaId = zadachaId;
        this.storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void setFirebaseImage(String searchImagesPath) {

        if (searchImagesPath.equals("answersimages")) {
            localListFiles = scrollingFragment.listFilesFirestore;
        } else {
            localListFiles = scrollingFragment.listSolutionFilesFirestore;
        }

        StorageReference spaceRef = storageRef.child("answersimages/answer" + zadachaId);
        spaceRef.getName();
        spaceRef.getMetadata();

        Object splitString = localListFiles.get(Integer.parseInt(zadachaId) - 1);
        String[] parts = ((String) splitString).split(Pattern.quote("/"));
        String imageLoad = parts[4];
        String imagePatch = searchImagesPath + "/" + imageLoad;

        storageRef.child(searchImagesPath + "/" + imageLoad).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(scrollingFragment.getContext())
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            Log.d(ScrollingFragment.TAG, "____DATE= " + "storageRef");
        });
    }
}
