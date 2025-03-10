package com.example.sqlassignment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccessAdapter extends RecyclerView.Adapter<AccessAdapter.AccessViewHolder> {

    private List<Access> accessList;

    public AccessAdapter(List<Access> accessList) {
        this.accessList = accessList;
    }

    @NonNull
    @Override
    public AccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.access_item, parent, false);
        return new AccessViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AccessViewHolder holder, int position) {
        Access access = accessList.get(position);
        holder.accessItem.setText(access.getTimestamp()+" "+access.getAccessType());
    }

    @Override
    public int getItemCount() {
        return accessList.size();
    }

    public String getTimestamp(int index){
        return accessList.get(index).getTimestamp();
    }

    public static class AccessViewHolder extends RecyclerView.ViewHolder {
        public TextView accessItem;

        public AccessViewHolder(@NonNull View itemView) {
            super(itemView);
            accessItem = itemView.findViewById(R.id.access_item);
        }
    }
}