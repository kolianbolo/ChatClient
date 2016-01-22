package ru.bolobanov.chat_client.services;

import android.util.Log;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;
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
            postJSON(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            postErrorMessage();
        }
    }

    private void postJSON(final JSONObject pJSON) {
        EventBus.getDefault().post(new LoginResponseEvent(pJSON));

    }

    private void postErrorMessage() {
        EventBus.getDefault().post(new TextEvent("некорректный ответ сервера"));
    }

}
