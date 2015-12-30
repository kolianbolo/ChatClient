package ru.bolobanov.chatclient.activity;

import android.app.Activity;
import android.os.Bundle;

import ru.bolobanov.chatclient.fragment.ChatPreferenceFragment_;

/**
 * Created by Bolobanov Nikolay on 10.12.15.
 */
public class PreferenceActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ChatPreferenceFragment_()).commit();
    }
}
