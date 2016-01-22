package ru.bolobanov.chat_client.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.Constants;
import ru.bolobanov.chat_client.HttpHelper;
import ru.bolobanov.chat_client.db.mapping.Message;
import ru.bolobanov.chat_client.events.MessagesResponseEvent;

/**
 * Created by Bolobanov Nikolay on 27.12.15.
 */
class ReceivingRunnable implements Runnable {

    private final String mBaseUrl;
    private final String mSession;

    public ReceivingRunnable(String pBaseUrl, String pSession) {
        mBaseUrl = pBaseUrl;
        mSession = pSession;
    }

    @Override
    public void run() {
        while (true) {
            Log.d("ReceivingRunnable", "run()");
            getMessages();
            try {
                Thread.sleep(Constants.GET_MESSAGES_PERIOD);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void getMessages() {
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
            EventBus.getDefault().postSticky(new MessagesResponseEvent(messagesList));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
