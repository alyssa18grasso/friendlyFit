package com.example.friendlyfit.Workouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendlyfit.R;
import com.example.friendlyfit.User;

import java.util.ArrayList;

public class WorkoutListAdapter extends RecyclerView.Adapter<WorkoutListAdapter.ViewHolder> {

    private ArrayList<Workout> workouts;
    private IWorkoutListAdapterListener sendData;
    private User localUser;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_WorkoutName;
        private ImageButton imageButton_Delete;

        public ViewHolder(@NonNull View itemView, IWorkoutListAdapterListener sendData) {
            super(itemView);

            textView_WorkoutName = itemView.findViewById(R.id.textView_ViewWorkoutsWorkoutsListRowWorkoutName);
            imageButton_Delete = itemView.findViewById(R.id.imageButton_ViewWorkoutsWorkoutsListDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        sendData.workoutSelected(getAdapterPosition());
                    }
                }
            });
        }

        public TextView getTextView_WorkoutName() {
            return textView_WorkoutName;
        }

        public ImageButton getImageButton_Delete() {
            return imageButton_Delete;
        }
    }

    public WorkoutListAdapter(ArrayList<Workout> workouts, User localUser) {
        this.workouts = workouts;
        this.localUser = localUser;
    }

    @NonNull
    @Override
    public WorkoutListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 3:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_workouts_workouts_list_selected_shared_row, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_workouts_workouts_list_selected_row, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_workouts_workouts_list_shared_row, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_workouts_workouts_list_row, parent, false);
                break;
        }

        Context context = parent.getContext();
        if (context instanceof IWorkoutListAdapterListener) {
            sendData = (IWorkoutListAdapterListener) context;
        } else {
            throw new RuntimeException(context + " must implement IWorkoutListAdapterListener interface");
        }

        return new WorkoutListAdapter.ViewHolder(view, sendData);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutListAdapter.ViewHolder holder, int position) {
        holder.getTextView_WorkoutName().setText(workouts.get(position).getWorkoutName());
        holder.getImageButton_Delete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog confirmDeleteDialog = new AlertDialog.Builder(view.getContext())
                        .setCancelable(true)
                        .setTitle("Confirm Delete")
                        .setMessage(String.format("Are you sure you want to delete %s? This action cannot be undone.", workouts.get(holder.getAdapterPosition()).getWorkoutName()))
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendData.workoutListDeletePressed(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                confirmDeleteDialog.show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (workouts.get(position).isSelected()) {
            if (!workouts.get(position).getCreatorEmail().equals(localUser.getEmail())) {
                // View type is 3 if this workout is shared with the current user and selected
                return 3;
            }
            // View type is 2 if this workout is selected
            return 2;
        } else if (!workouts.get(position).getCreatorEmail().equals(localUser.getEmail())) {
            // View type is 1 if this workout is shared with the current user
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }
}
