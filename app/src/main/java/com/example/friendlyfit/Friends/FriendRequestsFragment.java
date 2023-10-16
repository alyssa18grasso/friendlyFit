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
import com.google.android.gms.tasks.OnSuccessListener;
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
 * Use the {@link FriendRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendRequestsFragment extends Fragment {

    private TextView friends_label;
    private Button my_friends, friend_requests, discover_friends;
    private RecyclerView friend_requests_list;
    private RecyclerView.LayoutManager recyclerViewFriendsLayoutManager;
    private FriendRequestAdapter recyclerViewFriendsAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private User currentLocalUser;
    private ArrayList<User> requests;
    private IFriends listener;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    public FriendRequestsFragment(User curUser) {
        this.currentLocalUser = curUser;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendRequestsFragment newInstance(String param1, String param2) {
        FriendRequestsFragment fragment = new FriendRequestsFragment();
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
        requests = new ArrayList<>();

        // track the requests...
        getRequestsRealTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        friends_label = view.findViewById(R.id.friends);
        my_friends = view.findViewById(R.id.my_friends);
        friend_requests = view.findViewById(R.id.friends_requests);
        discover_friends = view.findViewById(R.id.friends_discover);
        friend_requests_list = view.findViewById(R.id.friend_requests_list);

        recyclerViewFriendsLayoutManager = new LinearLayoutManager(getContext());
        friend_requests_list.setLayoutManager(recyclerViewFriendsLayoutManager);
        recyclerViewFriendsAdapter = new FriendRequestAdapter(requests, getContext());
        friend_requests_list.setAdapter(recyclerViewFriendsAdapter);

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

    private void getRequestsRealTime() {
        db.collection("users")
                .document(currentLocalUser.getEmail())
                .collection("requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int oldSize = requests.size();
                        requests.clear();
                        if (recyclerViewFriendsAdapter != null) {
                            recyclerViewFriendsAdapter.notifyItemRangeRemoved(0, oldSize);
                        }

                        for (DocumentSnapshot doc: value.getDocuments()) {
                            String friendEmail = doc.getString("email");
                            db.collection("users").document(friendEmail).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    requests.add(documentSnapshot.toObject(User.class));
                                                    if (recyclerViewFriendsAdapter != null) {
                                                        recyclerViewFriendsAdapter.notifyItemInserted(requests.size() -1 );
                                                    }
                                                }
                                            });
                        }
                    }
                });
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