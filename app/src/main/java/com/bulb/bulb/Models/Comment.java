package com.bulb.bulb.Models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Comment implements Serializable{

    String username;
    int upvote;
    int followers;
    String content; //200 char max
    String date;
    Therapist therapist;

    public Comment(String usernameInput, String contentInput) {
        username = usernameInput;
        upvote = 0;
        followers = 0;
        content = contentInput;
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mma");
        date = format.format(new Date());
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public int getFollowers() {
        return followers;
    }

    public int getUpvote() {
        return upvote;
    }

    public void incrementUpvote() {
        upvote = upvote + 1;
    }

    public String getContent() {
        return content;
    }

    public Therapist getTherapist() {
        return therapist;
    }

    public void setTherapist(Therapist therapist) {
        this.therapist = therapist;
    }

}
