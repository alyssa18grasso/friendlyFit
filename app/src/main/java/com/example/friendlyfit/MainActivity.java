package com.example.friendlyfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.friendlyfit.Friends.Chat;
import com.example.friendlyfit.Friends.ChatFragment;
import com.example.friendlyfit.Friends.ChatRecord;
import com.example.friendlyfit.Friends.DiscoverFriendsFragment;
import com.example.friendlyfit.Friends.FriendRequestsFragment;
import com.example.friendlyfit.Friends.FriendUtils;
import com.example.friendlyfit.Friends.FriendsFragment;
import com.example.friendlyfit.Friends.IChat;
import com.example.friendlyfit.Friends.IFriends;
import com.example.friendlyfit.Workouts.CreateWorkoutFragment;
import com.example.friendlyfit.Workouts.IAddedFriendsAdapterListener;
import com.example.friendlyfit.Workouts.ICreateWorkout;
import com.example.friendlyfit.Workouts.IExerciseListAdapterListener;
import com.example.friendlyfit.Workouts.IHandleWorkouts;
import com.example.friendlyfit.Workouts.IShareWorkout;
import com.example.friendlyfit.Workouts.IWorkoutFriendsRecyclerViewListener;
import com.example.friendlyfit.Workouts.IWorkoutListAdapterListener;
import com.example.friendlyfit.Workouts.LoggedWorkout;
import com.example.friendlyfit.Workouts.ShareWorkoutFragment;
import com.example.friendlyfit.Workouts.ViewWorkoutsFragment;
import com.example.friendlyfit.Workouts.Workout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements IRegisterUser,
        ILoginUser,
        IButtonPanelNavigation,
        IHandleWorkouts,
        ILanding,
        IShareWorkout,
        IWorkoutFriendsRecyclerViewListener,
        IExerciseListAdapterListener,
        ICreateWorkout,
        IUserProfile,
        IEditProfile,
        IAddedFriendsAdapterListener,
        IWorkoutListAdapterListener,
        ICamera,
        IDisplayPhoto,
        IFriends,
        IChat,
        IFriendProfile {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mCurrentUser;
    private User mLocalCurrentUser;
    private static final int PERMISSIONS_CODE = 0x100;
    private FirebaseStorage storage;

    private String profileTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = mAuth.getCurrentUser();
        populateScreen();
    }

    private void populateScreen() {
        if(mCurrentUser != null) {
            populateDashboardScreen();
        }
        else {
            populateLandingScreen();
        }
    }

    private void populateLandingScreen() {
        setTitle(Titles.LANDING_PAGE_TITLE);
        // The null, null are the args to new instance. Change/Remove as necessary
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, LandingFragment.newInstance(null, null))
                .commit();
    }

    private void addButtonPanel() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.button_panel_fragment_root_container, ButtonPanelFragment.newInstance(), Tags.BUTTON_PANEL_FRAGMENT_TAG)
                .commit();
    }

    private void removeButtonPanel() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Tags.BUTTON_PANEL_FRAGMENT_TAG);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void populateDashboardScreen() {
        setTitle(Titles.DASHBOARD_TITLE);
        db.collection("users").document(mCurrentUser.getEmail()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                // The null, null are the args to new instance. Change/Remove as necessary
                                mLocalCurrentUser = documentSnapshot.toObject(User.class);
                                int goalNumber = mLocalCurrentUser.getGoalNumber();
                                String goalNumerator = mLocalCurrentUser.getGoalNumerator();
                                String goalDenominator = mLocalCurrentUser.getGoalDenominator();
                                findGoalPercentageForDashboard(goalNumber, goalNumerator, goalDenominator);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        mLocalCurrentUser = task.getResult()
                                .toObject(User.class);
                    }
                });
    }

    @Override
    public void registerUser(FirebaseUser currentUser, User user) {
        mCurrentUser = currentUser;
        mLocalCurrentUser = user;
        setTitle(Titles.REGISTER_PROFILE_TITLE);
        // The tag is so that we can come back to this fragment when uploading a profile pic
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new RegisterProfileFragment(user), "RegisterProfile")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void loginPressed(FirebaseUser currentUser) {
        mCurrentUser = currentUser;
        populateDashboardScreen();
    }

    @Override
    public void homeButtonPressed() {
        populateDashboardScreen();
    }

    @Override
    public void workoutsButtonPressed() {
        setTitle(Titles.VIEW_WORKOUTS_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, ViewWorkoutsFragment.newInstance(mLocalCurrentUser), Tags.VIEW_WORKOUTS_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void friendsButtonPressed() {
        setTitle(Titles.FRIENDS_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new FriendsFragment(mLocalCurrentUser))
                .commit();
    }

    @Override
    public void profileButtonPressed() {

        db.collection("users")
                .document(mCurrentUser.getEmail())
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            int friends = 0;
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                friends += 1;
                            }
                            findUserProfile(friends);
                        }
                    }
                });
    }

    public void findUserProfile(int friends) {
        db.collection("users")
                .document(mCurrentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User userInfo = documentSnapshot.toObject(User.class);
                            setProfileFragment(userInfo, friends);
                        }
                    }
                });
    }

    public void setProfileFragment(User userInfo, int friends) {
        setTitle(Titles.PROFILE_TITLE);
        updateLocalCurrentUser(userInfo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new ProfileFragment(userInfo, friends))
                .commit();
    }

    @Override
    public void logSelectedWorkoutPressed(String workoutId, int timeInMinutes) {
        // implement logic for logging the selected workout
    }

    @Override
    public void shareSelectedWorkoutPressed(String workoutId) {
        setTitle(Titles.SHARE_WORKOUT_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, ShareWorkoutFragment.newInstance(mLocalCurrentUser, workoutId), Tags.SHARE_WORKOUT_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
        removeButtonPanel();
    }

    @Override
    public void createNewWorkoutPressed() {
        setTitle(Titles.CREATE_WORKOUT_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, CreateWorkoutFragment.newInstance(mLocalCurrentUser), Tags.CREATE_WORKOUT_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
        removeButtonPanel();
    }

    @Override
    public void login() {
        setTitle(Titles.LOGIN_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, LoginFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @Override
    public void signup() {
        setTitle(Titles.SIGN_UP_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, SignUpFragment.newInstance())
                .addToBackStack("")
                .commit();
    }

    @Override
    public void shareWorkoutBackPressed() {
        setTitle(Titles.VIEW_WORKOUTS_TITLE);
        getSupportFragmentManager().popBackStack();
        addButtonPanel();
    }

    @Override
    public void sharePressed() {
        // I don't know if any more logic needs to be done here vs having it in the fragment
        setTitle(Titles.VIEW_WORKOUTS_TITLE);
        getSupportFragmentManager().popBackStack();
        addButtonPanel();
    }

    @Override
    public void friendClicked(int position, String fragmentTag) {
        if (fragmentTag.equals(Tags.SHARE_WORKOUT_FRAGMENT_TAG)) {
            ShareWorkoutFragment shareWorkoutFragment =
                    (ShareWorkoutFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
            shareWorkoutFragment.friendClicked(position);

        } else if (fragmentTag.equals(Tags.CREATE_WORKOUT_FRAGMENT_TAG)) {
            CreateWorkoutFragment createWorkoutFragment =
                    (CreateWorkoutFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
            createWorkoutFragment.friendClicked(position);
        }
    }

    @Override
    public void workoutListDeletePressed(int position) {
        ViewWorkoutsFragment viewWorkoutsFragment =
                (ViewWorkoutsFragment) getSupportFragmentManager().findFragmentByTag(Tags.VIEW_WORKOUTS_FRAGMENT_TAG);
        viewWorkoutsFragment.deleteWorkout(position);
    }

    @Override
    public void workoutSelected(int position) {
        ViewWorkoutsFragment viewWorkoutsFragment =
                (ViewWorkoutsFragment) getSupportFragmentManager().findFragmentByTag(Tags.VIEW_WORKOUTS_FRAGMENT_TAG);
        viewWorkoutsFragment.workoutSelected(position);
    }

    @Override
    public void createWorkoutBackPressed() {
        setTitle(Titles.VIEW_WORKOUTS_TITLE);
        getSupportFragmentManager().popBackStack();
        addButtonPanel();
    }

    @Override
    public void createWorkoutPressed() {
        // More logic may be needed, or logic can go in fragment
        setTitle(Titles.VIEW_WORKOUTS_TITLE);
        getSupportFragmentManager().popBackStack();
        addButtonPanel();
    }

    @Override
    public void chipCloseIconPressed(int position, String fragmentTag) {
        if (fragmentTag.equals(Tags.SHARE_WORKOUT_FRAGMENT_TAG)) {
            ShareWorkoutFragment shareWorkoutFragment =
                    (ShareWorkoutFragment) getSupportFragmentManager().findFragmentByTag(Tags.SHARE_WORKOUT_FRAGMENT_TAG);
            shareWorkoutFragment.removeAddedFriend(position);
        } else if (fragmentTag.equals(Tags.CREATE_WORKOUT_FRAGMENT_TAG)) {
            CreateWorkoutFragment createWorkoutFragment =
                    (CreateWorkoutFragment) getSupportFragmentManager().findFragmentByTag(Tags.CREATE_WORKOUT_FRAGMENT_TAG);
             createWorkoutFragment.removeAddedFriend(position);
        }
    }

    @Override
    public void exerciseListDeletePressed(int position) {
        CreateWorkoutFragment createWorkoutFragment =
                (CreateWorkoutFragment) getSupportFragmentManager().findFragmentByTag(Tags.CREATE_WORKOUT_FRAGMENT_TAG);
        createWorkoutFragment.deleteExercise(position);
    }

    @Override
    public void editProfile(User userInfo) {
        setTitle(Titles.EDIT_PROFILE_TITLE);
        removeButtonPanel();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new EditProfileFragment(userInfo))
                .commit();
    }

    @Override
    public void backToUserProfile() {
        this.profileButtonPressed();
        addButtonPanel();
    }

    @Override
    public void updateUserProfile(User userInfo) {
        DocumentReference userDocRef = db.collection("users")
                .document(mCurrentUser.getEmail());
        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("bio", userInfo.getBio());
        userInfoMap.put("favoriteExercise", userInfo.getFavoriteExercise());
        userInfoMap.put("goalNumber", userInfo.getGoalNumber());
        userInfoMap.put("goalDenominator", userInfo.getGoalDenominator());
        userInfoMap.put("goalNumerator", userInfo.getGoalNumerator());
        userInfoMap.put("profilePictureUri", userInfo.getProfilePictureUri());
        userInfoMap.put("username", userInfo.getUsername());

        userDocRef.update(userInfoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateLocalCurrentUser(userInfo);
                        Toast.makeText(MainActivity.this,
                                "User Profile Successfully Updated",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,
                                "Unable to Update Profile. Try again later",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void startCamera(User userInfo, String tag) {
        //        getting the instance of FirebaseStorage....
        storage = FirebaseStorage.getInstance();
        profileTag = tag;

//        Asking for permissions in runtime......
        Boolean cameraAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        Boolean readAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writeAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(cameraAllowed && readAllowed && writeAllowed){
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_root_container, new CameraFragment(userInfo, tag), "cameraFragment")
                    .commit();

        }else{
            requestPermissions(new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSIONS_CODE);
        }
    }

    @Override
    public void toHomeDashboard() {
        populateDashboardScreen();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>2){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_root_container, new CameraFragment(mLocalCurrentUser, this.getProfileTag()), "cameraFragment")
                    .commit();
        }else{
            Toast.makeText(this, "You must allow Camera and Storage permissions!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTakePhoto(Uri imageUri, User userInfo, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new DisplayPhotoFragment(imageUri, userInfo, tag),"displayFragment")
                .commit();
    }

    @Override
    public void backToEditProfile(User userInfo) {
        setTitle(Titles.EDIT_PROFILE_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new EditProfileFragment(userInfo))
                .commit();
    }

    @Override
    public void backToRegisterProfile(User userInfo) {
        setTitle(Titles.REGISTER_PROFILE_TITLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new RegisterProfileFragment(userInfo))
                .commit();
    }

    @Override
    public void retakePhoto(User userInfo, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new CameraFragment(userInfo, tag), "cameraFragment")
                .commit();
    }

    @Override
    public void uploadPhoto(Uri imageUri, ProgressBar progressBar, User userInfo, String tag) {
        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageReference = storage.getReference().child("images/"+imageUri.getLastPathSegment());
        UploadTask uploadImage = storageReference.putFile(imageUri);
        uploadImage.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Upload Failed! Try again!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        taskSnapshot.getStorage().getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                userInfo.setProfilePictureUri(uri.toString());
                                                updateLocalCurrentUser(userInfo);
                                                uploadSuccess(userInfo, tag);
                                            }
                                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);

                    }
                });
    }

    public void uploadSuccess(User userInfo, String tag) {
        if (tag.equals("edit")) {
            setTitle(Titles.EDIT_PROFILE_TITLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_root_container, new EditProfileFragment(userInfo))
                    .commit();
        } else if (tag.equals("register")) {
            setTitle(Titles.REGISTER_PROFILE_TITLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_root_container, new RegisterProfileFragment(userInfo))
                    .commit();
        }
    }

    public void updateLocalCurrentUser(User userInfo) {
        if (mLocalCurrentUser == null) {
            this.mLocalCurrentUser = userInfo;
        } else {
            this.mLocalCurrentUser.update(userInfo);
        }
    }

    public Timestamp getStartOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        Date now = cal.getTime();
        Timestamp timestamp_day = new Timestamp(now);
        return timestamp_day;
    }

    public Timestamp getStartOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        Date now = cal.getTime();
        Timestamp timestamp_week = new Timestamp(now);
        return timestamp_week;
    }

    public Timestamp getStartOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Date now = cal.getTime();
        Timestamp timestamp_month = new Timestamp(now);
        return timestamp_month;
    }

    public void findGoalPercentageForDashboard(int goalNumber, String goalNumerator, String goalDenominator) {
        int minutes = getGoalMinutes(goalNumber, goalNumerator);
        switch(goalDenominator) {
            case "day":
                Timestamp timestamp_day = getStartOfToday();
                getGoalCompletionInMinutes(minutes, timestamp_day);
                break;
            case "week":
                Timestamp timestamp_week = getStartOfWeek();
                if (goalNumerator.equals("days")) {
                    getGoalCompletionInDays(goalNumber, timestamp_week);
                } else {
                    getGoalCompletionInMinutes(minutes, timestamp_week);
                }
                break;
            case "month":
                Timestamp timestamp_month = getStartOfMonth();
                if (goalNumerator.equals("days")) {
                    getGoalCompletionInDays(goalNumber, timestamp_month);
                } else {
                    getGoalCompletionInMinutes(minutes, timestamp_month);
                }
                break;
            default:
                throw new RuntimeException("invalid goal " + goalDenominator);
        }
    }

    public void getGoalCompletionInDays(int days, Timestamp timestamp) {
        db.collection("users")
                .document(mCurrentUser.getEmail())
                .collection("loggedWorkouts")
                .whereGreaterThan("date", timestamp)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Integer> dates = new ArrayList<Integer>();
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                LoggedWorkout loggedWorkout = documentSnapshot.toObject(LoggedWorkout.class);
                                Date input = loggedWorkout.getDate();
                                LocalDate date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                if (!dates.contains(date.getDayOfMonth())) {
                                    dates.add(date.getDayOfMonth());
                                }
                            }
                            double percentageOfGoal = (dates.size() * 100 / days);
                            getMostRecentLoggedWorkout(percentageOfGoal);
                        }
                    }
                });
    }

    public void getGoalCompletionInMinutes(int minutes, Timestamp timestamp) {
        db.collection("users")
                .document(mCurrentUser.getEmail())
                .collection("loggedWorkouts")
                .whereGreaterThan("date", timestamp)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int sum = 0;
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                LoggedWorkout loggedWorkout = documentSnapshot.toObject(LoggedWorkout.class);
                                sum += loggedWorkout.getTimeInMinutes();
                            }
                            double percentageOfGoal = (sum * 100 / minutes);
                            getMostRecentLoggedWorkout(percentageOfGoal);
                        }
                    }
                });
    }

    public void setDashboardFragment(double percentageOfGoal, LoggedWorkout loggedWorkout, String workoutName, ArrayList<String> exercises)  {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container,
                        new DashboardFragment(percentageOfGoal, loggedWorkout, workoutName, exercises))
                .addToBackStack(null)
                .commit();
        addButtonPanel();
    }

    public int getGoalMinutes(int goalNumber, String goalNumerator) {
        if (goalNumerator.equals("minutes")) {
            return goalNumber;
        } else {
            return goalNumber * 60; // goalNumber is hours
        }
    }


    private void populateChatFragment(ArrayList<String> chatEmails) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new ChatFragment(mLocalCurrentUser, chatEmails),"newChatFragment")
                .addToBackStack(null)
                .commit();
        //remove button panel
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("buttonPanelFragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void backFromChatToFriends() {
        getSupportFragmentManager().popBackStack();
        addButtonPanel();
    }

    @Override
    public void chatFriend(User curFriend) {
        Log.d("help", "chatFriend: ");
        ArrayList<String> chatEmails = new ArrayList<>();
        final ChatRecord[] theRecord = new ChatRecord[1];
        chatEmails.add(mCurrentUser.getEmail());
        chatEmails.add(curFriend.getEmail());

//        Generate a unique value for the list of users in this chat...
        String uIDforChat = FriendUtils.generateUniqueID(chatEmails);
        Log.d("demo", "UUID: "+uIDforChat);

//        Fetch the collection of chat records from users tree for current user...
        DocumentReference chatDocRefInChatsTree = db.collection("users")
                .document(mLocalCurrentUser.getEmail())
                .collection("chats")
                .document(uIDforChat);

        chatDocRefInChatsTree
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {
                            // There is chat record there, populate the chat fragment .....
                            populateChatFragment(chatEmails);
                        } else {
                            // We need to create a new chat record ....
                            // First, we need to fetch the users in this chat, and generate the Chat Name....
                            fetchUsersInTheSelectedChat(chatEmails, uIDforChat);
                        }
                    }})
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("demo", "onFailure: "+ e.getMessage() );
                        }
                    });
    }

    private void fetchUsersInTheSelectedChat(ArrayList<String> chatEmails, String uIDforChat) {
        ArrayList<User> chatUsers = new ArrayList<>();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                if (chatEmails.contains(documentSnapshot.get("email"))) {
                                    chatUsers.add(documentSnapshot.toObject(User.class));
                                }
                            }
                            //        setting the name of the chat...
                            String chatName = FriendUtils.generateChatName(chatUsers);
                            //        Then, create a chat record in chats tree...
                            createRecordInFirebaseChatsCollection(chatName, chatEmails, uIDforChat);
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to retrieve users information", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createRecordInFirebaseChatsCollection(String chatName, ArrayList<String> chatEmails, String uIDforChat) {
        Chat newChat = new Chat(chatName, chatEmails);
        db.collection("chats")
                .add(newChat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                      On success make new chat record...
                        ChatRecord newChatRecord = new ChatRecord(chatName, documentReference.getId(), chatEmails);
                        // Now, we need to add the this document ID to users tree for all users batch update.....
                        updateChatRecordsInFirebaseUsersCollection(chatEmails, newChatRecord, uIDforChat);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("demo", "onFailure: "+e.getMessage());
                    }
                });
    }

    private void updateChatRecordsInFirebaseUsersCollection(ArrayList<String> chatEmails, ChatRecord newChatRecord, String uIDforChat) {
        WriteBatch batch = db.batch();
        for(String email: chatEmails){
            batch.set(db.collection("users")
                            .document(email)
                            .collection("chats")
                            .document(uIDforChat),
                    newChatRecord);
        }
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            populateChatFragment(chatEmails);
                        } else {
                            Toast.makeText(MainActivity.this, "An error occured! Try again!", Toast.LENGTH_SHORT).show();
                            populateScreen();
                        }
                    }
                });
    }

    @Override
    public void myFriends() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new FriendsFragment(mLocalCurrentUser))
                .commit();
    }

    @Override
    public void myFriendRequests() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new FriendRequestsFragment(mLocalCurrentUser))
                .commit();
    }

    @Override
    public void discoverFriends() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new DiscoverFriendsFragment(mLocalCurrentUser))
                .commit();
    }

    @Override
    public void acceptRequest(User curFriend) {
        // add friend into friend collection (both friends)
        // remove friend request from requests
        db.collection("users")
                .document(mLocalCurrentUser.getEmail())
                .collection("friends")
                .document(curFriend.getEmail())
                .set(curFriend);

        db.collection("users")
                .document(curFriend.getEmail())
                .collection("friends")
                .document(mLocalCurrentUser.getEmail())
                .set(mLocalCurrentUser);

        deleteFriendRequest(curFriend.getEmail());
    }

    private void deleteFriendRequest(String email) {
        db.collection("users")
                .document(mLocalCurrentUser.getEmail())
                .collection("requests")
                .document(email)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("demo", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("demo", "Error deleting document", e);
                    }
                });
                }

    @Override
    public void declineRequest(User curFriend) {
        // dont add friend into collection
        // delete request
        deleteFriendRequest(curFriend.getEmail());
    }

    @Override
    public void sendRequest(User curFriend) {
        // add request to friend's requests
        db.collection("users")
                .document(curFriend.getEmail())
                .collection("requests")
                .document(mLocalCurrentUser.getEmail())
                .set(mLocalCurrentUser);
    }

    @Override
    public void goToFriendProfile(User curFriend, String from) {
        removeButtonPanel();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_root_container, new FriendProfileFragment(curFriend, from))
                .commit();
    }

    @Override
    public void backToFriends(String from) {
        switch(from) {
            case "friends":
                addButtonPanel();
                myFriends();
                break;
            case "requests":
                addButtonPanel();
                myFriendRequests();
                break;
            case "discover":
                addButtonPanel();
                discoverFriends();
                break;
            default:
                throw new RuntimeException("Cannot specify string: " + from);
        }
    }

    public void logoutPressed() {
        mAuth.signOut();
        mCurrentUser = null;
        mLocalCurrentUser = null;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        removeButtonPanel();
        populateScreen();
    }

    public void getMostRecentLoggedWorkout(double percentageOfGoal) {
        db.collection("users")
                .document(mCurrentUser.getEmail())
                .collection("loggedWorkouts")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LoggedWorkout loggedWorkout = null;
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                loggedWorkout = documentSnapshot.toObject(LoggedWorkout.class);
                            }
                            if (loggedWorkout != null)  {
                                getWorkoutName(percentageOfGoal, loggedWorkout);
                            } else {
                                setDashboardFragment(percentageOfGoal, loggedWorkout, "", new ArrayList<String>());
                            }
                        }
                    }
                });
    }

    public void getWorkoutName(double percentageOfGoal, LoggedWorkout loggedWorkout) {
        db.collection("workouts")
                .document(loggedWorkout.getWorkoutDocumentReferenceId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().getString("workoutName");
                            getExerciseArrayList(percentageOfGoal, loggedWorkout, name);
                        }
                    }
                });
    }

    public void getExerciseArrayList(double percentageOfGoal, LoggedWorkout loggedWorkout, String name) {
        db.collection("workouts")
                .document(loggedWorkout.getWorkoutDocumentReferenceId())
                .collection("exercises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> exercises = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                String s = documentSnapshot.getString("exerciseName");
                                exercises.add(s);
                            }
                            setDashboardFragment(percentageOfGoal, loggedWorkout, name, exercises);
                        }
                    }
                });
    }

    public String getProfileTag() {
        return profileTag;
    }
}