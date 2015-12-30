package ru.bolobanov.chatclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Debug;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.services.LoginService_;
import ru.bolobanov.chatclient.services.ReceivingService_;

/**
 * Created by Bolobanov Nikolay on 30.12.15.
 */
@EActivity(R.layout.a_splash)
public class SplashActivity extends Activity {


    @Pref
    PreferencesService_ mPreferences;

    @AfterViews
    public void init() {
        if (mPreferences.sessionUUID().get().isEmpty()) {
            startActivity(new Intent(this, LoginActivity_.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            startService(new Intent(this, ReceivingService_.class));
            startActivity(new Intent(this, ChatActivity_.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}
