package com.example.friendlyfit.Workouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.friendlyfit.R;
import com.example.friendlyfit.Tags;
import com.example.friendlyfit.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewWorkoutsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewWorkoutsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCAL_USER = "localUser";

    private User mLocalUser;
    private Button button_CreateNewWorkout;
    private Button button_LogSelectedWorkout;
    private Button button_ShareSelectedWorkout;

    private ArrayList<String> exercises;
    private RecyclerView recyclerView_Exercises;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_Exercises;
    private ExerciseListAdapter exerciseListAdapter;

    private ArrayList<Workout> workouts;
    private RecyclerView recyclerView_Workouts;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_Workouts;
    private WorkoutListAdapter workoutListAdapter;

    private IHandleWorkouts sendData;
    private FirebaseFirestore db;
    private Workout currentSelectedWorkout;
    private int currentSelectedWorkoutIndex;

    public ViewWorkoutsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param localUser The current authorized user.
     * @return A new instance of fragment ViewWorkoutsFragment.
     */
    public static ViewWorkoutsFragment newInstance(User localUser) {
        ViewWorkoutsFragment fragment = new ViewWorkoutsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_view_workouts, container, false);

        workouts = new ArrayList<>();
        recyclerView_Workouts = rootView.findViewById(R.id.recyclerView_ViewWorkoutsWorkoutsList);
        recyclerViewLayoutManager_Workouts = new LinearLayoutManager(getContext());
        recyclerView_Workouts.setLayoutManager(recyclerViewLayoutManager_Workouts);
        workoutListAdapter = new WorkoutListAdapter(workouts, mLocalUser);
        recyclerView_Workouts.setAdapter(workoutListAdapter);

        exercises = new ArrayList<>();
        recyclerView_Exercises = rootView.findViewById(R.id.recyclerView_ViewWorkoutsExercisesList);
        recyclerViewLayoutManager_Exercises = new LinearLayoutManager(getContext());
        recyclerView_Exercises.setLayoutManager(recyclerViewLayoutManager_Exercises);
        exerciseListAdapter = new ExerciseListAdapter(exercises, Tags.VIEW_WORKOUTS_FRAGMENT_TAG);
        recyclerView_Exercises.setAdapter(exerciseListAdapter);

        button_CreateNewWorkout = rootView.findViewById(R.id.button_ViewWorkoutsCreateWorkout);
        button_LogSelectedWorkout = rootView.findViewById(R.id.button_ViewWorkoutsLogSelectedWorkout);
        button_ShareSelectedWorkout = rootView.findViewById(R.id.button_ViewWorkoutsShareSelectedWorkout);

        // initialize current selected workout values to "bad" values
        currentSelectedWorkout = null;
        currentSelectedWorkoutIndex = -1;

        button_CreateNewWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.createNewWorkoutPressed();
            }
        });

        button_LogSelectedWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSelectedWorkout == null) {
                    Toast.makeText(getContext(), "Please select a workout", Toast.LENGTH_SHORT).show();
                } else {
                    View dialogView = inflater.inflate(R.layout.dialog_input_workout_length, null);
                    EditText editText_Minutes = dialogView.findViewById(R.id.editText_DialogInputWorkoutLengthWorkoutTime);
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setView(dialogView)
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String minutes = editText_Minutes.getText().toString();
                                    if (minutes.equals("")) {
                                        Toast.makeText(getContext(), "Please enter a number greater than 0", Toast.LENGTH_SHORT).show();
                                    } else {
                                        logWorkout(Integer.parseInt(minutes));
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }
        });

        button_ShareSelectedWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement logic to get the document id for the selected workout
                if (currentSelectedWorkout == null) {
                    Toast.makeText(getContext(), "Please select a workout", Toast.LENGTH_SHORT).show();
                } else if (currentSelectedWorkout.getCreatorEmail().equals(mLocalUser.getEmail())) {
                    sendData.shareSelectedWorkoutPressed(currentSelectedWorkout.getWorkoutDocumentId());
                } else {
                    Toast.makeText(getContext(), "Cannot share a workout that is shared with you", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getAllWorkouts();

        return rootView;
    }

    public void deleteWorkout(int position) {
        if (position == currentSelectedWorkoutIndex) {
            currentSelectedWorkout = null;
            currentSelectedWorkoutIndex = -1;
            int numExercises = exercises.size();
            exercises.clear();
            exerciseListAdapter.notifyItemRangeRemoved(0, numExercises);
        }

        if (currentSelectedWorkoutIndex > position) {
            currentSelectedWorkoutIndex -= 1;
        }

        Workout deletedWorkout = workouts.remove(position);
        workoutListAdapter.notifyItemRemoved(position);

        db.collection("users").document(mLocalUser.getEmail()).collection("workouts").document(deletedWorkout.getWorkoutDocumentId()).delete();

        // if the creator of the workout deletes the workout, then remove the workout from all collaborators as well
        // otherwise remove the current user from the list of collaborators
        if (mLocalUser.getEmail().equals(deletedWorkout.getCreatorEmail())) {
            db.collection("workouts").document(deletedWorkout.getWorkoutDocumentId()).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Successfully deleted " + deletedWorkout.getWorkoutName(), Toast.LENGTH_SHORT).show();
                        }
                    });
            db.collection("workouts").document(deletedWorkout.getWorkoutDocumentId()).collection("collaborators").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String collaboratorEmail = documentSnapshot.getString("email");
                                db.collection("users").document(collaboratorEmail).collection("workouts").document(deletedWorkout.getWorkoutDocumentId()).delete();
                            }
                        }
                    });
        } else {
            db.collection("workouts").document(deletedWorkout.getWorkoutDocumentId()).collection("collaborators").document(mLocalUser.getEmail()).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Successfully deleted " + deletedWorkout.getWorkoutName(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void workoutSelected(int position) {
        if (currentSelectedWorkout != null) {
            currentSelectedWorkout.setSelected(false);
            workoutListAdapter.notifyItemChanged(currentSelectedWorkoutIndex);
        }
        workouts.get(position).setSelected(true);
        currentSelectedWorkout = workouts.get(position);
        workoutListAdapter.notifyItemChanged(position);
        currentSelectedWorkoutIndex = position;

        exercises.clear();
        exercises.addAll(currentSelectedWorkout.getExercises());
        exerciseListAdapter.notifyDataSetChanged();
    }

    private void logWorkout(int minutes) {
        LoggedWorkout loggedWorkout = new LoggedWorkout(currentSelectedWorkout.getWorkoutDocumentId(), minutes);
        db.collection("users").document(mLocalUser.getEmail()).collection("loggedWorkouts").add(loggedWorkout)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Successfully Logged " + currentSelectedWorkout.getWorkoutName(), Toast.LENGTH_SHORT).show();
                        sendData.logSelectedWorkoutPressed(currentSelectedWorkout.getWorkoutDocumentId(), minutes);
                    }
                });
    }

    private void getAllWorkouts() {
        db.collection("users").document(mLocalUser.getEmail()).collection("workouts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String workoutDocumentReferenceId = documentSnapshot.getString("workoutDocumentReferenceId");
                            getAllExercisesForWorkout(workoutDocumentReferenceId);
                        }
                    }
                });
    }

    private void getAllExercisesForWorkout(String workoutDocumentReferenceId) {
        DocumentReference workoutDoc = db.collection("workouts").document(workoutDocumentReferenceId);
        workoutDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String workoutName = documentSnapshot.getString("workoutName");
                String creatorEmail = documentSnapshot.getString("creatorEmail");
                workoutDoc.collection("exercises").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<String> exercises = new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String exercise = documentSnapshot.getString("exerciseName");
                                    exercises.add(exercise);
                                }
                                Workout workout = new Workout(workoutDocumentReferenceId, workoutName, exercises, creatorEmail);
                                workouts.add(workout);
                                workoutListAdapter.notifyItemInserted(workouts.size() - 1);
                            }
                        });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IHandleWorkouts) {
            sendData = (IHandleWorkouts) context;
        } else {
            throw new RuntimeException(getContext() + " must implement IHandleWorkouts interface");
        }
    }
}