package de.leonlatsch.olivia.broker;

import de.leonlatsch.olivia.rest.dto.MessageDTO;

public interface MessageListener {

    void receive(MessageDTO message);
}
