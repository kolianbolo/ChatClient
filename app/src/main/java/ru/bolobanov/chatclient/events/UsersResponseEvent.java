package ru.bolobanov.chatclient.events;

import org.json.JSONArray;

/**
 * Created by Bolobanov Nikolay on 22.01.16.
 */
public class UsersResponseEvent {
    public final JSONArray usersArray;

    public UsersResponseEvent(JSONArray pUsers) {
        usersArray = pUsers;
    }


}
