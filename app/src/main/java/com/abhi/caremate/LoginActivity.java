package com.abhi.caremate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {




    EditText logEmail, loggpass;
    Button loginBtn;
    TextView openReg, Admin, ForPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logEmail = findViewById(R.id.logEmail);
        loggpass = findViewById(R.id.loggpass);
        loginBtn = findViewById(R.id.login);
        openReg = findViewById(R.id.openReg);
        ForPassword = findViewById(R.id.ForPassword);
        Admin = findViewById(R.id.admin);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());

        openReg.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        Admin.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, Adminlogin.class));
            finish();
        });

        ForPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, forgetpassword.class));
        });
    }

    private void loginUser() {
        String email = logEmail.getText().toString().trim();
        String password = loggpass.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Verify your email first!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
