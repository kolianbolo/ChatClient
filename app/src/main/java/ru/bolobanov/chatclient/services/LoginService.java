package ru.bolobanov.chatclient.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.PreferencesService_;

/**
 * Created by Bolobanov Nikolay on 10.12.15.
 */
@EService
public class LoginService extends Service {

    private Thread mLoginThread;

    @Pref
    PreferencesService_ mPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLoginThread != null) {
            BusProvider.getInstance().unregister(mLoginThread);
            mLoginThread.interrupt();
        }
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        mLoginThread = new Thread(new LoginRunnable(this.getApplicationContext(), intent.getStringExtra("login"), intent.getStringExtra("password"),
                new StringBuilder("http://").append(address).append(":").append(port).toString()));
        BusProvider.getInstance().register(mLoginThread);
        mLoginThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(mLoginThread);
        mLoginThread.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
