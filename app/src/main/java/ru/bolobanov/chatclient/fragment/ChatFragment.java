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

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.HttpHelper;
import ru.bolobanov.chatclient.Message;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.db.ChatDatabaseHelper;

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
        final Intent receivedIntent = getActivity().getIntent();
        if (receivedIntent != null) {
            if (receivedIntent.hasExtra(COMPANION_KEY)) {
                String companion = receivedIntent.getStringExtra(COMPANION_KEY);
                HashMap<String, String> eventMap = new HashMap<>();
                eventMap.put(COMPANION_KEY, companion);
                getCompanion(eventMap);
            }
        }
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


    @Subscribe
    public void getMessage(ArrayList<Message> pMessages) {
        List<Message> myMessages = new ArrayList<>();
        for (Message message : pMessages)
            if (message.mSender.equals(mCompanion)) {
                myMessages.add(message);
            }
        if (myMessages.size() > 0) {
            addMessagesToChat(myMessages);
        }
    }

    @Subscribe
    public void getCompanion(HashMap<String, String> openChatEvent) {
        mCompanion = openChatEvent.get(COMPANION_KEY);
        cleanChat();
        receiverText.setText("Чат с " + mCompanion);
        ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(getActivity());
        List<Message> historyList = dbHelper.getMessages(mCompanion, mPreferences.login().get());
        addMessagesToChat(historyList);
        hideStub();
    }


    private void addMessagesToChat(List<Message> messagesList) {
        for (Message message : messagesList) {
            chatBuilder.append(simpleDF.format(message.mTimestamp)).append(message.mSender).append(": ").
                    append(message.mMessage).append("\n");

        }

        chatEdit.setText(chatBuilder.toString());
    }


    private void cleanChat() {
        chatBuilder = new StringBuilder();
        chatEdit.setText(chatBuilder.toString());
    }

    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Click(R.id.sendButton)
    public void onSend() {
        String messageStr = messageEdit.getText().toString();
        if (messageStr.isEmpty()) {
            return;
        }
        Message messageDAO = new Message(messageStr, mPreferences.login().get(), mCompanion, 0L);
        SendAsynkTask sendAsynkTask = new SendAsynkTask(messageDAO);
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
        BusProvider.getInstance().unregister(this);
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

        private Message mMessage;

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
        protected void onPostExecute(Long aLong) {
            if (aLong != 0L) {
                Message sendedMessage = new Message(mMessage.mMessage, mMessage.mSender, mMessage.mReceiver, aLong);
                ChatDatabaseHelper helper = new ChatDatabaseHelper(getActivity());
                ArrayList<Message> messagesList = new ArrayList<>();
                messagesList.add(sendedMessage);
                helper.saveMessages(messagesList);
                addMessagesToChat(messagesList);
                messageEdit.setText("");
            } else {
                Toast.makeText(getActivity(), "не удалось отправвить сообщение", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
