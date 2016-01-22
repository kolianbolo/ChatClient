package ru.bolobanov.chatclient.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONArray;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.events.MessagesResponseEvent;
import ru.bolobanov.chatclient.events.UsersResponseEvent;

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
            mUsersThread.interrupt();
        }
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        mUsersThread = new Thread(new UsersRunnable("http://" + address + ":" + port, this));
        EventBus.getDefault().register(this);
        mUsersThread.start();
        return START_STICKY;
    }

    public void onEventMainThread(UsersResponseEvent event) {

        mPreferences.users().put(event.usersArray.toString());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mUsersThread.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
