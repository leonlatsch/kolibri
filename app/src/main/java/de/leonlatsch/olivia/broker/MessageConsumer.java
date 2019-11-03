package de.leonlatsch.olivia.broker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.rest.dto.MessageDTO;

public class MessageConsumer {

    private static final String USER_PREFIX = "user.";
    private static final String USER_QUEUE_PREFIX = "queue.user.";

    private static MessageConsumer consumer; // Singleton

    private List<MessageListener> listeners;

    private boolean isConnectionEstablished = false;

    private MessageConsumer() {
        listeners = new ArrayList<>();

        UserInterface userInterface = UserInterface.getInstance();

        // Initialize config // TODO: use settings
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("olivia.leonlatsch.dev");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername(USER_PREFIX + userInterface.getUser().getUid());
        connectionFactory.setPassword(userInterface.getAccessToken());

        DeliverCallback callback = ((consumerTag, message) -> {
            System.out.println(new String(message.getBody(), StandardCharsets.UTF_8));
        });

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicConsume(USER_QUEUE_PREFIX + userInterface.getUser().getUid(), true, callback, consumerTag -> {});
            isConnectionEstablished = true;
        } catch (IOException | TimeoutException e) {
            isConnectionEstablished = false;
        }
    }

    public void addMessageListener(MessageListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(MessageDTO message) {
        for (MessageListener listener : listeners) {
            listener.receive(message);
        }
    }

    public static MessageConsumer getInstance() {
        if (consumer == null) {
            consumer = new MessageConsumer();
        }

        return consumer;
    }

    public boolean isConnectionEstablished() {
        return isConnectionEstablished;
    }
}
