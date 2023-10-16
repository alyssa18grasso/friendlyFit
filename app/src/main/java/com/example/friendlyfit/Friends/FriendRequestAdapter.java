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

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private ArrayList<User> requests;
    private IFriends mListener;
    private Context context;
    public FriendRequestAdapter(ArrayList<User> requests, Context context) {
        super();
        this.requests = requests;
        this.context = context;
        if(context instanceof IFriends){
            this.mListener = (IFriends) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IFriends");
        }
    }

    public ArrayList<User> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<User> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_requests_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.ViewHolder holder, int position) {
        User curFriend = this.getRequests().get(position);
        holder.getRequest_name().setText(curFriend.getUsername());
        if (curFriend.getProfilePictureUri() != null) {
            if (!curFriend.getProfilePictureUri().equals("")) {
                Glide.with(this.context)
                        .load(curFriend.getProfilePictureUri())
                        .into(holder.getRequest_profile());
            }
        }

        holder.getRequest_profile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToFriendProfile(curFriend, "requests");
            }
        });

        holder.getAccept().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Friend Request Accepted.", Toast.LENGTH_SHORT).show();
                mListener.acceptRequest(curFriend);
                notifyDataSetChanged();
            }
        });
        holder.getDecline().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Friend Request Declined.", Toast.LENGTH_SHORT).show();
                mListener.declineRequest(curFriend);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return getRequests().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView request_profile;
        private final TextView request_name;
        private final ImageView accept, decline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.request_profile = itemView.findViewById(R.id.my_friend_profile);
            this.request_name = itemView.findViewById(R.id.my_friend_name);
            this.accept = itemView.findViewById(R.id.accept_friend);
            this.decline = itemView.findViewById(R.id.decline_friend);
        }

        public ImageView getRequest_profile() {
            return request_profile;
        }

        public TextView getRequest_name() {
            return request_name;
        }

        public ImageView getAccept() {
            return accept;
        }

        public ImageView getDecline() {
            return decline;
        }
    }
}
