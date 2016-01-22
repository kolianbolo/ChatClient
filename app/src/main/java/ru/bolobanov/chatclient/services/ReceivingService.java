package ru.bolobanov.chatclient.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;


import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.SQLException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chatclient.Constants;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.activity.ChatActivity_;
import ru.bolobanov.chatclient.db.DataBaseHelper;
import ru.bolobanov.chatclient.db.HelperFactory;
import ru.bolobanov.chatclient.db.mapping.Message;
import ru.bolobanov.chatclient.events.MessagesResponseEvent;

/**
 * Created by Bolobanov Nikolay on 27.12.15.
 */
@EService
public class ReceivingService extends Service {
    private Thread mReceivingThread;

    @Pref
    PreferencesService_ mPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mReceivingThread != null) {
            mReceivingThread.interrupt();
        }
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        mReceivingThread = new Thread(new ReceivingRunnable("http://" + address + ":" + port,
                mPreferences.sessionUUID().get(), this));
        EventBus.getDefault().register(this);
        mReceivingThread.start();
        return START_STICKY;
    }

    public void onEventMainThread(MessagesResponseEvent event){
        DataBaseHelper databaseHelper = HelperFactory.getHelper();
        if (event.mMessages.size() > 0) {
            try {
                for(Message message : event.mMessages) {
                    databaseHelper.getMessageDAO().create(message);
                }
                long deadLine = event.mMessages.get(0).getTimestamp() -
                        Long.parseLong(mPreferences.lengthHistory().get()) * 24 * 60 * 60 * 1000;
                databaseHelper.getMessageDAO().deleteOldMessages(deadLine);
                notification(event.mMessages.get(0));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void notification(Message pMessage) {
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, ChatActivity_.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.settings)
                .setAutoCancel(true)
                .setContentTitle(pMessage.getSender())
                .setContentText(pMessage.getMessage());
        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Constants.NOTIFY_ID, notification);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mReceivingThread.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
