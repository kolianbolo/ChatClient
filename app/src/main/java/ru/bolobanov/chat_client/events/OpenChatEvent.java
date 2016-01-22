package ru.bolobanov.chat_client.events;

/**
 * Created by Bolobanov Nikolay on 22.01.16.
 */
public class OpenChatEvent {
    public final String mCompanion;

    public OpenChatEvent(String pCompanion) {
        mCompanion = pCompanion;
    }
}
