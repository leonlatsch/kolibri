package dev.leonlatsch.olivia.broker;

import dev.leonlatsch.olivia.database.model.Chat;

/**
 * Listener for the Chat list
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface ChatListChangeListener {

    /**
     * Called when a Chat has changed or a new one is created
     *
     * @param chat
     */
    void chatChanged(Chat chat);
}
