package com.bulb.bulb.Models;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.ForumActivity;
import com.bulb.bulb.MainActivity;
import com.bulb.bulb.MapTherapistDetailActivity;
import com.bulb.bulb.R;

import java.util.ArrayList;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Comment> comments;
    private Context mContext;
    private User user;

    public CommentsRecyclerAdapter(Context context, ArrayList<Comment> comments, User user) {
        mContext = context;
        this.user = user;
        this.comments = comments;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_cell, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        Log.d("Bulb", "onBindViewHolder: called. ADDING COMMENT");
        Comment comment = comments.get(i);
        ((ViewHolder) viewHolder).usernameTextView.setText(comment.getUsername() + (comment.getUsername().equals(user.getUsername()) ? " (you)" : "" ));
        ((ViewHolder) viewHolder).timeTextView.setText(comment.getDate());
        ((ViewHolder) viewHolder).contentTextView.setText(comment.getContent());
        if(comment.getTherapist() != null) {
            ((ViewHolder) viewHolder).therapistNameTextView.setText(comment.getTherapist().name);
            ((ViewHolder) viewHolder).therapistPhoneTextView.setText((comment.getTherapist().phone != null && comment.getTherapist().phone.length() > 1 ? comment.getTherapist().phone : "N/A"));
            ((ViewHolder) viewHolder).viewMoreTextView.setText((comment.getTherapist().address != null && comment.getTherapist().address.length() > 1 ? comment.getTherapist().address : "N/A"));
            ((ViewHolder) viewHolder).websiteTextView.setText((comment.getTherapist().website != null && comment.getTherapist().website.length() > 1 ? comment.getTherapist().website : "N/A"));
            ((ViewHolder) viewHolder).insuranceTextView.setText((comment.getTherapist().insurances != null && !comment.getTherapist().insurances.isEmpty() &&!(comment.getTherapist().insurances.toString().equals("[]")) ? getInsurances(comment.getTherapist().insurances) : "N/A"));
            Log.d("Bulb", "THERAPIST ASSOCIATED: " + comment.getTherapist().name + comment.getTherapist().phone);
        } else {
            ((ViewHolder) viewHolder).reviewContainer.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).therapistLabel.setVisibility(View.GONE);
        }
    }

    public String getInsurances(ArrayList<String> insurances) {
        String insurancesString = "";
        for(String insurance : insurances) {
            if(!insurance.equals("") && !insurance.equals(" ")) {
                insurancesString = insurancesString + insurance.replaceAll(" ", "") + ", ";
            }
        }
        return insurancesString.length() > 3 ? insurancesString.substring(0, insurancesString.length()-2) : "N/A";
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        TextView timeTextView;
        TextView contentTextView;
        ImageView user_icon;
        TextView therapistNameTextView;
        TextView viewMoreTextView;
        ConstraintLayout reviewContainer;
        TextView therapistPhoneTextView;
        TextView therapistLabel;
        TextView insuranceTextView;
        TextView websiteTextView;
        TextView insuranceLabel;
        TextView websiteLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            user_icon = itemView.findViewById(R.id.user_icon);
            therapistLabel = itemView.findViewById(R.id.therapist_label);
            therapistNameTextView = itemView.findViewById(R.id.comment_therapist_name);
            viewMoreTextView = itemView.findViewById(R.id.comment_therapist_view_detail);
            reviewContainer = itemView.findViewById(R.id.review_container);
            therapistPhoneTextView = itemView.findViewById(R.id.comment_therapist_phone);
            insuranceTextView = itemView.findViewById(R.id.comment_therapist_insurance);
            websiteTextView = itemView.findViewById(R.id.comment_therapist_website);
            insuranceLabel = itemView.findViewById(R.id.insurance_label);
            websiteLabel = itemView.findViewById(R.id.website_label);
        }
    }
}


