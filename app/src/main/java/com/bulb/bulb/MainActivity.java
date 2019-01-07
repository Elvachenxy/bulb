package com.bulb.bulb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bulb.bulb.Models.FilterRecyclerAdapter;
import com.bulb.bulb.Models.ForumRecyclerAdapter;
import com.bulb.bulb.Models.Therapist;
import com.bulb.bulb.Models.User;
import com.bulb.bulb.Models.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static Object therapists;
    private ArrayList<UserPost> posts = new ArrayList<>();
    private FloatingActionButton newPostButton;
    ForumRecyclerAdapter postAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    RecyclerView recyclerView;
    Button mapButton;
    ImageView menuButton;
    ConstraintLayout menuContainer;
    private DrawerLayout mDrawerLayout;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forumrecycler);
        user = (User) getIntent().getSerializableExtra("user");
        Log.d("Bulb", "User POSTS: " + user.getUserPosts().toString());
        Log.d("Bulb", "Following POSTS: " + user.followingPosts.toString());
        newPostButton = findViewById(R.id.newPostButton);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewPostActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getItemId() == R.id.action_map) {
                            initializeMap();
                            bottomNavigationView.setSelectedItemId(R.id.action_forum);
                        }
                        return true;
                    }
                });
        mDrawerLayout = findViewById(R.id.menu_drawer);
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_username)).setText(user.getUsername());
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight

                        if(!menuItem.isChecked() || menuItem.getItemId() == R.id.nav_following) {
                            menuItem.setChecked(true);
                            int id = menuItem.getItemId();
                            if(id == R.id.nav_forum) {
                                loadPosts();
                            }
                            else if(id == R.id.nav_user_posts) {
                                fetchPosts(user.getUserPosts());
                            }
                            else if(id == R.id.nav_following) {
                                fetchPosts(user.followingPosts);
                            }
                            else if(id == R.id.nav_logout) {
                                logout();
                            }
                        }
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Log.d("Bulb", "SELECTED: " + menuItem.toString());
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override public void onSuccess(AuthResult authResult) {
                Log.d("Bulb", "Successfully Authenticated");
                loadPosts();
            }
        }) .addOnFailureListener(this, new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception exception) {
                Log.e("Bulb", "signInAnonymously:FAILURE", exception);
            }
        });
        initializeFilters();
        //mapButton = findViewById(R.id.mapbutton);
        /*mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeMap();
            }
        });*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void initializeMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            askForLocationPermission();
        } else {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            i.putExtra("user", user);
            Log.d("BULB", "PASSING USER: " + user);
            startActivity(i);
        }
    }

    private void askForLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    i.putExtra("user", user);
                    Log.d("BULB", "PASSING USER: " + user);
                    startActivity(i);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Map feature requires location permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void loadPosts() {
        posts = new ArrayList<>();
        final StorageReference uuidRef = storage.getReference("posts/uuids");
        final long ONE_MEGABYTE = 1024 * 1024;
        uuidRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ArrayList<UUID> uuids = SerializationUtils.deserialize(bytes);
                Log.d("Bulb", "Fetching Posts: " + uuids);
                initializeRecycler();
                fetchPosts(uuids);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Bulb", "Failed to load uuids");
                initializeRecycler();
            }
        });
    }

    private void fetchPosts(ArrayList<UUID> uuids) {
        posts.clear();
        for(UUID uuid : uuids) {
            final StorageReference uuidRef = storage.getReference("posts/" + uuid);
            final long ONE_MEGABYTE = 1024 * 1024;
            uuidRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    UserPost post = SerializationUtils.deserialize(bytes);
                    Log.d("Bulb", "Loaded post: <" + post.getUuid() +"> with tags: " + post.getTags() + " with comments: " + post.getComments());
                    posts.add(post);
                    Collections.sort(posts, new Comparator<UserPost>() {
                        @Override
                        public int compare(UserPost p1, UserPost p2) {
                            return p2.getRawDate().compareTo(p1.getRawDate());
                        }
                    });
                    postAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("Bulb", "Failed to load post");
                }
            });
        }
        postAdapter.setPosts(posts);
        postAdapter.notifyDataSetChanged();
        Log.d("Bulb", "Posts:" + posts);
    }

    private void initializeRecycler(){
        RecyclerView recyclerView = findViewById(R.id.posts);
        Collections.sort(posts, new Comparator<UserPost>() {
            @Override
            public int compare(UserPost p1, UserPost p2) {
                return p2.getRawDate().compareTo(p1.getRawDate());
            }
        });
        postAdapter = new ForumRecyclerAdapter(this, posts, user);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void initializeFilters(){
        RecyclerView recyclerView = findViewById(R.id.filters);
        ArrayList<String> filters = new ArrayList<>();
        filters.add("All");
        filters.add("Anxiety");
        filters.add("Stress");
        filters.add("Depression");
        filters.add("PTSD");
        filters.add("Sleep");
        filters.add("Eating");
        filters.add("General");
        filters.add("Other");
        FilterRecyclerAdapter adapter = new FilterRecyclerAdapter(this, filters);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
    }

    public void filterPosts(ArrayList<String> tags) {
        Collections.sort(posts, new Comparator<UserPost>() {
            @Override
            public int compare(UserPost p1, UserPost p2) {
                return p2.getRawDate().compareTo(p1.getRawDate());
            }
        });
        Log.d("Bulb", "Adapter notified of change");
        postAdapter.setPosts(UserPost.filter(tags, posts));
        postAdapter.notifyDataSetChanged();
    }

}
