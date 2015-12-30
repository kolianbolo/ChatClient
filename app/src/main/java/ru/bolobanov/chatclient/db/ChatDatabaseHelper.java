package ru.bolobanov.chatclient.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import ru.bolobanov.chatclient.Constants;
import ru.bolobanov.chatclient.Message;

public class ChatDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    private static final String HISTORY_CREATE = "CREATE TABLE " + Constants.HISTORY_TABLE +
            " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Constants.COLUMN_MESSAGE +
            " TEXT NOT NULL, " + Constants.COLUMN_SENDER + " TEXT NOT NULL, " + Constants.COLUMN_RECEIVER +
            " TEXT NOT NULL, " + Constants.COLUMN_TIMESTAMP + "  UNSIGNED BIG INT);";


    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HISTORY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveMessages(List<Message> pMessages) {
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        for (Message message : pMessages) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.COLUMN_MESSAGE, message.mMessage);
            contentValues.put(Constants.COLUMN_SENDER, message.mSender);
            contentValues.put(Constants.COLUMN_RECEIVER, message.mReceiver);
            contentValues.put(Constants.COLUMN_TIMESTAMP, message.mTimestamp);
            database.insert(Constants.HISTORY_TABLE, null, contentValues);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    public List<Message> getMessages(String first, String second) {
        List<Message> returned = new ArrayList<>();
        final SQLiteDatabase database = getReadableDatabase();
        String query = new StringBuilder("( ").append(Constants.COLUMN_RECEIVER).append(" like ? AND ").
                append(Constants.COLUMN_SENDER).append(" like ? ) OR ( ").
                append(Constants.COLUMN_RECEIVER).append(" like ? AND ").
                append(Constants.COLUMN_SENDER).append(" like ? )").toString();
        final Cursor cursor = database.query(Constants.HISTORY_TABLE,
                new String[]{Constants.COLUMN_MESSAGE, Constants.COLUMN_SENDER, Constants.COLUMN_RECEIVER, Constants.COLUMN_TIMESTAMP},
                query, new String[]{first, second, second, first}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Message article = new Message(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(Constants.COLUMN_SENDER)),
                        cursor.getString(cursor.getColumnIndex(Constants.COLUMN_RECEIVER)),
                        cursor.getLong(cursor.getColumnIndex(Constants.COLUMN_TIMESTAMP)));
                returned.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return returned;
    }

    public void deleteOldMessage(long deadLine) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(Constants.HISTORY_TABLE, Constants.COLUMN_TIMESTAMP + " < ?", new String[]{Long.toString(deadLine)});
        database.close();
    }


}
