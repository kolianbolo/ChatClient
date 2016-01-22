package ru.bolobanov.chatclient.events;

import java.util.List;

import ru.bolobanov.chatclient.db.mapping.Message;

/**
 * Created by Bolobanov Nikolay on 22.01.16.
 */
public class MessagesResponseEvent {
    public final List<Message> mMessages;

    public MessagesResponseEvent(List<Message> pMessages) {
        mMessages = pMessages;

    }
}
