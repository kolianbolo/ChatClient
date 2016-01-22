package ru.bolobanov.chat_client;

/**
 * Created by Bolobanov Nikolay on 09.12.15.
 */
public final class Constants {
    public static final String HISTORY_TABLE = "history";

    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIVER = "receiver";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final int NOTIFY_ID = 42;

    public static final int GET_MESSAGES_PERIOD = 3 * 1000;
    public static final int GET_USERS_PERIOD = 30 * 1000;
}
