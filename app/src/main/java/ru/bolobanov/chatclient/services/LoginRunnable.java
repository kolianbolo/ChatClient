package ru.bolobanov.chatclient.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.HttpHelper;

/**
 * Created by Bolobanov Nikolay on 25.12.15.
 */
public class LoginRunnable implements Runnable {

    private String mLogin;
    private String mPassword;
    private String mBaseUrl;
    private Context mContext;


    public LoginRunnable(Context pContext, String pLogin, String pPassword, String pBaseUrl) {
        mContext = pContext;
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
        Handler mainHandler = new Handler(mContext.getMainLooper());
        Runnable busMessage = new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post(pJSON);
            }
        };
        mainHandler.post(busMessage);
    }

    private void postErrorMessage() {
        Handler mainHandler = new Handler(mContext.getMainLooper());
        Runnable busMessage = new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post("некорректный ответ от сервера");
            }
        };
        mainHandler.post(busMessage);
    }

}
