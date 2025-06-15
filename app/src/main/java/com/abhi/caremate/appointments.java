package com.abhi.caremate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class appointments extends Fragment {

    private LinearLayout layoutUpcomingAppointments, layoutPreviousAppointments;
    private DatabaseReference appointmentRef;
    private String currentUserId;

    public appointments() {}

    public static appointments newInstance(String param1, String param2) {
        appointments fragment = new appointments();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        layoutUpcomingAppointments = view.findViewById(R.id.layoutUpcomingAppointments);
        layoutPreviousAppointments = view.findViewById(R.id.layoutPreviousAppointments);

        currentUserId = FirebaseAuth.getInstance().getUid();
        appointmentRef = FirebaseDatabase.getInstance().getReference("Appointments").child(currentUserId);

        loadAppointments();

        return view;
    }

    private void loadAppointments() {
        appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layoutUpcomingAppointments.removeAllViews();
                layoutPreviousAppointments.removeAllViews();

                boolean hasUpcoming = false;
                boolean hasPrevious = false;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("name").getValue(String.class);    // doctor name
                    String date = child.child("date").getValue(String.class);    // format: dd/MM/yyyy
                    String reason = child.child("reason").getValue(String.class); // reason for appointment

                    if (name == null || date == null || reason == null) continue;

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date appointmentDate = sdf.parse(date);
                        Date today = sdf.parse(sdf.format(new Date())); // strip time

                        if (appointmentDate != null && today != null) {
                            View card = createAppointmentCard(name, date, reason);
                            if (!appointmentDate.before(today)) {
                                layoutUpcomingAppointments.addView(card);
                                hasUpcoming = true;
                            } else {
                                layoutPreviousAppointments.addView(card);
                                hasPrevious = true;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (!hasUpcoming) {
                    layoutUpcomingAppointments.addView(createInfoTextView("No upcoming appointments."));
                }
                if (!hasPrevious) {
                    layoutPreviousAppointments.addView(createInfoTextView("No previous appointments."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                layoutUpcomingAppointments.addView(createInfoTextView("Failed to load appointments."));
            }
        });
    }

    private View createAppointmentCard(String name, String date, String reason) {
        CardView card = new CardView(getContext());
        card.setCardElevation(8);
        card.setRadius(18);
        card.setCardBackgroundColor(Color.WHITE);
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView tvDoctor = new TextView(getContext());
        tvDoctor.setText("Doctor: " + name);
        tvDoctor.setTextSize(16);
        tvDoctor.setTextColor(Color.BLACK);

        TextView tvDate = new TextView(getContext());
        tvDate.setText("Date: " + date);
        tvDate.setTextColor(Color.DKGRAY);

        TextView tvType = new TextView(getContext());
        tvType.setText("Reason: " + reason);
        tvType.setTextColor(Color.DKGRAY);

        layout.addView(tvDoctor);
        layout.addView(tvDate);
        layout.addView(tvType);
        card.addView(layout);

        return card;
    }

    private TextView createInfoTextView(String message) {
        TextView tv = new TextView(getContext());
        tv.setText(message);
        tv.setTextColor(Color.DKGRAY);
        tv.setPadding(12, 20, 12, 20);
        return tv;
    }
}
