package com.example.friendlyfit.Workouts;

public interface IAddedFriendsAdapterListener {
    /**
     * @param position the position of the chip whose close button was pressed in the added friends adapter
     * @param fragmentTag the tag of the fragment the added friends adapter is a part of
     */
    void chipCloseIconPressed(int position, String fragmentTag);
}
