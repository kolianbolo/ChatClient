package ru.bolobanov.chatclient.db.mapping;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import ru.bolobanov.chatclient.Constants;

/**
 * Created by Bolobanov Nikolay on 20.01.16.
 */
@DatabaseTable(tableName = "history")
public class Message {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = Constants.COLUMN_MESSAGE, canBeNull = false)
    private String message;

    @DatabaseField(columnName = Constants.COLUMN_SENDER, canBeNull = false)
    private String sender;

    @DatabaseField(columnName = Constants.COLUMN_RECEIVER, canBeNull = false)
    private String receiver;

    @DatabaseField(columnName = Constants.COLUMN_TIMESTAMP)
    private long timestamp;

    public Message(){
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }


}
