package com.example.friendlyfit.Workouts;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendlyfit.R;
import com.example.friendlyfit.Tags;
import com.example.friendlyfit.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareWorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareWorkoutFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCAL_USER = "localUser";
    private static final String ARG_WORKOUT_DOCUMENT_ID = "workoutDocumentId";

    private User mLocalUser;
    private String mWorkoutDocumentId;
    private ImageButton imageButton_Back;
    private Button button_Share;
    private EditText editText_Collaborators;
    private IShareWorkout sendData;
    private FirebaseFirestore db;
    private ArrayList<User> friendsList;
    private ArrayList<User> fullFriendsList;
    private RecyclerView recyclerView_Friends;
    private WorkoutFriendsAdapter workoutFriendsAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_Friends;
    private ArrayList<User> addedFriends;
    private RecyclerView recyclerView_AddedFriends;
    private AddedFriendsAdapter addedFriendsAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_AddedFriends;
    private ArrayList<User> alreadyCollaborators;


    public ShareWorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param localUser The current authorized user.
     * @param workoutDocumentId The document id of the workout to share.
     * @return A new instance of fragment ShareWorkoutFragment.
     */
    public static ShareWorkoutFragment newInstance(User localUser, String workoutDocumentId) {
        ShareWorkoutFragment fragment = new ShareWorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCAL_USER, localUser);
        args.putString(ARG_WORKOUT_DOCUMENT_ID, workoutDocumentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocalUser = getArguments().getParcelable(ARG_LOCAL_USER);
            mWorkoutDocumentId = getArguments().getString(ARG_WORKOUT_DOCUMENT_ID);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share_workout, container, false);

        imageButton_Back = rootView.findViewById(R.id.imageButton_ShareWorkoutBackButton);
        button_Share = rootView.findViewById(R.id.button_ShareWorkoutShare);
        editText_Collaborators = rootView.findViewById(R.id.editText_ShareWorkoutSearchYourFriends);

        fullFriendsList = new ArrayList<>();
        friendsList = new ArrayList<>();
        addedFriends = new ArrayList<>();
        alreadyCollaborators = new ArrayList<>();

        recyclerView_Friends = rootView.findViewById(R.id.recyclerView_ShareWorkoutFriendsList);
        recyclerViewLayoutManager_Friends = new LinearLayoutManager(getContext());
        recyclerView_Friends.setLayoutManager(recyclerViewLayoutManager_Friends);
        workoutFriendsAdapter = new WorkoutFriendsAdapter(friendsList, Tags.SHARE_WORKOUT_FRAGMENT_TAG);
        recyclerView_Friends.setAdapter(workoutFriendsAdapter);

        recyclerView_AddedFriends = rootView.findViewById(R.id.recyclerView_ShareWorkoutAddedFriends);
        recyclerViewLayoutManager_AddedFriends = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_AddedFriends.setLayoutManager(recyclerViewLayoutManager_AddedFriends);
        addedFriendsAdapter = new AddedFriendsAdapter(addedFriends, Tags.SHARE_WORKOUT_FRAGMENT_TAG);
        recyclerView_AddedFriends.setAdapter(addedFriendsAdapter);

        imageButton_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.shareWorkoutBackPressed();
            }
        });

        button_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement logic to share the workout
                shareWorkout();
                sendData.sharePressed();
            }
        });

        editText_Collaborators.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populateFriendsList();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_Collaborators.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            private boolean justTriggered = false;

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (justTriggered) {
                    justTriggered = false;
                } else {
                    String friendName = textView.getText().toString();
                    if (friendName.equals("")) {
                        editText_Collaborators.setError("Please enter a name");
                    } else if (friendsList.isEmpty()) {
                        editText_Collaborators.setError("Friend could not be found");
                    } else {
                        // get the first friend in the list (this is to avoid issues with friends having the same username)
                        User addedFriend = friendsList.get(0);
                        addFriend(addedFriend);
                        editText_Collaborators.setText("");
                    }
                    justTriggered = true;
                }
                return true;
            }
        });

        getAlreadySharedFriends();
        getAllFriends();

        return rootView;
    }

    public void friendClicked(int position) {
        User friend = friendsList.get(position);
        addFriend(friend);
    }

    public void removeAddedFriend(int position) {
        User unsharedCollaborator = addedFriends.remove(position);
        addedFriendsAdapter.notifyItemRemoved(position);
        populateFriendsList();
    }

    private void addFriend(User friend) {
        addedFriends.add(friend);
        addedFriendsAdapter.notifyItemInserted(addedFriends.size() - 1);
        Toast.makeText(getContext(), "Successfully shared with " + friend.getUsername(), Toast.LENGTH_SHORT).show();
        populateFriendsList();
    }

    /**
     * Populates the friendsList with all friends whose username starts with the string in editText_Collaborators
     * that haven't already been added
     */
    private void populateFriendsList() {
        String filterSequence = editText_Collaborators.getText().toString();
        friendsList.clear();
        friendsList.addAll(fullFriendsList);

        // remove any friends that do not start with the given string or that have already been added
        friendsList.removeIf(user -> !user.getUsername().toLowerCase(Locale.ROOT).startsWith(filterSequence.toLowerCase())
                || addedFriends.stream().anyMatch(friend -> friend.getEmail().equals(user.getEmail())));
        workoutFriendsAdapter.notifyDataSetChanged();
    }

    private void getAllFriends() {
        db.collection("users").document(mLocalUser.getEmail()).collection("friends").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String friendEmail = documentSnapshot.getString("email");
                            getFriend(friendEmail);
                        }
                    }
                });

