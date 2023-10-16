package com.example.friendlyfit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonPanelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonPanelFragment extends Fragment implements View.OnClickListener {

    private Button button_Home;
    private Button button_Workouts;
    private Button button_Friends;
    private Button button_Profile;
    private IButtonPanelNavigation sendData;

    public ButtonPanelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ButtonPanelFragment.
     */
    public static ButtonPanelFragment newInstance() {
        ButtonPanelFragment fragment = new ButtonPanelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_button_panel, container, false);

        button_Home = rootView.findViewById(R.id.button_ButtonPanelHome);
        button_Workouts = rootView.findViewById(R.id.button_ButtonPanelWorkouts);
        button_Friends = rootView.findViewById(R.id.button_ButtonPanelFriends);
        button_Profile = rootView.findViewById(R.id.button_ButtonPanelProfile);

        button_Home.setOnClickListener(this);
        button_Workouts.setOnClickListener(this);
        button_Friends.setOnClickListener(this);
        button_Profile.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IButtonPanelNavigation) {
            sendData = (IButtonPanelNavigation) context;
        } else {
            throw new RuntimeException(getContext() + " must implement IButtonPanelNavigation interface");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_ButtonPanelHome) {
            sendData.homeButtonPressed();
        } else if (view.getId() == R.id.button_ButtonPanelWorkouts) {
            sendData.workoutsButtonPressed();
        } else if (view.getId() == R.id.button_ButtonPanelFriends) {
            sendData.friendsButtonPressed();
        } else if (view.getId() == R.id.button_ButtonPanelProfile) {
            sendData.profileButtonPressed();
        }
    }
}