package com.bulb.bulb.Models;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Therapist implements Serializable {

    public String gIdentity;

    public String name;
    public int mentions;

    public String phone;

    public String address;
    public String id;

    public ArrayList<String> insurances;

    public Location location;
    public String website;
    public ArrayList<Comment> reviews;

    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    public Therapist(String nameInput, String phoneInput, String addressInput, String insuranceInput, String contentInput, User user) {
        mentions = 0;
        reviews = new ArrayList<>();
        name = nameInput;
        phone = phoneInput;
        address = addressInput;
        id = name.replaceAll(" ", "_").replaceAll("[.]","").toLowerCase();

        //imp how to parse insurances
        insurances = new ArrayList<>();
        insurances.add(insuranceInput);
        //imp how to add reviews
        reviews.add(new Comment(user.getUsername(), contentInput));

    }

    public void pushUpdateToDatabase() {
        String path = "therapists/" + id;
        StorageReference postRef = storage.getReference(path);
        byte[] data = SerializationUtils.serialize(this);
        UploadTask uploadTask = postRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Bulb", "Updated therapist at database.");
            }
        });
    }

}
