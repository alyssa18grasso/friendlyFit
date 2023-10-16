package com.example.friendlyfit.Friends;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.friendlyfit.R;
import com.example.friendlyfit.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFriendsFragment extends Fragment {

    private TextView friends_label;
    private Button my_friends, friend_requests, discover_friends;
    private RecyclerView discover_friends_list;
    private RecyclerView.LayoutManager recyclerViewFriendsLayoutManager;
    private DiscoverFriendAdapter recyclerViewFriendsAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private User currentLocalUser;
    private ArrayList<User> users;
    private IFriends listener;

    public DiscoverFriendsFragment() {
        // Required empty public constructor
    }

    public DiscoverFriendsFragment(User curUser) {
        this.currentLocalUser = curUser;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFriendsFragment newInstance(String param1, String param2) {
        DiscoverFriendsFragment fragment = new DiscoverFriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        users = new ArrayList<>();

        // track the users...
        getUsersRealTime();
    }

    private void getUsersRealTime() {
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        users.clear();
                        for (DocumentSnapshot doc: value.getDocuments()) {
                            User user = doc.toObject(User.class);
                            if (!user.getEmail().equals(currentLocalUser.getEmail())) {
                                users.add(doc.toObject(User.class));
                            }
                        }
                        recyclerViewFriendsAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_friends, container, false);

        friends_label = view.findViewById(R.id.friends);
        my_friends = view.findViewById(R.id.my_friends);
        friend_requests = view.findViewById(R.id.friends_requests);
        discover_friends = view.findViewById(R.id.friends_discover);
        discover_friends_list = view.findViewById(R.id.discover_friends_list);

        recyclerViewFriendsLayoutManager = new LinearLayoutManager(getContext());
        discover_friends_list.setLayoutManager(recyclerViewFriendsLayoutManager);
        recyclerViewFriendsAdapter = new DiscoverFriendAdapter(users, getContext());
        discover_friends_list.setAdapter(recyclerViewFriendsAdapter);

        my_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.myFriends();
            }
        });
        friend_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.myFriendRequests();            }
        });
        discover_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.discoverFriends();            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFriends){
            this.listener = (IFriends) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IFriends)");
        }
    }
}