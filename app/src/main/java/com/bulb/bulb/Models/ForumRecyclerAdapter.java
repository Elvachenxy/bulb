package com.bulb.bulb.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.ForumActivity;
import com.bulb.bulb.MainActivity;
import com.bulb.bulb.NewPostActivity;
import com.bulb.bulb.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ForumRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String defaultTextColor = "#808080";
    private static final String active = "#ffa500";
    private static final String inactive = "#d3d3d3";
    private static final String following = "#6495ed";

    private ArrayList<UserPost> posts;
    private Context mContext;
    private User user;

    public ForumRecyclerAdapter(Context context, ArrayList<UserPost> posts, User user) {
        mContext = context;
        this.user = user;
        this.posts = posts;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_card, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        Log.d("Bulb", "onBindViewHolder: called.");
        //((ViewHolder) viewHolder).user_icon =
        final UserPost post = posts.get(i);
        ((ViewHolder) viewHolder).username.setText(post.getUsername() + (post.getUsername().equals(user.getUsername()) ? " (you)" : "" ));
        ((ViewHolder) viewHolder).time.setText(post.getDate());
        ((ViewHolder) viewHolder).content.setText(post.getContent());

        drawUpvote(viewHolder, post);
        drawFollow(viewHolder, post);
        ((ViewHolder) viewHolder).upvoteBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUpvoters().contains(user.getUsername()) && !post.getUsername().equals(user.getUsername())) {
                    post.incrementUpvote(user);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    post.decrementUpvote(user);
                }
                drawUpvote(viewHolder, post);
            }
        });
        /*((ViewHolder) viewHolder).upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUpvoters().contains(user.getUsername()) && !post.getUsername().equals(user.getUsername())) {
                    post.incrementUpvote(user);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    post.decrementUpvote(user);
                }
                drawUpvote(viewHolder, post);
            }
        });*/
        ((ViewHolder) viewHolder).followBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUsername().equals(user.getUsername()) && !user.followingPosts.contains(post.getUuid())) {
                    user.follow(post);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    user.unfollow(post);
                }
                drawFollow(viewHolder, post);
            }
        });
        /*((ViewHolder) viewHolder).follow_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUsername().equals(user.getUsername()) && !user.followingPosts.contains(post.getUuid())) {
                    user.follow(post);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    user.unfollow(post);
                }
                drawFollow(viewHolder, post);
            }
        });*/
        ((ViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Bulb", "onClickListener: called:"+ post);
                Intent intent = new Intent(mContext, ForumActivity.class);

                intent.putExtra("post", post);
                intent.putExtra("user", user);
                mContext.startActivity(intent);
            }
        });
    }

    private void drawUpvote(RecyclerView.ViewHolder viewHolder, UserPost post) {
        ((ViewHolder) viewHolder).upvote.setText("Upvote - " + post.getUpvote());
        if(post.getUpvoters().contains(user.getUsername())) {
            ((ViewHolder) viewHolder).upvote.setTextColor(Color.parseColor(active));
            ((ViewHolder) viewHolder).upvote_icon.setColorFilter(Color.parseColor(active));
        } else if(post.getUsername().equals(user.getUsername())) {
            ((ViewHolder) viewHolder).upvote.setTextColor(Color.parseColor(inactive));
            ((ViewHolder) viewHolder).upvote_icon.setColorFilter(Color.parseColor(inactive));
        } else {
            ((ViewHolder) viewHolder).upvote.setTextColor(Color.parseColor(defaultTextColor));
            ((ViewHolder) viewHolder).upvote_icon.setColorFilter(Color.parseColor(defaultTextColor));
        }
    }
    private void drawFollow(RecyclerView.ViewHolder viewHolder, UserPost post) {
        if(user.followingPosts.contains(post.getUuid())) {
            ((ViewHolder) viewHolder).follow.setTextColor(Color.parseColor(following));
            ((ViewHolder) viewHolder).follow_icon.setColorFilter(Color.parseColor(following));
            ((ViewHolder) viewHolder).follow.setText("Unfollow");
        } else if(post.getUsername().equals(user.getUsername())) {
            ((ViewHolder) viewHolder).follow.setTextColor(Color.parseColor(inactive));
            ((ViewHolder) viewHolder).follow_icon.setColorFilter(Color.parseColor(inactive));
            ((ViewHolder) viewHolder).follow.setText("Follow");
        } else {
            ((ViewHolder) viewHolder).follow.setTextColor(Color.parseColor(defaultTextColor));
            ((ViewHolder) viewHolder).follow_icon.setColorFilter(Color.parseColor(defaultTextColor));
            ((ViewHolder) viewHolder).follow.setText("Follow");
        }
    }



    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView user_icon;
        ImageView upvote_icon;
        ImageView follow_icon;
        TextView username;
        TextView time;
        TextView content;
        TextView upvote;
        TextView follow;
        View upvoteBox;
        View followBox;
        ConstraintLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            upvote_icon = itemView.findViewById(R.id.upvoteImageView);
            follow_icon = itemView.findViewById(R.id.followImageView);
            username = itemView.findViewById(R.id.username_textView);
            time = itemView.findViewById(R.id.time_textView);
            content = itemView.findViewById(R.id.content_textView);
            upvote = itemView.findViewById(R.id.upvoteCountTextView);
            container = itemView.findViewById(R.id.container);
            follow = itemView.findViewById(R.id.followTextView);
            upvoteBox = itemView.findViewById(R.id.upvote_box);
            followBox = itemView.findViewById(R.id.follow_box);
        }
    }

    public void setPosts(ArrayList<UserPost> posts) {
        this.posts = posts;
    }
}
