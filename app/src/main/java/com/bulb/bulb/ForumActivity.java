package com.bulb.bulb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.Models.Comment;
import com.bulb.bulb.Models.CommentsRecyclerAdapter;
import com.bulb.bulb.Models.FilterRecyclerAdapter;
import com.bulb.bulb.Models.ForumRecyclerAdapter;
import com.bulb.bulb.Models.Therapist;
import com.bulb.bulb.Models.User;
import com.bulb.bulb.Models.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.UUID;

public class ForumActivity extends AppCompatActivity {

    private static final String defaultTextColor = "#808080";
    private static final String active = "#ffa500";
    private static final String inactive = "#d3d3d3";
    private static final String following = "#6495ed";

    TextView contentTV;
    ImageView userIconIV;
    ImageView posterIconIV;
    TextView usernameTV;
    TextView dateTimeTV;
    TextView answerTV;
    TextView noAnswerTV;
    ImageView upvoteIV;
    TextView upvoteCountTV;
    ImageView followIV;
    TextView followCountTV;
    UserPost post;
    Button backToMainButton;
    private ConstraintLayout fragmentContainer;
    private ConstraintLayout newCommentFragmentContainer;
    CommentsFragment commentsFragment;
    NewCommentFragment newCommentFragment;
    RecyclerView recyclerView;
    CommentsRecyclerAdapter adapter;
    User user;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RecommendTherapistFragment recommendTherapistFragment;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        post = (UserPost) getIntent().getSerializableExtra("post");
        user = (User) getIntent().getSerializableExtra("user");
        usernameTV = findViewById(R.id.usernameTextView);
        usernameTV.setText(post.getUsername() + (post.getUsername().equals(user.getUsername()) ? " (you)" : "" ));
        contentTV = findViewById(R.id.contentsTextView);
        contentTV.setText(post.getContent());
        dateTimeTV = findViewById(R.id.dateTimeTextView);
        dateTimeTV.setText(post.getDate());
        backToMainButton = findViewById(R.id.backToMainButton);
        appBarLayout = findViewById(R.id.appBarLayout);
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForumActivity.this, MainActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        upvoteCountTV = findViewById(R.id.upvoteCountTextView);
        upvoteIV = findViewById(R.id.upvoteImageView);
        followCountTV = findViewById(R.id.followTextView);
        followIV = findViewById(R.id.followImageView);
        drawFollow();
        drawUpvote();
        findViewById(R.id.upvote_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUpvoters().contains(user.getUsername()) && !post.getUsername().equals(user.getUsername())) {
                    post.incrementUpvote(user);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    post.decrementUpvote(user);
                }
                drawUpvote();
            }
        });
        /*upvoteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUpvoters().contains(user.getUsername()) && !post.getUsername().equals(user.getUsername())) {
                    post.incrementUpvote(user);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    post.decrementUpvote(user);
                }
                drawUpvote();
            }
        });*/
        findViewById(R.id.follow_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUsername().equals(user.getUsername()) && !user.followingPosts.contains(post.getUuid())) {
                    user.follow(post);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    user.unfollow(post);
                }
                drawFollow();
            }
        });
        /*followIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getUsername().equals(user.getUsername()) && !user.followingPosts.contains(post.getUuid())) {
                    user.follow(post);
                } else if(!post.getUsername().equals(user.getUsername())) {
                    user.unfollow(post);
                }
                drawFollow();
            }
        });*/

        adapter = new CommentsRecyclerAdapter(this, post.getComments(), user);
        fragmentContainer = findViewById(R.id.commentContainer);
        newCommentFragmentContainer = findViewById(R.id.newCommentContainer);
        commentsFragment = new CommentsFragment();
        commentsFragment.adapter = adapter;
        commentsFragment.user = user;
        commentsFragment.post = post;
        displayCommentsFragment();

        newCommentFragment = new NewCommentFragment();
        newCommentFragmentContainer.setVisibility(View.GONE);
    }

    public void openRecommendTherapist() {
        FragmentManager manager = getSupportFragmentManager();
        recommendTherapistFragment = new RecommendTherapistFragment();
        newCommentFragmentContainer.setVisibility(View.VISIBLE);
        manager.beginTransaction().replace(R.id.newCommentContainer, recommendTherapistFragment, recommendTherapistFragment.getTag()).commit();
        getSupportFragmentManager().executePendingTransactions();
        recommendTherapistFragment.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Bulb", "Closing therapist fragment");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                displayNewCommentFragment(null, null);
            }
        });
        recommendTherapistFragment.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Bulb", "Adding Therapist recommendation");
                if (!recommendTherapistFragment.nameET.getText().toString().equals("")) {
                    String name = recommendTherapistFragment.nameET.getText().toString();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    checkDataBase(name, recommendTherapistFragment);
                }
            }
        });
    }

    public void openRecommendTherapistForExistingFragment(final RecommendTherapistFragment rtf, final Therapist therapist) {
        FragmentManager manager = getSupportFragmentManager();
        recommendTherapistFragment = rtf;
        newCommentFragmentContainer.setVisibility(View.VISIBLE);
        manager.beginTransaction().replace(R.id.newCommentContainer, recommendTherapistFragment, recommendTherapistFragment.getTag()).commit();
        getSupportFragmentManager().executePendingTransactions();
        recommendTherapistFragment.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Bulb", "Closing therapist fragment");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                displayNewCommentFragment(therapist, rtf);
            }
        });
        recommendTherapistFragment.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Bulb", "Adding Therapist recommendation");
                if (!recommendTherapistFragment.nameET.getText().toString().equals("")) {
                    String name = recommendTherapistFragment.nameET.getText().toString();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    checkDataBase(name, recommendTherapistFragment);
                }
            }
        });
    }

    private void drawUpvote() {
        upvoteCountTV.setText("Upvote - " + post.getUpvote());
        if(post.getUpvoters().contains(user.getUsername())) {
            upvoteCountTV.setTextColor(Color.parseColor(active));
            upvoteIV.setColorFilter(Color.parseColor(active));
        } else if(post.getUsername().equals(user.getUsername())) {
            upvoteCountTV.setTextColor(Color.parseColor(inactive));
            upvoteIV.setColorFilter(Color.parseColor(inactive));
        } else {
            upvoteCountTV.setTextColor(Color.parseColor(defaultTextColor));
            upvoteIV.setColorFilter(Color.parseColor(defaultTextColor));
        }
    }
    private void drawFollow() {
        if(user.followingPosts.contains(post.getUuid())) {
            followCountTV.setTextColor(Color.parseColor(following));
            followIV.setColorFilter(Color.parseColor(following));
            followCountTV.setText("Unfollow");
        } else if(post.getUsername().equals(user.getUsername())) {
            followCountTV.setTextColor(Color.parseColor(inactive));
            followIV.setColorFilter(Color.parseColor(inactive));
            followCountTV.setText("Follow");
        } else {
            followCountTV.setTextColor(Color.parseColor(defaultTextColor));
            followIV.setColorFilter(Color.parseColor(defaultTextColor));
            followCountTV.setText("Follow");
        }
    }

    private void displayCommentsFragment() {
        FragmentManager manager = getSupportFragmentManager();
        fragmentContainer.setVisibility(View.VISIBLE);
        commentsFragment.listener = (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Bulb", "Transitioning to new post");
                displayNewCommentFragment(null, null);
            }
        });
        manager.beginTransaction().replace(R.id.commentContainer, commentsFragment, commentsFragment.getTag()).commit();
    }

    private void displayNewCommentFragment(final Therapist therapist, final RecommendTherapistFragment rtf) {
        FragmentManager manager = getSupportFragmentManager();
        newCommentFragmentContainer.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.GONE);
        manager.beginTransaction().replace(R.id.newCommentContainer, newCommentFragment, newCommentFragment.getTag()).commit();
        getSupportFragmentManager().executePendingTransactions();
        newCommentFragment.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Log.d("Bulb", "Closing new comment Fragment");
                    newCommentFragmentContainer.setVisibility(View.GONE);
                    appBarLayout.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newCommentFragment.commentEditText.getWindowToken(), 0);
                    newCommentFragment = new NewCommentFragment();
            }
        });
        newCommentFragment.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newCommentFragment.commentEditText.getText().toString().equals("")) {
                    Log.d("Bulb", "Posting Comment Fragment");
                    postComment(therapist);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newCommentFragment.commentEditText.getWindowToken(), 0);
                }
            }
        });
        newCommentFragment.recommendTherapist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Bulb", "Opening Recommend Therapist View");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newCommentFragment.commentEditText.getWindowToken(), 0);
                openRecommendTherapist();
            }
        });
        if(therapist != null) {
            newCommentFragment.therapistNameTextView.setText(therapist.name);
            newCommentFragment.therapistPhoneTextView.setText((therapist.phone != null && therapist.phone.length() > 1 ? therapist.phone : "N/A"));
            newCommentFragment.viewMoreTextView.setText((therapist.address != null && therapist.address.length() > 1 ? therapist.address : "N/A"));
            newCommentFragment.websiteTextView.setText((therapist.website != null && therapist.website.length() > 1 ? therapist.website : "N/A"));
            newCommentFragment.insuranceTextView.setText((therapist.insurances != null && !therapist.insurances.isEmpty() &&!(therapist.insurances.toString().equals("[]")) ? getInsurances(therapist.insurances) : "N/A"));
            Log.d("Bulb", "THERAPIST ASSOCIATED: " + therapist.name + therapist.phone);
            newCommentFragment.recommendTherapist.setVisibility(View.GONE);
            newCommentFragment.editTherapist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Bulb", "Opening Recommend Therapist View");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(newCommentFragment.commentEditText.getWindowToken(), 0);
                    openRecommendTherapistForExistingFragment(rtf, therapist);
                }
            });
        } else {
            newCommentFragment.therapistContainer.setVisibility(View.GONE);
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

    public void postComment(final Therapist therapist) {
        if (!newCommentFragment.commentEditText.getText().toString().equals("")) {
            Comment comment = new Comment(user.getUsername(), newCommentFragment.commentEditText.getText().toString());
            if(therapist != null) {
                Log.d("Bulb", "Comment has Therapist: " + therapist);
                comment.setTherapist(therapist);
            }
            Log.d("Bulb-Post", "Posting comment");
            String path = "posts/" + post.getUuid();
            StorageReference postsRef = storage.getReference(path);
            post.getComments().add(comment);
            byte[] data = SerializationUtils.serialize(post);
            UploadTask uploadTask = postsRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    adapter.notifyDataSetChanged();
                    String therapistPath = "therapists/" + therapist.id;
                    StorageReference therapistRef = storage.getReference(therapistPath);
                    byte[] therapistData = SerializationUtils.serialize(therapist);
                    UploadTask uploadTaskTherapist = therapistRef.putBytes(therapistData);
                    uploadTaskTherapist.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            newCommentFragment.commentEditText.setText("");
                            newCommentFragmentContainer.setVisibility(View.GONE);
                            appBarLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }
    }


    void checkDataBase(String name, final RecommendTherapistFragment rtf) {
        String id = name.replaceAll(" ", "_").replaceAll("[.]","").toLowerCase();
        final StorageReference userRef = storage.getReference("therapists/" + id);
        final long ONE_MEGABYTE = 1024 * 1024;
        userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Therapist therapist = SerializationUtils.deserialize(bytes);
                Log.d("Bulb", "Retrieved therapist: " + therapist.name + ", this user shouldn't exist.");
                therapist.reviews.add(new Comment(user.getUsername(), rtf.contentET.getText().toString()));
                for(String str : rtf.insuranceET.getText().toString().split(" ")){
                    therapist.insurances.add(str.replaceAll(",", "").replaceAll(" ", ""));
                }
                if(rtf.phoneET.getText().toString().length() >= 1) {
                     therapist.phone = rtf.phoneET.getText().toString();
                }
                if(rtf.addrET.getText().toString().length() >= 1) {
                    therapist.address = rtf.addrET.getText().toString();
                }
                if(rtf.websiteET.getText().toString().length() >= 1) {
                    therapist.website = rtf.websiteET.getText().toString();
                }
                displayNewCommentFragment(therapist, rtf);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Bulb", "Failed to load user. Expected behaviour, registering user.");
                registerTherapist(rtf);
            }
        });
    }

    private void registerTherapist(final RecommendTherapistFragment rtf) {
        final String name = rtf.nameET.getText().toString();
        String therapistId = name.replaceAll(" ", "_").replaceAll("[.]","").toLowerCase();
        final String phone = rtf.phoneET.getText().toString();
        final String address = rtf.addrET.getText().toString();
        final String insurance = "";
        final String content = rtf.contentET.getText().toString();

        final Therapist therapist = new Therapist(name, phone, address, insurance, content, user);
        therapist.website = rtf.websiteET.getText().toString();
        for(String str : rtf.insuranceET.getText().toString().split(" ")){
            therapist.insurances.add(str.replaceAll(",", "").replaceAll(" ", ""));
        }
        displayNewCommentFragment(therapist, rtf);
    }


}


