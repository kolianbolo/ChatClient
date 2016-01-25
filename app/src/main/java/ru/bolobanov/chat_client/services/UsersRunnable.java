package ru.bolobanov.chat_client.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.Constants;
import ru.bolobanov.chat_client.HttpHelper;
import ru.bolobanov.chat_client.events.UsersResponseEvent;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
class UsersRunnable implements Runnable {

    private final String mBaseUrl;

    public UsersRunnable(String pBaseUrl) {
        mBaseUrl = pBaseUrl;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Log.d("UsersRunnable", "run()");
                loadUsers();
                Thread.sleep(Constants.GET_USERS_PERIOD);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void loadUsers() {
        HttpHelper helper = new HttpHelper();
        try {
            JSONObject responseJSON = new JSONObject(helper.getUsers(mBaseUrl));
            final JSONArray usersJSON = responseJSON.getJSONArray("users");
            EventBus.getDefault().post(new UsersResponseEvent(usersJSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

