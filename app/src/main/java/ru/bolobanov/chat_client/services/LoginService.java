package ru.bolobanov.chat_client.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import ru.bolobanov.chat_client.PreferencesService_;

/**
 * Created by Bolobanov Nikolay on 10.12.15.
 */
@EService
public class LoginService extends Service {

    private Thread mLoginThread;

    @Pref
    protected PreferencesService_ mPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLoginThread != null) {
            mLoginThread.interrupt();
        }
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        final String login = intent.getStringExtra("login");
        final String password = intent.getStringExtra("password");
        if ((login != null) && (password != null)) {
            mLoginThread = new Thread(new LoginRunnable(login, password, "http://" + address + ":" + port));
            mLoginThread.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mLoginThread.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
