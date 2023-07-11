package com.example.newsapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName,editUsername;
    Button saveButton;
    String nameUser, usernameUser,userId;
    DatabaseReference reference;

    FloatingActionButton profileBtn;

    ShapeableImageView img;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    Boolean change=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editName = findViewById(R.id.editName);
        editUsername = findViewById(R.id.editUsername);
        saveButton = findViewById(R.id.saveButton);

        profileBtn = findViewById(R.id.profileBtn);
        img = findViewById(R.id.profileImg);

        showData();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        // Display the selected image
                        Glide.with(this)
                                .load(imageUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(img);
                        uploadImage(imageUri);
                    }
                });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNameChanged() || isUsernameChanged() || isProfileChanged()) {
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
                finish();
                Intent it = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(it);
            }
        });
    }

    private void uploadImage(Uri imageUri) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference childRef = usersRef.child(userId).child("url");

        String imageName = getFileName(imageUri); // Obtain the file name from the imageUri

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images")
                .child(imageName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            childRef.setValue(imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        // Image URL uploaded successfully to the child node URL
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error uploading image URL
                                    });
                        }))
                .addOnFailureListener(e -> {
                    // Error uploading image to Firebase Storage
                });
    }

    private String getFileName(Uri uri) {
        String fileName = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return fileName;
    }



    private boolean isProfileChanged() {
        return false;
    }

    private boolean isUsernameChanged() {
        if (!usernameUser.equals(editUsername.getText().toString())){
            reference.child(userId).child("username").setValue(editUsername.getText().toString());
            return true;
        } else{
            return false;
        }
    }

    public boolean isNameChanged(){
        if (!nameUser.equals(editName.getText().toString())){
            reference.child(userId).child("name").setValue(editName.getText().toString());
            return true;
        } else{
            return false;
        }
    }

    public void showData(){
        Intent intent = getIntent();

        nameUser = intent.getStringExtra("name");
        usernameUser = intent.getStringExtra("username");
        String url = intent.getStringExtra("url");
        Glide.with(this)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(img);

        editName.setText(nameUser);
        editUsername.setText(usernameUser);


    }
}