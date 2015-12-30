package ru.bolobanov.chatclient;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Bolobanov Nikolay on 11.12.15.
 */

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface PreferencesService {

    @DefaultString("192.168.1.3")
    String serverAddress();

    @DefaultString("8080")
    String serverPort();

    @DefaultString("31")
    String lengthHistory();

    @DefaultString("")
    String sessionUUID();

    @DefaultString("")
    String login();

    @DefaultString("")
    String users();



}
