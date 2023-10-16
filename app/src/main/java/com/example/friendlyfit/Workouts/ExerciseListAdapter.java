package com.example.friendlyfit.Workouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlyfit.R;
import com.example.friendlyfit.Tags;

import java.util.ArrayList;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ViewHolder> {

    private ArrayList<String> exercises;
    // The tag of the fragment that this recycler view adapter is a part of
    private String fragmentTag;
    private IExerciseListAdapterListener sendData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_ExerciseName;
        private ImageButton imageButton_Delete;

        public ViewHolder(@NonNull View itemView, String fragmentTag) {
            super(itemView);
            if (fragmentTag.equals(Tags.CREATE_WORKOUT_FRAGMENT_TAG)) {
                textView_ExerciseName = itemView.findViewById(R.id.textView_CreateWorkoutExerciseListRowExerciseName);
                imageButton_Delete = itemView.findViewById(R.id.imageButton_CreateWorkoutExerciseListRowDelete);
            } else if (fragmentTag.equals(Tags.VIEW_WORKOUTS_FRAGMENT_TAG)) {
                textView_ExerciseName = itemView.findViewById(R.id.textView_ViewWorkoutsExerciseListRowExerciseName);
            }
        }

        public TextView getTextView_ExerciseName() {
            return textView_ExerciseName;
        }

        public ImageButton getButton_Delete() {
            return imageButton_Delete;
        }
    }

    public ExerciseListAdapter(ArrayList<String> exercises, String fragmentTag) {
        this.exercises = exercises;
        this.fragmentTag = fragmentTag;
    }

    @NonNull
    @Override
    public ExerciseListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (fragmentTag.equals(Tags.CREATE_WORKOUT_FRAGMENT_TAG)) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.create_workout_exercise_list_row, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_workouts_exercise_list_row, parent, false);
        }

        Context context = view.getContext();
        if (context instanceof IExerciseListAdapterListener) {
            sendData = (IExerciseListAdapterListener) context;
        } else {
            throw new RuntimeException(context + " must implement IExerciseListAdapterListener interface");
        }
        return new ExerciseListAdapter.ViewHolder(view, fragmentTag);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListAdapter.ViewHolder holder, int position) {
        holder.getTextView_ExerciseName().setText(exercises.get(position));
        if (fragmentTag.equals(Tags.CREATE_WORKOUT_FRAGMENT_TAG)) {
            holder.getButton_Delete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendData.exerciseListDeletePressed(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }
}
