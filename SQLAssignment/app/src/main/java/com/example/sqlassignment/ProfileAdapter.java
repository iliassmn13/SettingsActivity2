package com.example.sqlassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profiles> profilesList;
    private OnItemClickListener listener;
    private boolean sortByID;

    public interface OnItemClickListener {
        void onItemClick(Profiles profile);
    }

    public ProfileAdapter(List<Profiles> profilesList, OnItemClickListener listener, boolean sortByID) {
        this.profilesList = profilesList;
        this.listener = listener;
        this.sortByID = sortByID;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profiles profile = profilesList.get(position);
        if (sortByID) {
            holder.profileItem.setText((position+1)+". "+String.valueOf(profile.getStudentId()));
        } else {
            holder.profileItem.setText((position+1)+". "+profile.getSurname() + ", " + profile.getName());
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(profile));
    }

    @Override
    public int getItemCount() {
        return profilesList.size();
    }


    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView profileItem;

        public ProfileViewHolder(View view) {
            super(view);
            profileItem = view.findViewById(R.id.profile_item);
        }
    }
}