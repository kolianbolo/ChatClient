package ru.bolobanov.chatclient.fragment;

import android.app.Fragment;
import android.content.Intent;
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

import de.greenrobot.event.EventBus;
import ru.bolobanov.chatclient.PreferencesService_;
import ru.bolobanov.chatclient.R;
import ru.bolobanov.chatclient.UsersAdapter;
import ru.bolobanov.chatclient.activity.ChatActivity;
import ru.bolobanov.chatclient.activity.MobileChatActivity_;
import ru.bolobanov.chatclient.events.OpenChatEvent;

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
        List<String> users = new ArrayList<>();
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
                EventBus.getDefault().postSticky(new OpenChatEvent(receiver));
                if (!((ChatActivity) getActivity()).isTablet) {
                    startActivity(new Intent(getActivity(), MobileChatActivity_.class));
                }
            }
        });
    }
}
