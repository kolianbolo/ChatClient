package ru.bolobanov.chat_client.fragment;

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
import java.util.List;

import de.greenrobot.event.EventBus;
import ru.bolobanov.chat_client.Constants;
import ru.bolobanov.chat_client.PreferencesService_;
import ru.bolobanov.chat_client.R;
import ru.bolobanov.chat_client.UsersAdapter;
import ru.bolobanov.chat_client.activity.LoginActivity_;
import ru.bolobanov.chat_client.activity.MobileChatActivity_;
import ru.bolobanov.chat_client.events.OpenChatEvent;
import ru.bolobanov.chat_client.events.UsersResponseEvent;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
@EFragment(R.layout.f_list)
public class UsersListFragment extends Fragment {

    @ViewById
    protected ListView list;

    @Pref
    protected PreferencesService_ mPreferences;

    @AfterViews
    public void init() {
        initUsersList(convertUsers(loadUsers()));
        String companion = getActivity().getIntent().getStringExtra(ChatFragment.COMPANION_KEY);
        if (companion != null) {
            if (mPreferences.sessionUUID().get().isEmpty()) {
                startActivity(new Intent(getActivity(), LoginActivity_.class).
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                EventBus.getDefault().removeAllStickyEvents();
                openChat(companion);
            }
        }
    }

    private JSONArray loadUsers() {
        JSONArray returned = null;
        try {
            if (!mPreferences.users().get().isEmpty()) {
                returned = new JSONArray(mPreferences.users().get());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returned;
    }

    private void openChat(String pCompanion) {
        EventBus.getDefault().postSticky(new OpenChatEvent(pCompanion));
        if (getResources().getConfiguration().smallestScreenWidthDp < Constants.SMALLEST_SCREEN_WIDTH_DP) {
            startActivity(new Intent(getActivity(), MobileChatActivity_.class));
        }
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

    private void initUsersList(List<String> pUsers) {
        list.setAdapter(new UsersAdapter(pUsers, getActivity()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String receiver = (String) parent.getItemAtPosition(position);
                openChat(receiver);
            }
        });
    }

    private List<String> convertUsers(JSONArray usersJSON) {
        List<String> users = new ArrayList<>();
        if (usersJSON == null) {
            return users;
        }
        try {
            for (int i = 0; i < usersJSON.length(); i++) {
                users.add(usersJSON.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void onEventMainThread(UsersResponseEvent event) {
        initUsersList(convertUsers(event.usersArray));
    }
}
