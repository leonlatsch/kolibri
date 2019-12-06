package dev.leonlatsch.olivia.broker;

import dev.leonlatsch.olivia.database.model.Message;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface MessageRecyclerChangeListener {

    void receive(Message message);
}
