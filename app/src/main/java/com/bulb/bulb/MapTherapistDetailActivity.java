package com.bulb.bulb;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.Models.ReviewsRecyclerAdapter;
import com.bulb.bulb.Models.Therapist;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

public class MapTherapistDetailActivity extends Fragment {

    TextView nameTV;
    TextView detailTV;
    TextView reviewsHeaderTV;
    ReviewsRecyclerAdapter reviewAdapter;
    RecyclerView recyclerView;
    ImageView exitButton;
    TextView insuranceTV;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                      Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_therapist_detail, container, false);
        nameTV = view.findViewById(R.id.therapist_detail_name);
        detailTV = view.findViewById(R.id.therapist_detail_description);
        reviewsHeaderTV = view.findViewById(R.id.therapist_detail_review_header);
        insuranceTV = view.findViewById(R.id.therapist_detail_insurance);
        recyclerView = view.findViewById(R.id.review_container);
        exitButton = view.findViewById(R.id.exit_button);
        return view;
    }

}
