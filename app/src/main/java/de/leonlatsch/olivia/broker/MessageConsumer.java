package de.leonlatsch.olivia.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import de.leonlatsch.olivia.security.CryptoManager;

import static de.leonlatsch.olivia.constants.MessageType.*;

public class MessageConsumer {

    private static final String USER_PREFIX = "user.";
    private static final String USER_QUEUE_PREFIX = "queue.user.";
    private static final String THREAD_NAME = "BROKER-NET-THREAD";

    private static MessageConsumer consumer; // Singleton

    private ConnectionFactory connectionFactory;
    private DeliverCallback callback;
    private UserInterface userInterface;
    private Connection connection;

    private static List<MessageListener> listeners = new ArrayList<>();
    private static boolean isRunning = false;

    private MessageConsumer() {
        initialize();
    }

    private void initialize() {
        userInterface = UserInterface.getInstance();

        // Initialize config // TODO: use settings
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("olivia.leonlatsch.dev");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername(USER_PREFIX + userInterface.getUser().getUid());
        connectionFactory.setPassword(userInterface.getAccessToken());

        callback = ((consumerTag, message) -> {
           MessageDTO messageDTO = new ObjectMapper().readValue(new String(message.getBody(), StandardCharsets.UTF_8), MessageDTO.class);
           if (messageDTO != null) {
               switch (messageDTO.getType()) {
                   case TEXT:
                       processTextMessage(messageDTO);
                       break;
                   case IMAGE:
                       //TODO: Process Image
                       break;
                   case AUDIO:
                       //TODO: Process Audio
                       break;
                   case VIDEO:
                       //TODO Process Video
                       break;
                   default:
                       break;
               }
           }
        });
    }

    private void run() {
        new Thread(() -> {
            try {
                connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
                channel.basicConsume(USER_QUEUE_PREFIX + userInterface.getUser().getUid(), true, callback, consumerTag -> {
                });
                isRunning = true;
            } catch (IOException | TimeoutException e) {
                isRunning = false;
            }
        }, THREAD_NAME).start();
    }

    private void processTextMessage(MessageDTO message) {
        byte[] decryptedData = CryptoManager.decryptAndDecode(message.getContent(), userInterface.getUser().getPrivateKey());
        String content = new String(decryptedData, StandardCharsets.UTF_8);
        message.setContent(content);
        notifyListeners(message);
    }

    private void disconnect() {
        new Thread(() -> {
            try {
                connection.close();
            } catch (IOException e) {}
        }, THREAD_NAME).start();
    }

    public static void addMessageListener(MessageListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(MessageDTO message) {
        for (MessageListener listener : listeners) {
            listener.receive(message);
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void start() {
        if (!isRunning()) {
            if (consumer == null) {
                consumer = new MessageConsumer();
            }

            consumer.run();
        }
    }

    public static void stop() {
        if (isRunning()) {
            consumer.disconnect();
            consumer = null;
            isRunning = false;
        }
    }
}
