package com.abhi.caremate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class dashboard extends Fragment {

    private LinearLayout appointmentLayout, Consult, HealthTools, ShareApp, Website;
    private TextView tvHealthTip;
    private DatabaseReference dbRef;
    private FirebaseUser currentUser;

    public dashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Views
        appointmentLayout = view.findViewById(R.id.appointmentLayout);
        tvHealthTip = view.findViewById(R.id.tvHealthTip);
        HealthTools = view.findViewById(R.id.tool);
        ShareApp = view.findViewById(R.id.share);
        Website = view.findViewById(R.id.web);

        // Firebase User
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));

        }

        // Firebase reference for appointments
        dbRef = FirebaseDatabase.getInstance()
                .getReference("Appointments")
                .child(currentUser.getUid());

        // Load appointments
        loadAppointments();

        // Load latest health tip
        fetchLatestHealthTip();

        // Card: Book Appointment
        CardView cardBookAppointment = view.findViewById(R.id.cardBookAppointment);
        cardBookAppointment.setOnClickListener(v ->
                startActivity(new Intent(getContext(), BookAppointmentActivity.class)));

        // Card: Consult


        // Card: Health Tools -> Open Web Link
        HealthTools.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.webmd.com/tools/default.htm")); // Or any link you prefer
            startActivity(browserIntent);
        });

        // Card: Share App
        ShareApp.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain"); // or use APK sharing if needed
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CareMate App");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Download CareMate App:\nhttps://play.google.com/store/apps/details?id=com.abhi.caremate");
            startActivity(Intent.createChooser(shareIntent, "Share App via"));
        });

        // Card: Website
        Website.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.nih.gov/health-information")); // Replace with your real URL
            startActivity(browserIntent);
        });

        return view;
    }

    private void loadAppointments() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentLayout.removeAllViews();
                for (DataSnapshot data : snapshot.getChildren()) {
                    AppointmentModel model = data.getValue(AppointmentModel.class);
                    if (model != null) {
                        addAppointmentCard(model);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAppointmentCard(AppointmentModel model) {
        if (getContext() == null) return;

        TextView tv = new TextView(getContext());
        tv.setText("• " + model.getName() + " | " + model.getDate() + " | " + model.getReason());
        tv.setTextSize(16);
        tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        tv.setPadding(0, 8, 0, 8);
        appointmentLayout.addView(tv);
    }

    private void fetchLatestHealthTip() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("HealthTips");

        // Fetch the latest tip (limit to last 1)
        ref.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tipSnap : snapshot.getChildren()) {
                    HealthTipModel tip = tipSnap.getValue(HealthTipModel.class);
                    if (tip != null) {
                        tvHealthTip.setText(tip.getTitle() + ": " + tip.getDescription());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvHealthTip.setText("Unable to load health tip.");
            }
        });
    }

    // Appointment Model
    public static class AppointmentModel {
        private String name;
        private String date;
        private String reason;

        public AppointmentModel() {}

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
