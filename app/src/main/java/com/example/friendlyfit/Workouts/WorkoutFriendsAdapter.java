package com.example.friendlyfit.Workouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendlyfit.R;
import com.example.friendlyfit.User;

import java.util.ArrayList;

public class WorkoutFriendsAdapter extends RecyclerView.Adapter<WorkoutFriendsAdapter.ViewHolder> {

    private ArrayList<User> friends;
    private Context context;
    // the fragment that this adapter is part of
    private String fragmentTag;
    private IWorkoutFriendsRecyclerViewListener sendData;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView_FriendName;
        private final ImageView imageView_FriendProfilePicture;

        public ViewHolder(@NonNull View itemView, IWorkoutFriendsRecyclerViewListener sendData, String fragmentTag) {
            super(itemView);
            textView_FriendName = itemView.findViewById(R.id.textView_ShareWorkoutFriendsListRowName);
            imageView_FriendProfilePicture = itemView.findViewById(R.id.imageView_ShareWorkoutFriendsListRowAvatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        sendData.friendClicked(position, fragmentTag);
                    }
                }
            });
        }

        public TextView getTextView_FriendName() {
            return textView_FriendName;
        }

        public ImageView getImageView_FriendProfilePicture() {
            return imageView_FriendProfilePicture;
        }
    }

    public WorkoutFriendsAdapter(ArrayList<User> friends, String fragmentTag) {
        this.friends = friends;
        this.fragmentTag = fragmentTag;
    }

    @NonNull
    @Override
    public WorkoutFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_friends_list_row, parent, false);
        context = view.getContext();
        if (context instanceof IWorkoutFriendsRecyclerViewListener) {
            sendData = (IWorkoutFriendsRecyclerViewListener) context;
        } else {
            throw new RuntimeException(context + " must implement IWorkoutFriendsRecyclerViewListener interface");
        }
        return new WorkoutFriendsAdapter.ViewHolder(view, sendData, fragmentTag);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutFriendsAdapter.ViewHolder holder, int position) {
        User user = friends.get(position);
        if (user.getProfilePictureUri() != null && !user.getProfilePictureUri().equals("")) {
            Glide.with(context)
                    .load(user.getProfilePictureUri())
                    .into(holder.getImageView_FriendProfilePicture());
        }
        holder.getTextView_FriendName().setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
