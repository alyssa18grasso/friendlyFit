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
import android.widget.EditText;
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
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /////////////////////////////////////////////////////////////////////////////
    private TextView friends_label;
    private Button my_friends, friend_requests, discover_friends;
    private RecyclerView friends_list;
    private RecyclerView.LayoutManager recyclerViewFriendsLayoutManager;
    private FriendsAdapter recyclerViewFriendsAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private User currentLocalUser;
    private ArrayList<User> friends;
    private IFriends listener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public FriendsFragment(User currentLocalUser) {
        this.currentLocalUser = currentLocalUser;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        friends = new ArrayList<>();
//        state = Friend_type.MY_FRIENDS;

        // track the friends...
        getFriendsRealTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friends_label = view.findViewById(R.id.friends);
        my_friends = view.findViewById(R.id.my_friends);
        friend_requests = view.findViewById(R.id.friends_requests);
        discover_friends = view.findViewById(R.id.friends_discover);
        friends_list = view.findViewById(R.id.friends_list);

        recyclerViewFriendsLayoutManager = new LinearLayoutManager(getContext());
        friends_list.setLayoutManager(recyclerViewFriendsLayoutManager);
        recyclerViewFriendsAdapter = new FriendsAdapter(friends, getContext());
        friends_list.setAdapter(recyclerViewFriendsAdapter);

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


    private void getFriendsRealTime() {
        db.collection("users")
                .document(currentLocalUser.getEmail())
                .collection("friends")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int oldSize = friends.size();
                        friends.clear();
                        if (recyclerViewFriendsAdapter != null) {
                            recyclerViewFriendsAdapter.notifyItemRangeRemoved(0, oldSize);
                        }

                        for (DocumentSnapshot doc: value.getDocuments()) {
                            String friendEmail = doc.getString("email");
                            // don't show current user in friends list
                            if (!friendEmail.equals(currentLocalUser.getEmail())) {
                                db.collection("users").document(friendEmail).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                friends.add(documentSnapshot.toObject(User.class));
                                                if (recyclerViewFriendsAdapter != null) {
                                                    recyclerViewFriendsAdapter.notifyItemInserted(friends.size() - 1);
                                                }
                                            }
                                        });
                            }
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