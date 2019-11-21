package dev.leonlatsch.olivia.broker;

import dev.leonlatsch.olivia.database.model.Chat;

public interface ChatListChangeListener {

    void addChat(Chat chat);
}
