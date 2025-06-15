package com.abhi.caremate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etHeight, etWeight, etBloodGroup, etAge;
    private ImageView ivProfileImage;
    private Button btnSave;

    private Uri imageUri;
    private final int PICK_IMAGE = 1;
    private String profileImageUrl = "";

    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etBloodGroup = findViewById(R.id.etBloodGroup);
        etAge = findViewById(R.id.etAge);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnSave = findViewById(R.id.btnSaveProfile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        ivProfileImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveProfile());

        loadExistingData();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivProfileImage.setImageURI(imageUri);
        }
    }

    private void loadExistingData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").getValue(String.class));
                    etHeight.setText(snapshot.child("height").getValue(String.class));
                    etWeight.setText(snapshot.child("weight").getValue(String.class));
                    etBloodGroup.setText(snapshot.child("bloodGroup").getValue(String.class));
                    etAge.setText(snapshot.child("age").getValue(String.class));

                    profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get().load(profileImageUrl).into(ivProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String bloodGroup = etBloodGroup.getText().toString().trim().toUpperCase();
        String age = etAge.getText().toString().trim();

        // === Validation ===

        // Name: only letters and spaces
        if (!name.matches("^[a-zA-Z\\s]{2,50}$")) {
            Toast.makeText(this, "Invalid name. Use letters and spaces only.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Height: numeric 2–3 digits
        if (!height.matches("^\\d{2,3}$")) {
            Toast.makeText(this, "Invalid height. Use a number like 170.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Weight: numeric 1–3 digits
        if (!weight.matches("^\\d{1,3}$")) {
            Toast.makeText(this, "Invalid weight. Use a number like 65.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Blood Group: must be valid type
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        boolean isValidGroup = false;
        for (String group : validGroups) {
            if (group.equals(bloodGroup)) {
                isValidGroup = true;
                break;
            }
        }
        if (!isValidGroup) {
            Toast.makeText(this, "Invalid blood group. Use A+, B-, AB+, etc.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Age: numeric, 1–2 digits
        if (!age.matches("^\\d{1,2}$")) {
            Toast.makeText(this, "Invalid age. Use a number like 25.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed to upload
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Saving...");
        dialog.show();

        if (imageUri != null) {
            StorageReference imageRef = storageRef.child(currentUser.getUid() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profileImageUrl = uri.toString();
                        uploadData(name, height, weight, bloodGroup, age, profileImageUrl, dialog);
                    })
            ).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            uploadData(name, height, weight, bloodGroup, age, profileImageUrl, dialog);
        }
    }

    private void uploadData(String name, String height, String weight, String bloodGroup, String age, String imageUrl, ProgressDialog dialog) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("height", height);
        data.put("weight", weight);
        data.put("bloodGroup", bloodGroup);
        data.put("age", age);
        data.put("profileImageUrl", imageUrl);

        userRef.updateChildren(data).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
