package com.abhi.caremate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class consult extends Fragment {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<DoctorModel> doctorList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consult, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(getContext(), doctorList);
        recyclerView.setAdapter(adapter);

        loadDoctorData();

        return view;
    }

    private void loadDoctorData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Doctors");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();  // Clear existing data
                for (DataSnapshot snap : snapshot.getChildren()) {
                    DoctorModel model = snap.getValue(DoctorModel.class);
                    if (model != null) {
                        doctorList.add(model);
                    }
                }
                adapter.notifyDataSetChanged();  // Refresh list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load doctor data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