//        Local Testing data
//        User bob = new User("test1@dev.com", "Bob");
//        bob.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User fred = new User("test2@dev.com", "Fred");
//        fred.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User bill = new User("test3@dev.com", "Bill");
//        bill.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User carol = new User("test4@dev.com", "Carol");
//        carol.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User jack = new User("test5@dev.com", "Jack");
//        jack.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User nathan = new User("test6@dev.com", "Nathan");
//        nathan.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User marge = new User("test7@dev.com", "Marge");
//        marge.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User aaron = new User("test8@dev.com", "Aaron");
//        aaron.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User david = new User("test9@dev.com", "David");
//        david.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User ellen = new User("test10@dev.com", "Ellen");
//        ellen.setProfilePictureUri("https://ih1.redbubble.net/image.1106423053.5126/st,small,507x507-pad,600x600,f8f8f8.u3.jpg");
//        User bob2 = new User("test11@dev.com", "Bob");
//        bob2.setProfilePictureUri("https://static.wikia.nocookie.net/bofuri/images/e/e4/Oboro.jpg/revision/latest?cb=20200306074347");
//
//        fullFriendsList.add(bob);
//        fullFriendsList.add(fred);
//        fullFriendsList.add(bill);
//        fullFriendsList.add(carol);
//        fullFriendsList.add(jack);
//        fullFriendsList.add(nathan);
//        fullFriendsList.add(marge);
//        fullFriendsList.add(aaron);
//        fullFriendsList.add(david);
//        fullFriendsList.add(ellen);
//        fullFriendsList.add(bob2);
//
//        populateFriendsList();
    }

    private void getFriend(String friendEmail) {
        db.collection("users").document(friendEmail).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User friend = documentSnapshot.toObject(User.class);
                        fullFriendsList.add(friend);
                        if (addedFriends.stream().noneMatch(addedFriend -> addedFriend.getEmail().equals(friend.getEmail()))) {
                            friendsList.add(friend);
                            workoutFriendsAdapter.notifyItemInserted(friendsList.size() - 1);
                        }
                    }
                });
    }

    private void getAlreadySharedFriends() {
        db.collection("workouts").document(mWorkoutDocumentId).collection("collaborators").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String collaboratorEmail = documentSnapshot.getString("email");
                            getCollaborator(collaboratorEmail);
                        }
                    }
                });
    }

    private void getCollaborator(String collaboratorEmail) {
        db.collection("users").document(collaboratorEmail).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User collaborator = documentSnapshot.toObject(User.class);
                        alreadyCollaborators.add(collaborator);
                        addedFriends.add(collaborator);
                        addedFriendsAdapter.notifyItemInserted(addedFriends.size() - 1);
                        friendsList.removeIf(friend -> friend.getEmail().equals(collaboratorEmail));
                    }
                });
    }

    private void shareWorkout() {
        // Share workout with added friends
        CollectionReference workoutCollaborators= db.collection("workouts").document(mWorkoutDocumentId).collection("collaborators");
        for (User collaborator : addedFriends) {
            Map<String, String> collaboratorData = new HashMap<>();
            collaboratorData.put("email", collaborator.getEmail());
            workoutCollaborators.document(collaborator.getEmail()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()) {
                                documentSnapshot.getReference().set(collaboratorData);
                            }
                        }
                    });

            Map<String, String> workoutData = new HashMap<>();
            workoutData.put("workoutDocumentReferenceId", mWorkoutDocumentId);
            db.collection("users").document(collaborator.getEmail()).collection("workouts").document(mWorkoutDocumentId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()) {
                                documentSnapshot.getReference().set(workoutData);
                            }
                        }
                    });
        }

        // unshare workout with removed collaborators
        for (User collaborator : alreadyCollaborators) {
            if (addedFriends.stream().noneMatch(addedFriend -> addedFriend.getEmail().equals(collaborator.getEmail()))) {
                db.collection("users").document(collaborator.getEmail()).collection("workouts").document(mWorkoutDocumentId).delete();
                workoutCollaborators.document(collaborator.getEmail()).delete();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IShareWorkout) {
            sendData = (IShareWorkout) context;
        } else {
            throw new RuntimeException(getContext() + " must implement IShareWorkout interface");
        }
    }
}