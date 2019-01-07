package com.bulb.bulb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulb.bulb.Models.User;
import com.bulb.bulb.Models.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {
    ImageView exitIV;
    EditText contentET;
    Button postButton;
    CheckBox checkbox;
    ImageView userIconIV;
    ArrayList<String> tags;
    UserPost post;
    TextView errorTV;
    User user;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        tags = new ArrayList<>();

        exitIV = findViewById(R.id.exit_button);
        exitIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewPostActivity.this, MainActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });
        contentET = findViewById(R.id.main_edit_text);
        userIconIV = findViewById(R.id.user_icon);
        contentET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        // When user clicks POST button
        postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPostButton(v);
            }
        });

    }

    public void onClickPostButton(View view) {
        if (!contentET.getText().toString().equals("")) {
            post = new UserPost(user.getUsername(), contentET.getText().toString(), tags);
            Log.d("Bulb-Post", "Posting with tags: " + tags);
            final Intent i = new Intent(this, ForumActivity.class);
            i.putExtra("post", post);
            i.putExtra("user", user);
            String path = "posts/" + post.getUuid();
            StorageReference postsRef = storage.getReference(path);
            byte[] data = SerializationUtils.serialize(post);
            UploadTask uploadTask = postsRef.putBytes(data);

            final StorageReference uuidRef = storage.getReference("posts/uuids");
            final long ONE_MEGABYTE = 1024 * 1024;
            uuidRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    ArrayList<UUID> uuids = SerializationUtils.deserialize(bytes);
                    uuids.add(post.getUuid());
                    byte[] uuidList = SerializationUtils.serialize(uuids);
                    UploadTask uuidUploadTask = uuidRef.putBytes(uuidList);
                    user.registerPost(post);
                    startActivity(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    ArrayList<UUID> uuids = new ArrayList<>();
                    uuids.add(post.getUuid());
                    byte[] uuidList = SerializationUtils.serialize(uuids);
                    UploadTask uuidUploadTask = uuidRef.putBytes(uuidList);
                    startActivity(i);
                }
            });

        }
    }

    public void onCheckBoxClicked(View view) {
        String tag = ((CheckBox) view).getText().toString();
        Log.d("Bulb-Tag", tag);
        if(tags.contains(tag)) {
            tags.remove(tag);
        } else {
            tags.add(tag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
