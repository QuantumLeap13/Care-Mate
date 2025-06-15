package com.abhi.caremate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Calendar;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText etName, etDate, etReason;
    private Button btnSubmit;
    private LinearLayout historyLayout;

    private DatabaseReference dbRef;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        setTitle("Book Appointment");

        etName = findViewById(R.id.etName);
        etDate = findViewById(R.id.etDate);
        etReason = findViewById(R.id.etReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        historyLayout = findViewById(R.id.historyLayout);
        progressBar = findViewById(R.id.progressBar); // Add this ProgressBar in your XML

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Appointments").child(currentUser.getUid());

        // Load existing history for this user
        loadAppointmentHistory();

        // Open calendar when clicking on date
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(reason)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(false);

            String appointmentId = dbRef.push().getKey();
            AppointmentModel appointment = new AppointmentModel(name, date, reason);

            assert appointmentId != null;
            dbRef.child(appointmentId).setValue(appointment).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(this, "Appointment booked", Toast.LENGTH_SHORT).show();

                    // Clear fields
                    etName.setText("");
                    etDate.setText("");
                    etReason.setText("");

                    // Update UI
                    addAppointmentToHistoryView(appointment);
                } else {
                    Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            etDate.setText(selectedDate);
        }, year, month, day);

        // Block past dates
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    private void loadAppointmentHistory() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyLayout.removeAllViews();
                for (DataSnapshot data : snapshot.getChildren()) {
                    AppointmentModel appointment = data.getValue(AppointmentModel.class);
                    if (appointment != null) {
                        addAppointmentToHistoryView(appointment);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookAppointmentActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAppointmentToHistoryView(AppointmentModel appointment) {
        TextView tv = new TextView(this);
        tv.setText("• " + appointment.getName() + " | " + appointment.getDate() + " | " + appointment.getReason());
        tv.setTextSize(16);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setPadding(0, 8, 0, 8);
        historyLayout.addView(tv);
    }

    // Appointment Model
    public static class AppointmentModel {
        private String name;
        private String date;
        private String reason;

        public AppointmentModel() {
        }

        public AppointmentModel(String name, String date, String reason) {
            this.name = name;
            this.date = date;
            this.reason = reason;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getReason() {
            return reason;
        }
    }
}
