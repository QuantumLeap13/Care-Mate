package com.abhi.caremate;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class consultADD extends AppCompatActivity {

    private EditText editDoctorName, editDepartment;
    private Button btnSave;

    DatabaseReference doctorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_add);

        editDoctorName = findViewById(R.id.editDoctorName);
        editDepartment = findViewById(R.id.editDepartment);
        btnSave = findViewById(R.id.btnSave);

        // Firebase reference to "Doctors" node
        doctorRef = FirebaseDatabase.getInstance().getReference("Doctors");

        btnSave.setOnClickListener(view -> {
            String name = editDoctorName.getText().toString().trim();
            String department = editDepartment.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                editDoctorName.setError("Enter Doctor Name");
                return;
            }

            if (TextUtils.isEmpty(department)) {
                editDepartment.setError("Enter Department");
                return;
            }

            // Save data to Firebase
            String key = doctorRef.push().getKey(); // unique key

            HashMap<String, String> doctorMap = new HashMap<>();
            doctorMap.put("name", name);
            doctorMap.put("department", department);

            if (key != null) {
                doctorRef.child(key).setValue(doctorMap)
                        .addOnSuccessListener(aVoid -> Toast.makeText(consultADD.this, "Doctor added successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(consultADD.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
