package ru.bolobanov.chat_client.activity;

import android.app.Activity;
import android.os.Bundle;

import ru.bolobanov.chat_client.fragment.ChatPreferenceFragment_;

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
