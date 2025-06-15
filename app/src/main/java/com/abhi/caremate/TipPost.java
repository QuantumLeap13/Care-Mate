package com.abhi.caremate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TipPost extends AppCompatActivity {

    private EditText editTitle, editDescription;
    private Button btnPost;

    private DatabaseReference tipRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_post);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        btnPost = findViewById(R.id.btnPost);

        tipRef = FirebaseDatabase.getInstance().getReference("HealthTips");

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTip();
            }
        });
    }

    private void postTip() {
        String title = editTitle.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTitle.setError("Enter title");
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            editDescription.setError("Enter description");
            return;
        }

        String id = tipRef.push().getKey();
        HealthTipModel tip = new HealthTipModel(id, title, desc);

        tipRef.child(id).setValue(tip).addOnSuccessListener(unused -> {
            Toast.makeText(TipPost.this, "Tip posted successfully!", Toast.LENGTH_SHORT).show();
            editTitle.setText("");
            editDescription.setText("");
        }).addOnFailureListener(e -> {
            Toast.makeText(TipPost.this, "Failed to post tip", Toast.LENGTH_SHORT).show();
        });
    }
}
