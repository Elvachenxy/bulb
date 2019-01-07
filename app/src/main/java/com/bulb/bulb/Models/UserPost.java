package com.bulb.bulb.Models;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class UserPost implements Serializable {

    ArrayList<String> tags;
    ArrayList<String> upvoters;
    String username;
    ArrayList<Comment> comments;
    int upvote;
    String content; //200 char max
    Date date;
    UUID uuid;
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();


    public UserPost(String usernameInput, String contentInput, ArrayList<String> tagList) {
        username = usernameInput;
        comments = new ArrayList<>();
        upvote = 0;
        content = contentInput;
        date = new Date();
        tags = new ArrayList<>();
        upvoters = new ArrayList<>();
        for(String tag : tagList) {
            tags.add(tag.toLowerCase());
        }
        tags.add("all");
        uuid = UUID.randomUUID();
    }

    public static ArrayList<UserPost> filter(ArrayList<String> tags, ArrayList<UserPost> posts) {
        ArrayList<UserPost> filteredPosts = new ArrayList<>();
        for(UserPost post : posts) {
            for(String tag : tags) {
                if(post.tags.contains(tag.toLowerCase())) {
                    filteredPosts.add(post);
                }
            }
        }
        return filteredPosts;
    }

    public void pushUpdateToDatabase(){
        String path = "posts/" + uuid;
        StorageReference postRef = storage.getReference(path);
        byte[] data = SerializationUtils.serialize(this);
        UploadTask uploadTask = postRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Bulb", "Updated post at database.");
            }
        });
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mma");
        return format.format(date);
    }
    public Date getRawDate() {
        return date;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public int getUpvote() {
        return upvote;
    }

    public void incrementUpvote(User user) {
        upvote = upvote + 1;
        upvoters.add(user.getUsername());
        pushUpdateToDatabase();
    }

    public void decrementUpvote(User user) {
        upvote = upvote - 1;
        upvoters.remove(user.getUsername());
        pushUpdateToDatabase();
    }

    public String getContent() {
        return content;
    }

    public UUID getUuid() {return uuid;}

    public ArrayList<String> getUpvoters() {
        return upvoters;
    }
}
