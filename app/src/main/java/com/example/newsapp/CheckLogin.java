package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class CheckLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        if (savedEmail != null && savedPassword != null) {
            // Saved login credentials exist, attempt automatic login
            attemptLogin(savedEmail, savedPassword);
        } else {
            // No saved login credentials, show the login screen
            Intent intent = new Intent(CheckLogin.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void attemptLogin(String savedEmail, String savedPassword) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(savedEmail, savedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful, proceed to the main activity
                        Intent intent = new Intent(CheckLogin.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Optional: finish the login activity to prevent going back
                    } else {
                        // Login failed, handle the error or show the login screen
                        Intent intent = new Intent(CheckLogin.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}