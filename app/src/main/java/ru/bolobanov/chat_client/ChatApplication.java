package ru.bolobanov.chat_client;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

import ru.bolobanov.chat_client.db.HelperFactory;


/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
@EApplication
public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(this.getApplicationContext());
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}
