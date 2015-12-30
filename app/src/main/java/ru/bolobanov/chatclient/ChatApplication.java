package ru.bolobanov.chatclient;

import android.app.Application;
import android.content.Intent;

import org.androidannotations.annotations.EApplication;

import ru.bolobanov.chatclient.services.UsersService_;


/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
@EApplication
public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        startService(new Intent(this, UsersService_.class));
        super.onCreate();
    }
}
