package ru.bolobanov.chat_client.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Debug;
import android.util.Log;
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
import ru.bolobanov.chat_client.activity.ChatActivity;
import ru.bolobanov.chat_client.activity.MobileChatActivity_;
import ru.bolobanov.chat_client.events.OpenChatEvent;

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
                openChat(receiver);
            }
        });
        String companion = getActivity().getIntent().getStringExtra(ChatFragment.COMPANION_KEY);
        if (companion != null) {
            EventBus.getDefault().removeAllStickyEvents();
            openChat(companion);
        }
    }

    private void openChat(String pCompanion) {
        EventBus.getDefault().postSticky(new OpenChatEvent(pCompanion));
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        if (getResources().getConfiguration().smallestScreenWidthDp < Constants.SMALLEST_SCREEN_WIDTH_DP) {
            startActivity(new Intent(getActivity(), MobileChatActivity_.class));
        }
    }
}
