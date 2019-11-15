package de.leonlatsch.olivia.broker;

import de.leonlatsch.olivia.database.model.Chat;

public interface ChatListChangeListener {

    void addChat(Chat chat);
}
