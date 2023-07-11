package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername;
    TextView titleName, titleUsername;
    Button editProfile, logout;

    ProgressBar progressBar;

    ShapeableImageView pImg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseApp.initializeApp(this);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        progressBar = findViewById(R.id.idLoading);
        pImg = findViewById(R.id.profileImg);
        logout = findViewById(R.id.logoutButton);

        showUserData(this);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", null);
                editor.putString("password", null);
                editor.apply();
                Intent it = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            }
        });
    }

    public void showUserData(Context context){

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
                        String imageUrl = (String) userData.get("url");

                        if (imageUrl == null){
                            imageUrl = "https://firebasestorage.googleapis.com/v0/b/newsapp-2779f.appspot.com/o/images%2Fdefault_img?alt=media&token=cccb049b-7ad3-433e-8c39-fcef02e5baed";
                        }

                        titleName.setText(nameFromDB);
                        titleUsername.setText(username);
                        profileName.setText(nameFromDB);
                        profileEmail.setText(emailFromDB);
                        profileUsername.setText(username);
                        Glide.with(context)
                                .load(imageUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(pImg);

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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String nameFromDB = snapshot.child(userId).child("name").getValue(String.class);
                    String usernameFromDB = snapshot.child(userId).child("username").getValue(String.class);
                    String imageUrl = snapshot.child(userId).child("url").getValue(String.class);

                    if (imageUrl == null){
                        imageUrl = "https://firebasestorage.googleapis.com/v0/b/newsapp-2779f.appspot.com/o/images%2Fdefault_img?alt=media&token=cccb049b-7ad3-433e-8c39-fcef02e5baed";
                    }



                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("url", imageUrl);

                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}