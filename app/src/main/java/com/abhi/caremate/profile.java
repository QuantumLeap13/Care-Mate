package com.abhi.caremate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

public class profile extends Fragment {

    private ImageView profileImage;
    private TextView tvName, tvEmail, tvHeight, tvWeight, tvBloodGroup, tvAge;
    private Button btnEditProfile;

    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    public profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvBloodGroup = view.findViewById(R.id.tvBloodGroup);
        tvAge = view.findViewById(R.id.tvAge);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            tvEmail.setText(currentUser.getEmail());
            loadProfileData();
        }

        // Prevent clicking the image from doing anything
        profileImage.setClickable(false);
        profileImage.setFocusable(false);

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadProfileData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String height = snapshot.child("height").getValue(String.class);
                    String weight = snapshot.child("weight").getValue(String.class);
                    String bloodGroup = snapshot.child("bloodGroup").getValue(String.class);
                    String age = snapshot.child("age").getValue(String.class);
                    String imageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    tvName.setText(name != null && !name.isEmpty() ? name : "Your Name");
                    tvHeight.setText(height + " cm");
                    tvWeight.setText(weight + " kg");
                    tvBloodGroup.setText(bloodGroup != null ? bloodGroup : "--");
                    tvAge.setText(age != null ? age : "--");

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).placeholder(R.drawable.img_3).into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.img_3); // fallback image
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
