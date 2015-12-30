package ru.bolobanov.chatclient.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONArray;
import org.json.JSONObject;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.PreferencesService_;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
@EService
public class UsersService extends Service {

    private Thread mUsersThread;

    @Pref
    PreferencesService_ mPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUsersThread != null) {
            BusProvider.getInstance().unregister(mUsersThread);
            mUsersThread.interrupt();
        }
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        mUsersThread = new Thread(new UsersRunnable(new StringBuilder("http://").append(address).
                append(":").append(port).toString(), this));
        BusProvider.getInstance().register(this);
        BusProvider.getInstance().register(mUsersThread);
        mUsersThread.start();
        return START_STICKY;
    }

    @Subscribe
    public void getMessage(JSONArray usersJSON) {
        mPreferences.users().put(usersJSON.toString());
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(mUsersThread);
        BusProvider.getInstance().unregister(this);
        mUsersThread.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
