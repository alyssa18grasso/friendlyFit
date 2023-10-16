package com.example.friendlyfit.Friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendlyfit.R;
import com.example.friendlyfit.User;

import java.util.ArrayList;

public class DiscoverFriendAdapter extends RecyclerView.Adapter<DiscoverFriendAdapter.ViewHolder> {

    private ArrayList<User> users;
    private IFriends mListener;
    private Context context;

    public DiscoverFriendAdapter(ArrayList<User> users, Context context) {
        super();
        this.users = users;
        this.context = context;
        if(context instanceof IFriends){
            this.mListener = (IFriends) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IFriends");
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @NonNull
    @Override
    public DiscoverFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discover_friends_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverFriendAdapter.ViewHolder holder, int position) {
        User curFriend = this.getUsers().get(position);
        holder.getDiscover_name().setText(curFriend.getUsername());
        if (curFriend.getProfilePictureUri() != null) {
            if (!curFriend.getProfilePictureUri().equals("")) {
                Glide.with(this.context)
                        .load(curFriend.getProfilePictureUri())
                        .into(holder.getDiscover_profile());
            }
        }

        holder.getDiscover_profile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToFriendProfile(curFriend, "discover");
            }
        });

        holder.getRequest().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Friend Request Sent.", Toast.LENGTH_SHORT).show();
                mListener.sendRequest(curFriend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getUsers().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView discover_profile;
        private final TextView discover_name;
        private final Button request;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.discover_profile = itemView.findViewById(R.id.my_friend_profile);
            this.discover_name = itemView.findViewById(R.id.my_friend_name);
            this.request = itemView.findViewById(R.id.request_button);
        }


        public ImageView getDiscover_profile() {
            return discover_profile;
        }

        public TextView getDiscover_name() {
            return discover_name;
        }

        public Button getRequest() {
            return request;
        }
    }
}
