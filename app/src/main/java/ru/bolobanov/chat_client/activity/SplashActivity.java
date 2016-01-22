package ru.bolobanov.chat_client.activity;

import android.app.Activity;
import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import ru.bolobanov.chat_client.PreferencesService_;
import ru.bolobanov.chat_client.R;
import ru.bolobanov.chat_client.services.ReceivingService_;
import ru.bolobanov.chat_client.services.UsersService_;

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
            startService(new Intent(this, UsersService_.class));
            startActivity(new Intent(this, ChatActivity_.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}
