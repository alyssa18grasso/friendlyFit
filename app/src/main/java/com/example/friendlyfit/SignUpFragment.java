package com.example.friendlyfit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    private IRegisterUser sendData;
    private EditText editText_Name;
    private EditText editText_Username;
    private EditText editText_Email;
    private EditText editText_Password;
    private Button button_SignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        editText_Name = rootView.findViewById(R.id.editText_SignUpName);
        editText_Username = rootView.findViewById(R.id.editText_SignUpUsername);
        editText_Email = rootView.findViewById(R.id.editText_SignUpEmail);
        editText_Password = rootView.findViewById(R.id.editText_SignUpPassword);
        button_SignUp = rootView.findViewById(R.id.button_SignUpSignUp);

        button_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editText_Username.getText().toString();
                String email = editText_Email.getText().toString();
                String password = editText_Password.getText().toString();

                if (editText_Name.getText().toString().equals("")) {
                    editText_Name.requestFocus();
                    editText_Name.setError("Please enter your name");
                } else if (username.equals("")) {
                    editText_Username.requestFocus();
                    editText_Username.setError("Please enter a username");
                } else if (email.equals("")) {
                    editText_Email.requestFocus();
                    editText_Email.setError("Please enter your email");
                } else if (password.equals("")) {
                    editText_Password.requestFocus();
                    editText_Password.setError("Please enter a password");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editText_Email.requestFocus();
                    editText_Email.setError("Please enter a valid email address");
                } else {
                    DocumentReference userDoc = db.collection("users").document(email);
                    userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                editText_Email.requestFocus();
                                editText_Email.setError("A user with this email already exists");
                            } else {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Toast.makeText(getContext(), "Register Successful!", Toast.LENGTH_SHORT).show();
                                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(username)
                                                        .build();

                                                currentUser.updateProfile(profileChangeRequest)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                User newUser = new User(email, username);
                                                                userDoc.set(newUser);
                                                                sendData.registerUser(currentUser, newUser);
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });

        return rootView;
    }

    private void createNewUser(String email, String username, String password) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IRegisterUser) {
            sendData = (IRegisterUser) context;
        } else {
            throw new RuntimeException(context + " must implement IRegisterUser interface");
        }
    }
}