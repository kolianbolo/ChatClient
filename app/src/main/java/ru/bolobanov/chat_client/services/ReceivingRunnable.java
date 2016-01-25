package ru.bolobanov.chat_client.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.Constants;
import ru.bolobanov.chat_client.HttpHelper;
import ru.bolobanov.chat_client.db.mapping.Message;
import ru.bolobanov.chat_client.events.LogoutEvent;
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
            try {
                getMessages();
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
            int error_code = responseJSON.getInt(Constants.ERROR_CODE);
            switch (error_code) {
                case Constants.OK:
                    final JSONArray messagesJSON = responseJSON.getJSONArray("messages");
                    processingMessages(messagesJSON);
                    break;
                case Constants.BAD_SESSION:
                    EventBus.getDefault().post(new LogoutEvent());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
         private void processingMessages(JSONArray pMessagesJSON ) throws JSONException {
             final ArrayList<Message> messagesList = new ArrayList<>();
             for (int i = 0; i < pMessagesJSON.length(); i++) {
                 Message node = new Message();
                 node.setMessage(pMessagesJSON.getJSONObject(i).getString("message"));
                 node.setSender(pMessagesJSON.getJSONObject(i).getString("sender"));
                 node.setReceiver(pMessagesJSON.getJSONObject(i).getString("receiver"));
                 node.setTimestamp(pMessagesJSON.getJSONObject(i).getLong("timestamp"));
                 messagesList.add(node);
             }
             EventBus.getDefault().postSticky(new MessagesResponseEvent(messagesList));
         }
}
