package de.leonlatsch.olivia.broker;

import de.leonlatsch.olivia.database.model.Message;

public interface MessageRecyclerChangeListener {

    void receive(Message message);
}
