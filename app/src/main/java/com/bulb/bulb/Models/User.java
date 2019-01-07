package com.bulb.bulb.Models;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class User  implements Serializable {
    String username;
    String password;
    ArrayList<UUID> userPosts;
    public ArrayList<UUID> followingPosts;
    UUID uuid;
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    public User(String name, String password) {
        username = name;
        this.password = password;
        userPosts = new ArrayList<>();
        followingPosts = new ArrayList<>();

    }

    public ArrayList<UUID> getUserPosts() {
        return userPosts;
    }

    public void pushUpdateToDatabase(){
        String path = "users/" + username;
        StorageReference userRef = storage.getReference(path);
        byte[] data = SerializationUtils.serialize(this);
        UploadTask uploadTask = userRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Bulb", "Updated user at database.");
            }
        });
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public void registerPost(UserPost post) {
        userPosts.add(post.getUuid());
        pushUpdateToDatabase();
    }

    public void follow(UserPost post) {
        followingPosts.add(post.getUuid());
        pushUpdateToDatabase();
    }

    public void unfollow(UserPost post) {
        followingPosts.remove(post.getUuid());
        pushUpdateToDatabase();
    }
}
