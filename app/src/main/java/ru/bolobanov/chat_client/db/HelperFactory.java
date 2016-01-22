package ru.bolobanov.chat_client.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Bolobanov Nikolay on 20.01.16.
 */
public class HelperFactory{

    private static DataBaseHelper databaseHelper;

    public static DataBaseHelper getHelper(){
        return databaseHelper;
    }
    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}