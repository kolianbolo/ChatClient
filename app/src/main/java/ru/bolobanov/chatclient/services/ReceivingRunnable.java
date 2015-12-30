package ru.bolobanov.chatclient.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.Constants;
import ru.bolobanov.chatclient.HttpHelper;
import ru.bolobanov.chatclient.Message;

/**
 * Created by Bolobanov Nikolay on 27.12.15.
 */
class ReceivingRunnable implements Runnable {

    private final Context mContext;
    private String mBaseUrl;
    private final String mSession;

    public ReceivingRunnable(String pBaseUrl, String pSession, Context pContext) {
        mBaseUrl = pBaseUrl;
        mSession = pSession;
        mContext = pContext.getApplicationContext();
    }

    @Override
    public void run() {
        while (true) {
            Log.d("ReceivingRunnable", "run()");
            getMessages();
            try {
                Thread.sleep(Constants.GET_MESSAGES_PERIIOD);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void getMessages() {
        HttpHelper helper = new HttpHelper();
        try {
            JSONObject responseJSON = new JSONObject(helper.getMessages(mBaseUrl, mSession));
            Log.d("ReceivingRunnable", responseJSON.toString());
            final JSONArray messagesJSON = responseJSON.getJSONArray("messages");
            final ArrayList<Message> messagesList = new ArrayList<>();
            for (int i = 0; i < messagesJSON.length(); i++) {
                Message node = new Message(messagesJSON.getJSONObject(i).getString("message"),
                        messagesJSON.getJSONObject(i).getString("sender"),
                        messagesJSON.getJSONObject(i).getString("receiver"),
                        messagesJSON.getJSONObject(i).getLong("timestamp"));
                messagesList.add(node);
            }
            Handler mainHandler = new Handler(mContext.getMainLooper());
            Runnable busMessage = new Runnable() {
                @Override
                public void run() {
                    BusProvider.getInstance().post(messagesList);

                }
            };
            mainHandler.post(busMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
