package com.example.friendlyfit.Friends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendlyfit.R;
import com.example.friendlyfit.User;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private ArrayList<User> friends;
    private IFriends mListener;
    private Context context;

    public FriendsAdapter(ArrayList<User> friends, Context context) {
        this.friends = friends;
        this.context = context;
        if(context instanceof IFriends){
            this.mListener = (IFriends) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IFriends");
        }
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView friend_profile, chat_friend;
        private final TextView friend_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.friend_profile = itemView.findViewById(R.id.my_friend_profile);
            this.chat_friend = itemView.findViewById(R.id.my_friend_chat);
            this.friend_name = itemView.findViewById(R.id.my_friend_name);
        }

        public ImageView getFriend_profile() {
            return friend_profile;
        }

        public ImageView getChat_friend() {
            return chat_friend;
        }

        public TextView getFriend_name() {
            return friend_name;
        }
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_friends_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        User curFriend = this.getFriends().get(position);
        holder.getFriend_name().setText(curFriend.getUsername());
        if (curFriend.getProfilePictureUri() != null) {
            if (!curFriend.getProfilePictureUri().equals("")) {
                Glide.with(this.context)
                        .load(curFriend.getProfilePictureUri())
                        .into(holder.getFriend_profile());
            }
        }

        holder.getFriend_profile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToFriendProfile(curFriend, "friends");
            }
        });

        holder.getChat_friend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.chatFriend(curFriend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.getFriends().size();
    }


}
