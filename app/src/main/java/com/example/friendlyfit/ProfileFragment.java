package com.example.friendlyfit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User userInfo;
    private int friends;

    private TextView textView_UserProfileName;
    private TextView textView_UserProfileFavWorkout;
    private TextView textView_UserProfileGoalNumber;
    private TextView textView_UserProfileGoalLabel1;
    private TextView textView_UserProfileGoalLabel2;
    private TextView textView_UserProfileBio;
    private TextView textView_UserProfileFriendsNumber;

    private ImageView imageView_EditProfile;
    private ImageView imageView_UserProfile;
    private ImageButton imageButton_Logout;

    private IUserProfile sendData;




    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(User userInfo, int friends) {
        this.userInfo = userInfo;
        this.friends = friends;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_profile,
                container, false);

        imageView_UserProfile = rootView.findViewById(R.id.imageView_UserProfile);
        imageView_EditProfile = rootView.findViewById(R.id.imageView_EditProfile);
        imageButton_Logout = rootView.findViewById(R.id.imageButton_Logout);
        textView_UserProfileName = rootView.findViewById(R.id.textView_UserProfileName);
        textView_UserProfileFavWorkout = rootView.findViewById(R.id.textView_UserProfileFavWorkout);
        textView_UserProfileGoalNumber = rootView.findViewById(R.id.textView_UserProfileGoalNumber);
        textView_UserProfileGoalLabel1 = rootView.findViewById(R.id.textView_UserProfileGoalLabel1);
        textView_UserProfileGoalLabel2 = rootView.findViewById(R.id.textView_UserProfileGoalLabel2);
        textView_UserProfileBio = rootView.findViewById(R.id.textView_UserProfileBio);
        textView_UserProfileFriendsNumber = rootView.findViewById(R.id.textView_UserProfileFriendsNumber);

        textView_UserProfileName.setText(userInfo.getUsername());
        textView_UserProfileFavWorkout.setText(userInfo.getFavoriteExercise());
        textView_UserProfileGoalNumber.setText(Integer.toString(userInfo.getGoalNumber()));
        textView_UserProfileGoalLabel1.setText(userInfo.getGoalNumerator());
        textView_UserProfileGoalLabel2.setText(userInfo.getGoalDenominator());
        textView_UserProfileBio.setText(userInfo.getBio());
        textView_UserProfileFriendsNumber.setText(Integer.toString(this.friends));

        if (userInfo.getProfilePictureUri() != null) {
            if (!userInfo.getProfilePictureUri().isEmpty()) {
                Glide.with(rootView)
                        .load(userInfo.getProfilePictureUri())
                        .centerCrop()
                        .into(imageView_UserProfile);
            }
        }


        imageView_EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.editProfile(userInfo);
            }
        });

        imageButton_Logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendData.logoutPressed();
                    }
                }
        );


        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IUserProfile) {
            sendData = (IUserProfile) context;
        } else {
            throw new RuntimeException(context + " must implement IUserProfile interface");
        }
    }
}