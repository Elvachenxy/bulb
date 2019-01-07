package com.bulb.bulb.Models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.R;

import java.util.ArrayList;

public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Comment> reviews;
    private Context mContext;
    private Therapist therapist;


    public ReviewsRecyclerAdapter(Context context, Therapist t) {
        mContext = context;
        this.therapist = t;
        this.reviews = t.reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.review_cell, viewGroup, false);
        ReviewsRecyclerAdapter.ViewHolder viewHolder = new ReviewsRecyclerAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Comment review = reviews.get(i);
        ((ReviewsRecyclerAdapter.ViewHolder) viewHolder).contentTV.setText(review.content);
        ((ViewHolder) viewHolder).usernameTV.setText(review.username);
        ((ViewHolder) viewHolder).contentTV.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView contentTV;
        TextView usernameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.review_username);
            contentTV = itemView.findViewById(R.id.review_content);
        }
    }
}
