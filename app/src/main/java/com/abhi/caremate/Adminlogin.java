package com.abhi.caremate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Adminlogin extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton;
    private FirebaseAuth mAuth;

    // Firebase Auth Admin Credentials
    private static final String ADMIN_EMAIL = "quicklearner00000@gmail.com";
    private static final String ADMIN_PASSWORD = "258963";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailInput = findViewById(R.id.AEmail);
        passwordInput = findViewById(R.id.Apass);
        loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Basic validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Adminlogin.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Only allow login with predefined admin email
            if (!email.equals(ADMIN_EMAIL)) {
                Toast.makeText(Adminlogin.this, "You are not authorized as admin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Toast.makeText(Adminlogin.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Adminlogin.this, Admin.class));
                                finish();
                            } else {
                                if (user != null) {
                                    user.sendEmailVerification();
                                    Toast.makeText(Adminlogin.this, "Please verify your email. Verification link sent.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(Adminlogin.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        TextView clickHereText = findViewById(R.id.llp); // link to user login
        clickHereText.setOnClickListener(v -> {
            Intent intent = new Intent(Adminlogin.this, LoginActivity.class);
            startActivity(intent);
            finish(); // close current activity
        });
    }
}
