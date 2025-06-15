package com.abhi.caremate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HealthTipAdapter extends RecyclerView.Adapter<HealthTipAdapter.ViewHolder> {

    private List<HealthTipModel> tipList;

    public HealthTipAdapter(List<HealthTipModel> tipList) {
        this.tipList = tipList;
    }

    @NonNull
    @Override
    public HealthTipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_tip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthTipAdapter.ViewHolder holder, int position) {
        HealthTipModel model = tipList.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return tipList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            description = itemView.findViewById(R.id.textDescription);
        }
    }
}
