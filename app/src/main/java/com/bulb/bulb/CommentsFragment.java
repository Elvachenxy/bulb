package com.bulb.bulb;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.Models.CommentsRecyclerAdapter;
import com.bulb.bulb.Models.Therapist;
import com.bulb.bulb.Models.User;
import com.bulb.bulb.Models.UserPost;

public class CommentsFragment extends Fragment {


    public TextView newCommentButton;
    public ImageView profileImageView;
    public RecyclerView commentsRecycler;
    public View.OnClickListener listener;
    public CommentsRecyclerAdapter adapter;
    public User user;
    public UserPost post;
    public CommentsFragment() {

    }

    public void setOnClickListener(View.OnClickListener listener) {newCommentButton.setOnClickListener(listener);}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_comments_fragment, container, false);
        newCommentButton = view.findViewById(R.id.answerTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        commentsRecycler = view.findViewById(R.id.commentsRecyclerView);

        commentsRecycler.setAdapter(adapter);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        newCommentButton.setOnClickListener(listener);
        if(post.getUsername().equals(user.getUsername())) {
            view.findViewById(R.id.lineBreakBottom).setVisibility(View.GONE);
            newCommentButton.setVisibility(View.GONE);
            profileImageView.setVisibility(View.GONE);
        }

        return view;
    }

}
