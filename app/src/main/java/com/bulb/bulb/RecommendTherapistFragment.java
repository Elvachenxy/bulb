package com.bulb.bulb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bulb.bulb.Models.Therapist;
import com.bulb.bulb.Models.User;
import com.bulb.bulb.Models.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.UUID;

public class RecommendTherapistFragment extends Fragment {
    public Button doneButton;
    public ImageView exitButton;
    public EditText nameET;
    public EditText phoneET;
    public EditText websiteET;
    public EditText addrET;
    public EditText insuranceET;
    public EditText contentET;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recommend_therapist, container, false);
        nameET = view.findViewById(R.id.recommend_name);
        nameET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameET.setSingleLine(true);
        phoneET = view.findViewById(R.id.recommend_phone);
        phoneET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phoneET.setSingleLine(true);
        websiteET = view.findViewById(R.id.recommend_website);
        websiteET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        websiteET.setSingleLine(true);
        addrET = view.findViewById(R.id.recommend_address);
        addrET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addrET.setSingleLine(true);
        insuranceET = view.findViewById(R.id.recommend_insurance);
        insuranceET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        insuranceET.setSingleLine(true);
        contentET = view.findViewById(R.id.recommend_content);
        contentET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        contentET.setSingleLine(true);
        doneButton = view.findViewById(R.id.recommend_done_button);
        exitButton = view.findViewById(R.id.recommend_exit_button);
        return view;
    }

}
