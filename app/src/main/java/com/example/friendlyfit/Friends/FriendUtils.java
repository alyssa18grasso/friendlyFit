package com.example.friendlyfit.Friends;

import static java.util.Collections.sort;

import com.example.friendlyfit.User;

import java.util.ArrayList;
import java.util.UUID;

public class FriendUtils {

    public static String generateUniqueID(ArrayList<String> chatEmails) {
        // Using Java UUID (Unique User ID) generator utility to create the IDs from the list of user emails......
        sort(chatEmails);
        String uuid = UUID.nameUUIDFromBytes(
                        chatEmails.toString().getBytes())
                .toString().substring(0,16);
        return uuid;
    }

    public static String generateChatName(ArrayList<User> users){
        String name = "";
        for(int i=0; i< users.size(); i++){
            if(i==users.size()-1){
                name+= users.get(i).getUsername();
            }else{
                name+= users.get(i).getUsername()+", ";
            }
        }
        return name;
    }
}
