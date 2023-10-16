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
import com.example.friendlyfit.Friends.IFriends;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User userInfo;

    private ImageButton imageButton_BackToFriends;

    private TextView textView_FriendProfileName;
    private TextView textView_FriendProfileFavWorkout;
    private TextView textView_FriendProfileGoalNumber;
    private TextView textView_FriendProfileGoalLabel1;
    private TextView textView_FriendProfileGoalLabel2;
    private TextView textView_FriendProfileBio;
    private TextView textView_FriendProfileFriendsNumber;

    private ImageView imageView_FriendProfile;

    private IFriendProfile sendData;
    private String from;

    public FriendProfileFragment() {
        // Required empty public constructor
    }

    public FriendProfileFragment(User userInfo, String from) {
        this.userInfo = userInfo;
        this.from = from;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendProfileFragment newInstance(String param1, String param2) {
        FriendProfileFragment fragment = new FriendProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_friend_profile,
                container, false);

        imageButton_BackToFriends = rootView.findViewById(R.id.imageButton_BackToFriends);
        imageView_FriendProfile = rootView.findViewById(R.id.imageView_FriendProfile);
        textView_FriendProfileName = rootView.findViewById(R.id.textView_FriendProfileName);
        textView_FriendProfileFavWorkout = rootView.findViewById(R.id.textView_FriendProfileFavWorkout);
        textView_FriendProfileGoalNumber = rootView.findViewById(R.id.textView_FriendProfileGoalNumber);
        textView_FriendProfileGoalLabel1 = rootView.findViewById(R.id.textView_FriendProfileGoalLabel1);
        textView_FriendProfileGoalLabel2 = rootView.findViewById(R.id.textView_FriendProfileGoalLabel2);
        textView_FriendProfileBio = rootView.findViewById(R.id.textView_FriendProfileBio);
        textView_FriendProfileFriendsNumber = rootView.findViewById(R.id.textView_FriendProfileFriendsNumber);

        textView_FriendProfileName.setText(userInfo.getUsername());
        textView_FriendProfileFavWorkout.setText(userInfo.getFavoriteExercise());
        textView_FriendProfileGoalNumber.setText(Integer.toString(userInfo.getGoalNumber()));
        textView_FriendProfileGoalLabel1.setText(userInfo.getGoalNumerator());
        textView_FriendProfileGoalLabel2.setText(userInfo.getGoalDenominator());
        textView_FriendProfileBio.setText(userInfo.getBio());

        if (userInfo.getProfilePictureUri() != null) {
            if (!userInfo.getProfilePictureUri().isEmpty()) {
                Glide.with(rootView)
                        .load(userInfo.getProfilePictureUri())
                        .centerCrop()
                        .into(imageView_FriendProfile);
            }
        }


        imageButton_BackToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.backToFriends(from);
            }
        });


        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFriendProfile) {
            sendData = (IFriendProfile) context;
        } else {
            throw new RuntimeException(context + " must implement IFriendProfile interface");
        }
    }
}