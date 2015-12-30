package ru.bolobanov.chatclient.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bolobanov.chatclient.BusProvider;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.UsersAdapter;
import ru.bolobanov.chatclient.activity.ChatActivity;
import ru.bolobanov.chatclient.activity.MobileChatActivity;
import ru.bolobanov.chatclient.activity.MobileChatActivity_;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
@EFragment(R.layout.f_list)
public class UsersListFragment extends Fragment {

    @ViewById
    ListView list;

    @Pref
    PreferencesService_ mPreferences;

    @AfterViews
    public void init() {
        List<String> users = new ArrayList<String>();
        try {
            JSONArray usersJSON = new JSONArray(mPreferences.users().get());
            for (int i = 0; i < usersJSON.length(); i++) {
                users.add(usersJSON.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list.setAdapter(new UsersAdapter(users, getActivity()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String receiver = (String) parent.getItemAtPosition(position);
                HashMap<String, String> openChatEvent = new HashMap<String, String>();
                openChatEvent.put(ChatFragment.COMPANION_KEY, receiver);
                BusProvider.getInstance().post(openChatEvent);
                if (!((ChatActivity) getActivity()).isTablet) {
                    Intent intent = new Intent(getActivity(), MobileChatActivity_.class);
                    intent.putExtra(ChatFragment.COMPANION_KEY, receiver);
                    startActivity(intent);
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }


    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }

}
