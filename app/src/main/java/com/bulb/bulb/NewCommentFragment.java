package com.bulb.bulb;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.Models.Therapist;

import static android.app.Activity.RESULT_OK;

public class NewCommentFragment extends Fragment {


    public ImageView userIconImageView;
    public ImageView exitButton;
    public Button postButton;
    public EditText commentEditText;
    public TextView recommendTherapist;
    public ConstraintLayout recommendContainer;
    public ConstraintLayout therapistContainer;
    TextView therapistNameTextView;
    TextView viewMoreTextView;
    TextView therapistPhoneTextView;
    TextView therapistLabel;
    TextView insuranceTextView;
    TextView websiteTextView;
    TextView insuranceLabel;
    TextView websiteLabel;
    ImageView editTherapist;
    public Therapist therapist;

    public NewCommentFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_comment, container, false);
        userIconImageView = view.findViewById(R.id.user_icon);
        exitButton = view.findViewById(R.id.exit_button);
        postButton = view.findViewById(R.id.post_button);
        commentEditText = view.findViewById(R.id.main_edit_text);
        recommendTherapist = view.findViewById(R.id.recommend_therapist);
        recommendContainer = view.findViewById(R.id.recommend_therapist_container);
        therapistNameTextView = view.findViewById(R.id.comment_therapist_name);
        viewMoreTextView = view.findViewById(R.id.comment_therapist_view_detail);
        therapistContainer = view.findViewById(R.id.review_container);
        therapistPhoneTextView = view.findViewById(R.id.comment_therapist_phone);
        insuranceTextView = view.findViewById(R.id.comment_therapist_insurance);
        websiteTextView = view.findViewById(R.id.comment_therapist_website);
        insuranceLabel = view.findViewById(R.id.insurance_label);
        websiteLabel = view.findViewById(R.id.website_label);
        editTherapist = view.findViewById(R.id.edit_therapist);
        return view;
    }
}
