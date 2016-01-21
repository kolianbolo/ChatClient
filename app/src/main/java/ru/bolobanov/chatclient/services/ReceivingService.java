package ru.bolobanov.chatclient.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.SQLException;
import java.util.ArrayList;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.Constants;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.activity.ChatActivity_;
import ru.bolobanov.chatclient.db.DataBaseHelper;
import ru.bolobanov.chatclient.db.HelperFactory;
import ru.bolobanov.chatclient.db.mapping.Message;

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
            BusProvider.getInstance().unregister(mReceivingThread);
            mReceivingThread.interrupt();
        }
        final String address = mPreferences.serverAddress().get();
        final String port = mPreferences.serverPort().get();
        mReceivingThread = new Thread(new ReceivingRunnable("http://" + address + ":" + port,
                mPreferences.sessionUUID().get(), this));
        BusProvider.getInstance().register(this);
        BusProvider.getInstance().register(mReceivingThread);
        mReceivingThread.start();
        return START_STICKY;
    }

    @Subscribe
    public void getMessage(ArrayList<Message> pMessages) {
        DataBaseHelper databaseHelper = HelperFactory.getHelper();
        if (pMessages.size() > 0) {
            try {
                for(Message message : pMessages) {
                    databaseHelper.getMessageDAO().create(message);
                }
              long deadLine = pMessages.get(0).getTimestamp() -
              Long.parseLong(mPreferences.lengthHistory().get()) * 24 * 60 * 60 * 1000;
              databaseHelper.getMessageDAO().deleteOldMessages(deadLine);
                notification(pMessages.get(0));
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
        BusProvider.getInstance().unregister(mReceivingThread);
        BusProvider.getInstance().unregister(this);
        mReceivingThread.interrupt();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
