package ru.bolobanov.chat_client.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.SupportActionModeWrapper;
import android.view.Menu;
import android.view.MenuItem;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.R;
import ru.bolobanov.chat_client.events.LogoutEvent;
import ru.bolobanov.chat_client.services.LoginService_;
import ru.bolobanov.chat_client.services.ReceivingService_;

/**
 * Created by Bolobanov Nikolay on 30.12.15.
 */
public class ParentActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(PreferenceManager.getDefaultSharedPreferences(this).getString("login", ""));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        stopService(new Intent(this, LoginService_.class));
        stopService(new Intent(this, ReceivingService_.class));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sessionUUID", "");
        editor.putString("login", "");
        editor.apply();
        startActivity(new Intent(this, LoginActivity_.class).
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


    public void onEventMainThread(LogoutEvent event) {
        logout();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }
}
