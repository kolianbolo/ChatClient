package ru.bolobanov.chatclient.activity;

import android.app.Activity;
import android.app.FragmentManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import ru.bolobanov.chatclient.R;

/**
 * Created by Bolobanov Nikolay on 25.12.15.
 */
@EActivity(R.layout.a_chat)
public class ChatActivity extends ParentActivity {

    public boolean isTablet;

    @AfterViews
    protected void init() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment_chat) != null) {
            isTablet = true;
        }
    }
}
