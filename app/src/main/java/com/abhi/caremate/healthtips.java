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

public class healthtips extends Fragment {

    private RecyclerView recyclerView;
    private HealthTipAdapter adapter;
    private List<HealthTipModel> tipList;

    public healthtips() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healthtips, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHealthTips);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tipList = new ArrayList<>();
        adapter = new HealthTipAdapter(tipList);
        recyclerView.setAdapter(adapter);

        loadHealthTips();

        return view;
    }

    private void loadHealthTips() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("HealthTips");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tipList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    HealthTipModel model = snap.getValue(HealthTipModel.class);
                    if (model != null) {
                        tipList.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load health tips", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
