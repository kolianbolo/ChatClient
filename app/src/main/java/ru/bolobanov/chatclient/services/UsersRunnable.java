package ru.bolobanov.chatclient.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.Constants;
import ru.bolobanov.chatclient.HttpHelper;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
public class UsersRunnable implements Runnable {

    private Context mContext;
    private String mBaseUrl;

    public UsersRunnable(String pBaseUrl, Context pContext) {
        mBaseUrl = pBaseUrl;
        mContext = pContext.getApplicationContext();
    }

    @Override
    public void run() {
        while (true) {
            Log.d("UsersRunnable", "run()");
            loadUsers();
            try {
                Thread.sleep(Constants.GET_USERS_PERIIOD);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void loadUsers() {
        HttpHelper helper = new HttpHelper();
        try {
            JSONObject responseJSON = new JSONObject(helper.getUsers(mBaseUrl));
            final JSONArray usersJSON = responseJSON.optJSONArray("users");
            Handler mainHandler = new Handler(mContext.getMainLooper());
            Runnable busMessage = new Runnable() {
                @Override
                public void run() {
                    BusProvider.getInstance().post(usersJSON);
                }
            };
            mainHandler.post(busMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

