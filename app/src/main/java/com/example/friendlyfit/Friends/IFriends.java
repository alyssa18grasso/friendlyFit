package com.example.friendlyfit.Friends;

import com.example.friendlyfit.User;

public interface IFriends {
    void chatFriend(User curFriend);

    void myFriends();

    void myFriendRequests();

    void discoverFriends();

    void acceptRequest(User curFriend);

    void declineRequest(User curFriend);

    void sendRequest(User curFriend);

    void goToFriendProfile(User curFriend, String from);
}
