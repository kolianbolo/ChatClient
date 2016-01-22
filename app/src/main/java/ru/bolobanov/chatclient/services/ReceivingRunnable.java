package ru.bolobanov.chatclient.services;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chatclient.Constants;
import ru.bolobanov.chatclient.HttpHelper;
import ru.bolobanov.chatclient.db.mapping.Message;
import ru.bolobanov.chatclient.events.LoginResponseEvent;
import ru.bolobanov.chatclient.events.MessagesResponseEvent;

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
                Message node = new Message();
                node.setMessage(messagesJSON.getJSONObject(i).getString("message"));
                node.setSender(messagesJSON.getJSONObject(i).getString("sender"));
                node.setReceiver(messagesJSON.getJSONObject(i).getString("receiver"));
                node.setTimestamp(messagesJSON.getJSONObject(i).getLong("timestamp"));

                messagesList.add(node);
            }
            EventBus.getDefault().post(new MessagesResponseEvent(messagesList));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
