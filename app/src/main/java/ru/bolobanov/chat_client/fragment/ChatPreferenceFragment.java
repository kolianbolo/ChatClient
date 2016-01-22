package ru.bolobanov.chat_client.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.bolobanov.chat_client.PreferencesService_;
import ru.bolobanov.chat_client.R;
import ru.bolobanov.chat_client.services.UsersService_;


/**
 * Created by Bolobanov Nikolay on 10.12.15.
 */

@EFragment
public class ChatPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String PORT_KEY = "serverPort";
    private final static String ADDRESS_KEY = "serverAddress";

    private static final Pattern ADDRESS_PATTERN
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    private static final Pattern PORT_PATTERN
            = Pattern.compile(
            "(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3})");

    @Pref
    PreferencesService_ mPreferences;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        EditTextPreference addressPreference = (EditTextPreference) findPreference(ADDRESS_KEY);
        EditTextPreference portPreference = (EditTextPreference) findPreference(PORT_KEY);
        addressPreference.setSummary(addressPreference.getText());
        portPreference.setSummary(portPreference.getText());
        addressPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newIp = (String) newValue;
                return ipVerify(newIp);
            }
        });

        portPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newPort = (String) newValue;
                return portVerify(newPort);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (ADDRESS_KEY.equals(key)) {
            EditTextPreference preference = (EditTextPreference) findPreference(key);
            preference.setSummary(preference.getText());
        } else if (PORT_KEY.equals(key)) {
            EditTextPreference preference = (EditTextPreference) findPreference(key);
            preference.setSummary(preference.getText());
        }
        if (getActivity() != null) {
            getActivity().startService(new Intent(getActivity(), UsersService_.class));
        }
    }

    private boolean ipVerify(String ip) {
        Matcher m = ADDRESS_PATTERN.matcher(ip);
        return m.matches();
    }

    private boolean portVerify(String portStr) {
        Matcher m = PORT_PATTERN.matcher(portStr);
        return m.matches();
    }

}
