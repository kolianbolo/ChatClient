package ru.bolobanov.chatclient;

/**
 * Created by Bolobanov Nikolay on 26.12.15.
 */
public class Message {

    public final String mMessage;
    public final String mSender;
    public final String mReceiver;
    public final long mTimestamp;

    public Message(String pMessage, String pSender, String pReceiver, long pTimestamp) {
        mMessage = pMessage;
        mSender = pSender;
        mReceiver = pReceiver;
        mTimestamp = pTimestamp;
    }
}
