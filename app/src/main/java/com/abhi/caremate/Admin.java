package com.abhi.caremate;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Admin extends AppCompatActivity {



    MaterialCardView cardAppointments, cardConsult, cardHealthTips;
    TextView txtAppointments;
    Button btnLogout;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        cardAppointments = findViewById(R.id.cardAppointments);
        cardConsult = findViewById(R.id.cardConsult);
        cardHealthTips = findViewById(R.id.cardHealthTips);
        btnLogout = findViewById(R.id.btnLogout);
        txtAppointments = new TextView(this);

        ((LinearLayout) cardAppointments.getChildAt(0)).addView(txtAppointments);

        // Fetch and display today's appointment count
        fetchTodaysAppointmentCount();
        cardConsult.setOnClickListener(v -> startActivity(new Intent(Admin.this, consultADD.class)));
        cardHealthTips.setOnClickListener(v -> startActivity(new Intent(Admin.this, TipPost.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Admin.this, Adminlogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchTodaysAppointmentCount() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Appointments");

        // Get today's date in d/M/yyyy format (e.g., 14/6/2025)
        String todayDate = new SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot appointmentSnapshot : userSnapshot.getChildren()) {
                        String date = appointmentSnapshot.child("date").getValue(String.class);
                        if (todayDate.equals(date)) {
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    txtAppointments.setText(count + " Appointments Today");
                } else {
                    txtAppointments.setText("No Appointments Today");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtAppointments.setText("No Appointments Today");
            }
        });
    }


}
