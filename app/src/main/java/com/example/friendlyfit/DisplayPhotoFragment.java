package com.example.friendlyfit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayPhotoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URI = "imageUri";

    private Uri imageUri;
    private User userInfo;
    private ImageView imageView_DisplayPhoto;
    private Button button_Retake;
    private Button button_Upload;
    private ProgressBar progressBar_PhotoUpload;
    private String tag;
    private IDisplayPhoto sendData;

    public DisplayPhotoFragment() {
        // Required empty public constructor
    }

    public DisplayPhotoFragment(Uri imageUri, User userInfo, String tag) {
        this.imageUri = imageUri;
        this.userInfo = userInfo;
        this.tag = tag;
    }

    // TODO: Rename and change types and number of parameters
    public static DisplayPhotoFragment newInstance(Uri imageUri) {
        DisplayPhotoFragment fragment = new DisplayPhotoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = getArguments().getParcelable(ARG_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_display_photo, container, false);
//        ProgressBar setup init.....
        progressBar_PhotoUpload = view.findViewById(R.id.progressBar_PhotoUpload);
        progressBar_PhotoUpload.setVisibility(View.GONE);

        imageView_DisplayPhoto = view.findViewById(R.id.imageView_DisplayPhoto);
        button_Retake = view.findViewById(R.id.button_Retake);
        button_Upload = view.findViewById(R.id.button_Upload);
        Glide.with(view)
                .load(imageUri)
                .centerCrop()
                .into(imageView_DisplayPhoto);

        button_Retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.retakePhoto(userInfo, tag);
            }
        });

        button_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData.uploadPhoto(imageUri, progressBar_PhotoUpload, userInfo, tag);
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IDisplayPhoto){
            sendData = (IDisplayPhoto) context;
        }else{
            throw new RuntimeException(context+" must implement IDisplayPhoto");
        }
    }
}