package com.example.friendlyfit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User userInfo;

    private ImageView imageView_RegisterProfile;

    private EditText editText_ProfileName;
    private EditText editText_FavWorkout;
    private EditText editText_GoalNumber;
    private Spinner spinner_Numerator;
    private Spinner spinner_Denominator;
    private EditText editText_Bio;

    private Button button_RegisterProfile;

    private IEditProfile sendData;


    public RegisterProfileFragment() {
        // Required empty public constructor
    }

    public RegisterProfileFragment(User userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterProfileFragment newInstance(String param1, String param2) {
        RegisterProfileFragment fragment = new RegisterProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_register_profile,
                container, false);

        imageView_RegisterProfile = rootView.findViewById(R.id.imageView_RegisterProfile);
        editText_ProfileName = rootView.findViewById(R.id.editText_RegisterProfileName);
        editText_FavWorkout = rootView.findViewById(R.id.editText_RegisterFavWorkout);
        editText_GoalNumber = rootView.findViewById(R.id.editText_RegisterGoalNumber);
        spinner_Numerator = rootView.findViewById(R.id.spinner_RegisterNumerator);
        spinner_Denominator = rootView.findViewById(R.id.spinner_RegisterDenominator);
        editText_Bio = rootView.findViewById(R.id.editText_RegisterBio);
        button_RegisterProfile = rootView.findViewById(R.id.button_RegisterProfile);

        editText_ProfileName.setText(userInfo.getUsername());
        editText_FavWorkout.setText(userInfo.getFavoriteExercise());
        editText_GoalNumber.setText(Integer.toString(userInfo.getGoalNumber()));
        setSelectedItem(spinner_Numerator, getResources().getStringArray(R.array.stringArray_Label1), userInfo.getGoalNumerator());
        setSelectedItem(spinner_Denominator, getResources().getStringArray(R.array.stringArray_Label2), userInfo.getGoalDenominator());
        editText_Bio.setText(userInfo.getBio());

        if (!userInfo.getProfilePictureUri().equals("")) {
            Glide.with(rootView)
                    .load(userInfo.getProfilePictureUri())
                    .centerCrop()
                    .into(imageView_RegisterProfile);
        }




        imageView_RegisterProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInfo.setProfilePictureUri(userInfo.getProfilePictureUri());
                userInfo.setUsername(editText_ProfileName.getText().toString());
                userInfo.setFavoriteExercise(editText_FavWorkout.getText().toString());
                userInfo.setGoalNumber(Integer.parseInt(editText_GoalNumber.getText().toString()));
                userInfo.setGoalNumerator(spinner_Numerator.getSelectedItem().toString());
                userInfo.setGoalDenominator(spinner_Denominator.getSelectedItem().toString());
                userInfo.setBio(editText_Bio.getText().toString());
                sendData.startCamera(userInfo, "register");
            }
        });

        button_RegisterProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo.getProfilePictureUri().equals("")) {
                    Toast.makeText(getContext(),
                            "Please take a profile Picture",
                            Toast.LENGTH_LONG).show();
                } else if (editText_ProfileName.getText().toString().equals("")) {
                    Toast.makeText(getContext(),
                            "Please enter a username.",
                            Toast.LENGTH_LONG).show();
                } else if (editText_FavWorkout.getText().toString().equals("")) {
                    Toast.makeText(getContext(),
                            "Please enter a favorite workout.",
                            Toast.LENGTH_LONG).show();
                } else if (editText_GoalNumber.getText().toString().equals("")
                        || Integer.parseInt(editText_GoalNumber.getText().toString()) == 0) {
                    Toast.makeText(getContext(),
                            "Please enter a valid number for your goal.",
                            Toast.LENGTH_LONG).show();
                } else if (spinner_Numerator.getSelectedItem().toString().equals("days")
                        && spinner_Denominator.getSelectedItem().toString().equals("day")) {
                    Toast.makeText(getContext(),
                            "Cannot have a goal of X days in a day.",
                            Toast.LENGTH_LONG).show();
                } else if (editText_Bio.getText().toString().equals("")) {
                    Toast.makeText(getContext(),
                            "Please enter a Bio.",
                            Toast.LENGTH_LONG).show();
                } else {
                    userInfo.setProfilePictureUri(userInfo.getProfilePictureUri());
                    userInfo.setUsername(editText_ProfileName.getText().toString());
                    userInfo.setFavoriteExercise(editText_FavWorkout.getText().toString());
                    userInfo.setGoalNumber(Integer.parseInt(editText_GoalNumber.getText().toString()));
                    userInfo.setGoalNumerator(spinner_Numerator.getSelectedItem().toString());
                    userInfo.setGoalDenominator(spinner_Denominator.getSelectedItem().toString());
                    userInfo.setBio(editText_Bio.getText().toString());
                    sendData.updateUserProfile(userInfo);
                    sendData.toHomeDashboard();
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IEditProfile) {
            sendData = (IEditProfile) context;
        } else {
            throw new RuntimeException(context + " must implement IEditProfile interface");
        }
    }

    public void setSelectedItem(Spinner spinner, String[] stringArray, String goalString) {
        List<String> myArrayList = Arrays.asList(stringArray);
        if (myArrayList.contains(goalString)) {
            int i = myArrayList.indexOf(goalString);
            spinner.setSelection(i);
        }
    }
}