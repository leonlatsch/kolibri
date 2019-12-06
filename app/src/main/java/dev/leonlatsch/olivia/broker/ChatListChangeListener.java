package dev.leonlatsch.olivia.broker;

import dev.leonlatsch.olivia.database.model.Chat;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface ChatListChangeListener {

    void chatChanged(Chat chat);
}
