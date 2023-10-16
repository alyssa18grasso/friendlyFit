package com.example.friendlyfit.Workouts;

public interface IWorkoutFriendsRecyclerViewListener {
    // the int is the position in the array of the recycler view item that was pressed/clicked
    // the fragmentTag is the fragment that the recycler view that invoked this method is attached to
    void friendClicked(int position, String fragmentTag);
}
