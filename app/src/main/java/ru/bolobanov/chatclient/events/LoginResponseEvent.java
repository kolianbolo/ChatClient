package ru.bolobanov.chatclient.events;

import org.json.JSONObject;

/**
 * Created by Bolobanov Nikolay on 22.01.16.
 */
public class LoginResponseEvent {
    public final JSONObject mResponse;

    public LoginResponseEvent(JSONObject pResponse){
        mResponse = pResponse;
    }
}
