package ru.bolobanov.chat_client.services;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.Constants;
import ru.bolobanov.chat_client.HttpHelper;
import ru.bolobanov.chat_client.events.LoginResponseEvent;
import ru.bolobanov.chat_client.events.TextEvent;

/**
 * Created by Bolobanov Nikolay on 25.12.15.
 */
class LoginRunnable implements Runnable {

    private final String mLogin;
    private final String mPassword;
    private final String mBaseUrl;


    public LoginRunnable(String pLogin, String pPassword, String pBaseUrl) {
        mLogin = pLogin;
        mPassword = pPassword;
        mBaseUrl = pBaseUrl;
    }

    @Override
    public void run() {
        HttpHelper helper = new HttpHelper();
        try {
            Log.d("LoginRunnable", "run()");
            JSONObject jsonResponse = new JSONObject(helper.login(mLogin, mPassword, mBaseUrl));
            post(jsonResponse);
        }catch(ConnectException e){
            EventBus.getDefault().post(new TextEvent("Не удалось подключится к верверу" ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post(final JSONObject pJSON) throws JSONException {
        if (pJSON.getInt(Constants.ERROR_CODE) == 0) {
            EventBus.getDefault().post(new LoginResponseEvent(pJSON));
        } else {
            EventBus.getDefault().post(new TextEvent(pJSON.optString(Constants.ERROR_MESSAGE)));
        }

    }
}
