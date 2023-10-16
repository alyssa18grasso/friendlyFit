package com.example.friendlyfit.Workouts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlyfit.R;
import com.example.friendlyfit.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class AddedFriendsAdapter extends RecyclerView.Adapter<AddedFriendsAdapter.ViewHolder> {

    private ArrayList<User> addedFriends;
    // the tag of the fragment this adapter is a part of
    private String fragmentTag;
    private IAddedFriendsAdapterListener sendData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Chip friendChip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendChip = itemView.findViewById(R.id.chip_AddedFriend);
        }

        public Chip getFriendChip() {
            return friendChip;
        }
    }

    public AddedFriendsAdapter(ArrayList<User> addedFriends, String fragmentTag) {
        this.addedFriends = addedFriends;
        this.fragmentTag = fragmentTag;
    }

    @NonNull
    @Override
    public AddedFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.added_friends_list_column, parent, false);

        Context context = parent.getContext();
        if (context instanceof IAddedFriendsAdapterListener) {
            sendData = (IAddedFriendsAdapterListener) context;
        } else {
            throw new RuntimeException(context + " must implement IAddedFriendsAdapterListener interface");
        }

        return new AddedFriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedFriendsAdapter.ViewHolder holder, int position) {
        Chip friendChip = holder.getFriendChip();
        friendChip.setText(addedFriends.get(position).getUsername());
        friendChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.chipCloseIconPressed(holder.getAdapterPosition(), fragmentTag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addedFriends.size();
    }
}
