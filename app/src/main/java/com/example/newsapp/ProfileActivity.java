package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editProfile;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseApp.initializeApp(this);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        progressBar = findViewById(R.id.idLoading);

        showUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
    }

    public void showUserData(){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        showLoadingIndicator();
        databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hideLoadingIndicator();

                if (dataSnapshot.exists()) {
                    HashMap<String, Object> userData = (HashMap<String, Object>) dataSnapshot.getValue();

                    if (userData != null) {
                        String username = (String) userData.get("username");
                        String nameFromDB = (String) userData.get("name");
                        String emailFromDB = (String) userData.get("email");
                        String passwordFromDB = (String) userData.get("password");

                        titleName.setText(nameFromDB);
                        titleUsername.setText(username);
                        profileName.setText(nameFromDB);
                        profileEmail.setText(emailFromDB);
                        profileUsername.setText(username);
                        profilePassword.setText(passwordFromDB);

                        // Use the retrieved values as needed
                    } else {
                        // Handle the case where userData is null
                        Toast.makeText(ProfileActivity.this, "Empty User Data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the snapshot doesn't exist
                    hideLoadingIndicator();
                    Toast.makeText(ProfileActivity.this, "Empty Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any database error that occurred
                Toast.makeText(ProfileActivity.this, "No User", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void hideLoadingIndicator() {
        progressBar.setVisibility(View.GONE);
    }

    private void showLoadingIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void passUserData(){
        String userUsername = profileUsername.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("password", passwordFromDB);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}