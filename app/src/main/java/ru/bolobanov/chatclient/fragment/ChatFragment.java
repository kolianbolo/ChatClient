package ru.bolobanov.chatclient.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chatclient.HttpHelper;
import ru.bolobanov.chatclient.db.HelperFactory;
import ru.bolobanov.chatclient.db.mapping.Message;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.db.DataBaseHelper;
import ru.bolobanov.chatclient.events.MessagesResponseEvent;
import ru.bolobanov.chatclient.events.OpenChatEvent;
import ru.bolobanov.chatclient.events.TextEvent;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
@EFragment(R.layout.f_chat)
public class ChatFragment extends Fragment implements TextView.OnEditorActionListener {

    public static final String COMPANION_KEY = "companion";

    @ViewById
    LinearLayout stubLinear;

    @ViewById
    LinearLayout contentLinear;

    @ViewById
    EditText chatEdit;

    @ViewById
    EditText messageEdit;

    @ViewById
    TextView receiverText;

    @Pref
    PreferencesService_ mPreferences;

    private ProgressDialog progressDialog;

    private String mCompanion;

    private StringBuilder chatBuilder = new StringBuilder();

    private final SimpleDateFormat simpleDF = new SimpleDateFormat("[yyyy-MM-dd HH:mm]");

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCompanion = savedInstanceState.getString(COMPANION_KEY);
        } else {
            showStub();
        }
//        final Intent receivedIntent = getActivity().getIntent();
//        if (receivedIntent != null) {
//            if (receivedIntent.hasExtra(COMPANION_KEY)) {
//                String companion = receivedIntent.getStringExtra(COMPANION_KEY);
//                //TODO: отобразить имя компаньёна на экране и в поле активити
////                HashMap<String, String> eventMap = new HashMap<>();
////                eventMap.put(COMPANION_KEY, companion);
////                getCompanion(eventMap);
//            }
//        }
        messageEdit.setOnEditorActionListener(this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putCharSequence(COMPANION_KEY, mCompanion);
        super.onSaveInstanceState(outState);
    }

    public void onEventMainThread(MessagesResponseEvent event) {
        List<Message> myMessages = new ArrayList<>();
        for (Message message : event.mMessages)
            if (message.getSender().equals(mCompanion)) {
                myMessages.add(message);
            }
        if (myMessages.size() > 0) {
            addMessagesToChat(myMessages);
        }
    }

    public void onEvent(OpenChatEvent event) {
        mCompanion = event.mCompanion;
        cleanChat();
        receiverText.setText("Чат с " + mCompanion);
        DataBaseHelper dbHelper = HelperFactory.getHelper();
        try {
            List<Message> historyList = dbHelper.getMessageDAO().getMessages(mCompanion, mPreferences.login().get());
            addMessagesToChat(historyList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().removeStickyEvent(event);
        hideStub();
    }

    private void addMessagesToChat(List<Message> messagesList) {
        for (Message message : messagesList) {
            chatBuilder.append(simpleDF.format(message.getTimestamp())).append(message.getSender()).append(": ").
                    append(message.getMessage()).append("\n");

        }

        chatEdit.setText(chatBuilder.toString());
    }


    private void cleanChat() {
        chatBuilder = new StringBuilder();
        chatEdit.setText(chatBuilder.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Click(R.id.sendButton)
    public void onSend() {
        String messageStr = messageEdit.getText().toString();
        if (messageStr.isEmpty()) {
            return;
        }
        Message message = new Message();
        message.setMessage(messageStr);
        message.setReceiver(mCompanion);
        message.setSender(mPreferences.login().get());
        SendAsynkTask sendAsynkTask = new SendAsynkTask(message);
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.show();
        sendAsynkTask.execute("http://" + address + ":" + port, mPreferences.sessionUUID().get());
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void showStub() {
        stubLinear.setVisibility(View.VISIBLE);
        contentLinear.setVisibility(View.GONE);
    }

    private void hideStub() {
        stubLinear.setVisibility(View.GONE);
        contentLinear.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onSend();
        return true;
    }

    class SendAsynkTask extends AsyncTask<String, Void, Long> {

        private final Message mMessage;

        public SendAsynkTask(Message sendingMessage) {
            mMessage = sendingMessage;
        }

        @Override
        protected Long doInBackground(String... params) {
            HttpHelper httpHelper = new HttpHelper();
            try {
                String response = httpHelper.putMessages(params[0], mMessage, params[1]);
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.getInt("error_code") == 0) {
                    return responseJSON.getLong("timestamp");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aTimestamp) {
            if (aTimestamp != 0L) {
                mMessage.setTimestamp(aTimestamp);
                DataBaseHelper helper = HelperFactory.getHelper();
                ArrayList<Message> messagesList = new ArrayList<>();
                messagesList.add(mMessage);
                try {
                    helper.getMessageDAO().create(mMessage);
                    addMessagesToChat(messagesList);
                    messageEdit.setText("");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "не удалось отправить сообщение", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
