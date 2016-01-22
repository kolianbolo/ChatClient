package ru.bolobanov.chat_client.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.PreferencesService_;
import ru.bolobanov.chat_client.R;
import ru.bolobanov.chat_client.events.LoginResponseEvent;
import ru.bolobanov.chat_client.events.TextEvent;
import ru.bolobanov.chat_client.services.LoginService_;
import ru.bolobanov.chat_client.services.ReceivingService_;

@EActivity(R.layout.a_login)
public class LoginActivity extends Activity {

    @ViewById
    EditText loginEdit;

    @ViewById
    EditText passwordEdit;

    @Pref
    PreferencesService_ mPreferences;

    @Click(R.id.settingsButton)
    public void clickSettings() {
        startActivity(new Intent(this, PreferenceActivity.class));
    }

    @Click(R.id.loginButton)
    public void clickLogin() {
        Intent intent = new Intent(this, LoginService_.class);
        intent.putExtra("login", loginEdit.getText().toString());
        intent.putExtra("password", passwordEdit.getText().toString());
        startService(intent);
    }

    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(TextEvent event){
        Toast.makeText(this, event.mText, Toast.LENGTH_LONG).show();
    }


    public void onEventMainThread(LoginResponseEvent event){
        Log.d("getResponse()", event.mResponse.toString());
        JSONObject userObject = event.mResponse.optJSONObject("user");
        if (userObject != null) {
            String sessionString = userObject.optString("session");
            String loginString = userObject.optString("login");
            if ((sessionString == null) || (sessionString.isEmpty()) || (loginString == null || (loginString.isEmpty()))) {
                Toast.makeText(this, "Некорректный ответ сервера", Toast.LENGTH_LONG).show();
                return;
            }
            mPreferences.sessionUUID().put(sessionString);
            mPreferences.login().put(loginString);
            startService(new Intent(this, ReceivingService_.class));
            startActivity(new Intent(this, ChatActivity_.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

}
