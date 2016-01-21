package ru.bolobanov.chatclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import ru.bolobanov.chatclient.db.mapping.Message;

/**
 * Created by Bolobanov Nikolay on 20.01.16.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DataBaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "history.db";

    private static final int DATABASE_VERSION = 1;

    private MessageDAO messageDao = null;


    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, Message.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer){
    }

    public MessageDAO getMessageDAO() throws SQLException {
        if(messageDao == null){
            messageDao = new MessageDAO(getConnectionSource(), Message.class);
        }
        return  messageDao;
    }

    @Override
    public void close(){
        super.close();
        messageDao = null;

    }
}