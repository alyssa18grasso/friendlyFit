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
import com.example.friendlyfit.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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
 * Use the {@link CreateWorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateWorkoutFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCAL_USER = "localUser";

    private User mLocalUser;
    private ImageButton imageButton_Back;
    private ImageButton imageButton_AddExercise;
    private Button button_CreateWorkout;
    private EditText editText_WorkoutName;
    private EditText editText_CollaboratorName;
    private EditText editText_ExerciseName;
    private FirebaseFirestore db;
    private ArrayList<User> fullFriendsList;
    private ArrayList<User> friendsList;
    private RecyclerView recyclerView_Friends;
    private WorkoutFriendsAdapter workoutFriendsAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_Friends;
    private ArrayList<String> exercises;
    private RecyclerView recyclerView_Exercises;
    private ExerciseListAdapter exerciseListAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_Exercises;
    private ArrayList<User> addedFriends;
    private RecyclerView recyclerView_AddedFriends;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_AddedFriends;
    private AddedFriendsAdapter addedFriendsAdapter;
    private ICreateWorkout sendData;

    public CreateWorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param localUser The current authorized user.
     * @return A new instance of fragment CreateWorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateWorkoutFragment newInstance(User localUser) {
        CreateWorkoutFragment fragment = new CreateWorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCAL_USER, localUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocalUser = getArguments().getParcelable(ARG_LOCAL_USER);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_workout, container, false);

        imageButton_Back = rootView.findViewById(R.id.imageButton_CreateWorkoutBack);
        imageButton_AddExercise = rootView.findViewById(R.id.imageButton_CreateWorkoutAddExercise);
        button_CreateWorkout = rootView.findViewById(R.id.button_CreateWorkoutCreateWorkout);
        editText_WorkoutName = rootView.findViewById(R.id.editText_CreateWorkoutWorkoutName);
        editText_CollaboratorName = rootView.findViewById(R.id.editText_CreateWorkoutAddCollaborators);
        editText_ExerciseName = rootView.findViewById(R.id.editText_CreateWorkoutNewExerciseName);

        fullFriendsList = new ArrayList<>();
        friendsList = new ArrayList<>();
        exercises = new ArrayList<>();
        addedFriends = new ArrayList<>();

        recyclerView_Friends = rootView.findViewById(R.id.recyclerView_CreateWorkoutFriendsList);
        recyclerViewLayoutManager_Friends = new LinearLayoutManager(getContext());
        recyclerView_Friends.setLayoutManager(recyclerViewLayoutManager_Friends);
        workoutFriendsAdapter = new WorkoutFriendsAdapter(friendsList, Tags.CREATE_WORKOUT_FRAGMENT_TAG);
        recyclerView_Friends.setAdapter(workoutFriendsAdapter);

        recyclerView_Exercises = rootView.findViewById(R.id.recyclerView_CreateWorkoutExerciseList);
        recyclerViewLayoutManager_Exercises = new LinearLayoutManager(getContext());
        recyclerView_Exercises.setLayoutManager(recyclerViewLayoutManager_Exercises);
        exerciseListAdapter = new ExerciseListAdapter(exercises, Tags.CREATE_WORKOUT_FRAGMENT_TAG);
        recyclerView_Exercises.setAdapter(exerciseListAdapter);

        recyclerView_AddedFriends = rootView.findViewById(R.id.recyclerView_CreateWorkoutAddedFriends);
        recyclerViewLayoutManager_AddedFriends = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_AddedFriends.setLayoutManager(recyclerViewLayoutManager_AddedFriends);
        addedFriendsAdapter = new AddedFriendsAdapter(addedFriends, Tags.CREATE_WORKOUT_FRAGMENT_TAG);
        recyclerView_AddedFriends.setAdapter(addedFriendsAdapter);

        imageButton_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.createWorkoutBackPressed();
            }
        });

        imageButton_AddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exerciseName = editText_ExerciseName.getText().toString();
                if (exerciseName.equals("")) {
                    editText_ExerciseName.requestFocus();
                    editText_ExerciseName.setError("Please enter an exercise name");
                } else {
                    addExercise(exerciseName);
                    editText_ExerciseName.setText("");
                }
            }
        });

        editText_ExerciseName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            private boolean justTriggered = false;
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (justTriggered) {
                    justTriggered = false;
                } else {
                    String exerciseName = textView.getText().toString();
                    if (exerciseName.equals("")) {
                        editText_ExerciseName.setError("Please enter an exercise name");
                    } else {
                        addExercise(exerciseName);
                    }
                    justTriggered = true;
                    editText_ExerciseName.setText("");
                }
                return true;
            }
        });

        editText_CollaboratorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                populateFriendsList();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText_CollaboratorName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            private boolean justTriggered = false;

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (justTriggered) {
                    justTriggered = false;
                } else {
                    if (textView.getText().toString().equals("")) {
                        editText_CollaboratorName.setError("Please enter a name");
                    } else if (friendsList.isEmpty()) {
                        editText_CollaboratorName.setError("Friend could not be found");
                    } else {
                        User friend = friendsList.get(0);
                        addFriend(friend);
                        editText_CollaboratorName.setText("");
                    }
                    justTriggered = true;
                }
                return true;
            }
        });

        editText_CollaboratorName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    recyclerView_Friends.setVisibility(View.VISIBLE);
                } else {
                    recyclerView_Friends.setVisibility(View.GONE);
                }
            }
        });

        button_CreateWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWorkout();
            }
        });

        getAllFriends();

        return rootView;
    }

    public void deleteExercise(int position) {
        exercises.remove(position);
        exerciseListAdapter.notifyItemRemoved(position);
    }

    public void friendClicked(int position) {
        User friend = friendsList.get(position);
        addFriend(friend);
    }

    public void removeAddedFriend(int position) {
        addedFriends.remove(position);
        addedFriendsAdapter.notifyItemRemoved(position);
        populateFriendsList();
    }

    private void createWorkout() {
        String workoutName = editText_WorkoutName.getText().toString();
        if (workoutName.equals("")) {
            editText_WorkoutName.setError("Please enter a workout name");
        } else if (exercises.isEmpty()) {
            Toast.makeText(getContext(), "Cannot create a workout with no exercises", Toast.LENGTH_SHORT).show();
        } else {
            String workoutId = Utils.generateUniqueWorkoutId(mLocalUser.getEmail(), workoutName);
            DocumentReference workoutDoc = db.collection("workouts").document(workoutId);
            workoutDoc.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                editText_WorkoutName.setError("A workout with that name already exists");
                            } else {
                                Map<String, Object> workoutData = new HashMap<>();
                                workoutData.put("workoutName", workoutName);
                                workoutData.put("creatorEmail", mLocalUser.getEmail());
                                workoutDoc.set(workoutData);
                                for (String exercise : exercises) {
                                    Map<String, String> exerciseData = new HashMap<>();
                                    exerciseData.put("exerciseName", exercise);
                                    workoutDoc.collection("exercises").add(exerciseData);
                                }

                                Map<String, String> userWorkoutData = new HashMap<>();
                                userWorkoutData.put("workoutDocumentReferenceId", workoutId);
                                for (User collaborator : addedFriends) {
                                    Map<String, String> collaboratorData = new HashMap<>();
                                    collaboratorData.put("email", collaborator.getEmail());
                                    workoutDoc.collection("collaborators").document(collaborator.getEmail()).set(collaboratorData);

                                    db.collection("users").document(collaborator.getEmail()).collection("workouts").document(workoutId).set(userWorkoutData);
                                }

                                db.collection("users").document(mLocalUser.getEmail()).collection("workouts").document(workoutId).set(userWorkoutData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Successfully created " + workoutName, Toast.LENGTH_SHORT).show();
                                                sendData.createWorkoutPressed();
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    /**
     * Populates the friendsList with all friends whose username starts with the string in editText_Collaborators
     * that haven't already been added
     */
    private void populateFriendsList() {
        String filterSequence = editText_CollaboratorName.getText().toString();
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

    private void addExercise(String exerciseName) {
        exercises.add(exerciseName);
        exerciseListAdapter.notifyItemInserted(exercises.size()-1);
    }

    private void addFriend(User friend) {
        addedFriends.add(friend);
        addedFriendsAdapter.notifyItemInserted(addedFriends.size() - 1);
        Toast.makeText(getContext(), "Successfully shared with " + friend.getUsername(), Toast.LENGTH_SHORT).show();
        populateFriendsList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ICreateWorkout) {
            sendData = (ICreateWorkout) context;
        } else {
            throw new RuntimeException(getContext() + " must implement ICreateWorkout interface");
        }
    }
}