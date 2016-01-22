package ru.bolobanov.chat_client.activity;

import android.app.FragmentManager;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import ru.bolobanov.chat_client.R;

/**
 * Created by Bolobanov Nikolay on 25.12.15.
 */
@EActivity(R.layout.a_chat)
public class ChatActivity extends ParentActivity {


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        FragmentManager fragmentManager = getFragmentManager();
//        if (fragmentManager.findFragmentById(R.id.fragment_chat) != null) {
//            isTablet = true;
//        }
//        super.onCreate(savedInstanceState);
//    }

//    protected void init() {
//        FragmentManager fragmentManager = getFragmentManager();
//        if (fragmentManager.findFragmentById(R.id.fragment_chat) != null) {
//            isTablet = true;
//        }
//    }
}
