package dev.leonlatsch.olivia.broker;

import dev.leonlatsch.olivia.database.model.Message;

public interface MessageRecyclerChangeListener {

    void receive(Message message);
}
