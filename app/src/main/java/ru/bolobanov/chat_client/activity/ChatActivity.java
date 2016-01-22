package ru.bolobanov.chat_client.activity;

import android.app.FragmentManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import ru.bolobanov.chat_client.R;

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
