package com.bulb.bulb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bulb.bulb.Models.FilterRecyclerAdapter;
import com.bulb.bulb.Models.ForumRecyclerAdapter;
import com.bulb.bulb.Models.User;
import com.bulb.bulb.Models.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    EditText usernameEditText;
    EditText passwordEditText;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private boolean inProcess = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override public void onSuccess(AuthResult authResult) {
                Log.d("Bulb", "Successfully Authenticated");
            }
        }) .addOnFailureListener(this, new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception exception) {
                Log.e("Bulb", "signInAnonymously:FAILURE", exception);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inProcess = true;
                Log.d("Bulb", "Logging In");
                disableButtons();
                login();
                inProcess = false;
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inProcess = true;
                Log.d("Bulb", "Registering");
                disableButtons();
                register();
                inProcess = false;
            }
        });

    }

    private void moveToMain(User user) {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    private void disableButtons() {
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        usernameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
    }

    private void enableButtons() {
        loginButton.setEnabled(true);
        registerButton.setEnabled(true);
        usernameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
    }

    private void login() {
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        final StorageReference userRef = storage.getReference("users/" + username);
        final long ONE_MEGABYTE = 1024 * 1024;
        userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                User user = SerializationUtils.deserialize(bytes);
                Log.d("Bulb", "Retrieved user: " + user.getUsername() + ", password: \'" + user.getPassword() + "\' provided: \'" + password + "\'");
                if(password.equals(user.getPassword())) {
                    Log.d("Bulb", "User successfully authenticated.");
                    moveToMain(user);
                } else {
                    Log.d("Bulb", "User denied, incorrect password.");
                    Toast.makeText(LoginActivity.this, "Either your username or password was incorrect.", Toast.LENGTH_LONG)
                            .show();
                    enableButtons();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Bulb", "Failed to load user. Does the username exist?");
                Toast.makeText(LoginActivity.this, "There is no user with this name.", Toast.LENGTH_LONG)
                        .show();
                enableButtons();
            }
        });
    }

    private void register() {
        String username = usernameEditText.getText().toString();
        final StorageReference userRef = storage.getReference("users/" + username);
        final long ONE_MEGABYTE = 1024 * 1024;
        userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                User user = SerializationUtils.deserialize(bytes);
                Log.d("Bulb", "Retrieved user: " + user.getUsername() + ", this user shouldn't exist.");
                Toast.makeText(LoginActivity.this, "This user already exists.", Toast.LENGTH_LONG)
                        .show();
                enableButtons();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Bulb", "Failed to load user. Expected behaviour, registering user.");
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        String path = "users/" + username;
        StorageReference userRef = storage.getReference(path);
        final User user = new User(username, password);
        byte[] data = SerializationUtils.serialize(user);
        UploadTask uploadTask = userRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                moveToMain(user);
            }
        });
    }
}

