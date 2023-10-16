package com.example.friendlyfit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.friendlyfit.Workouts.ExerciseListAdapter;
import com.example.friendlyfit.Workouts.LoggedWorkout;
import com.google.firebase.Timestamp;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.futured.donut.DonutProgressView;
import app.futured.donut.DonutSection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView textView_Percentage;
    private DonutProgressView donut_Progress;
    private RecyclerView recyclerView_LastWorkout;
    private TextView textView_LastWorkoutName;
    private TextView textView_DashboardDate;

    private TextView textView_On;
    private LoggedWorkout loggedWorkout;

    private double percentageOfGoal;
    private String name;
    private ArrayList<String> exercises;
    private RecyclerView.LayoutManager recyclerViewLayoutManager_Exercises;
    private ExerciseListAdapter exerciseListAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public DashboardFragment(double percentageOfGoal,
                             LoggedWorkout loggedWorkout,
                             String name,
                             ArrayList<String> exercises) {
        this.percentageOfGoal = percentageOfGoal;
        this.loggedWorkout = loggedWorkout;
        this.name = name;
        this.exercises = exercises;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard,
                container, false);
        int percentageInt = (int) Math.round(percentageOfGoal);
        String percentageString = Integer.toString(percentageInt);

        textView_Percentage = rootView.findViewById(R.id.textView_Percentage);
        textView_Percentage.setText(percentageString + getString(R.string.label_Percentage));

        donut_Progress = rootView.findViewById(R.id.donut_Progress);
        donut_Progress.setCap(100);
        ArrayList<DonutSection> sections = new ArrayList<DonutSection>();
        sections.add(new DonutSection("Progress", Color.CYAN, percentageInt));

        donut_Progress.submitData(sections);

        textView_On = rootView.findViewById(R.id.textView_On);
        textView_On.setText("");

        if (loggedWorkout != null) {
            textView_LastWorkoutName = rootView.findViewById(R.id.textView_LastWorkoutName);
            textView_LastWorkoutName.setText(this.name);
            textView_DashboardDate = rootView.findViewById(R.id.textView_DashboardDate);
            SimpleDateFormat sdfOutput = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
            String formatted = sdfOutput.format(loggedWorkout.getDate());
            textView_DashboardDate.setText(formatted);
            textView_On.setText("on");
        }

        recyclerView_LastWorkout = rootView.findViewById(R.id.recyclerView_LastWorkout);
        recyclerViewLayoutManager_Exercises = new LinearLayoutManager(getContext());
        recyclerView_LastWorkout.setLayoutManager(recyclerViewLayoutManager_Exercises);
        exerciseListAdapter = new ExerciseListAdapter(exercises, Tags.VIEW_WORKOUTS_FRAGMENT_TAG);
        recyclerView_LastWorkout.setAdapter(exerciseListAdapter);

        return rootView;
    }

}