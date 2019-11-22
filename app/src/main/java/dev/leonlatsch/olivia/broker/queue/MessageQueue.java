package dev.leonlatsch.olivia.broker.queue;

import java.io.IOException;
import java.util.List;

import dev.leonlatsch.olivia.database.DatabaseMapper;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.Message;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.service.ChatService;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import retrofit2.Response;

public class MessageQueue {

    private static final String THREAD_NAME = "MESSAGE-QUEUE-THREAD";

    private static MessageQueue messageQueue; // Singleton

    private ChatService chatService;
    private ChatInterface chatInterface;
    private UserInterface userInterface;
    private DatabaseMapper databaseMapper;

    private Thread thread;

    private MessageQueue() {
        chatService = RestServiceFactory.getChatService();
        chatInterface = ChatInterface.getInstance();
        userInterface = UserInterface.getInstance();
        databaseMapper = DatabaseMapper.getInstance();

        thread = new Thread(runnable);
    }

    private Runnable runnable = () -> {
        while (true) {
            try {
                Thread.sleep(1000);

                List<Message> messages = chatInterface.getAllUnsentMessages();

                for (Message message : messages) {
                    Response<Container<String>> response = chatService.send(userInterface.getAccessToken(), databaseMapper.toDto(message)).execute();
                    if (response.isSuccessful()) {
                        chatInterface.setMessageSent(message);
                    }
                }
            } catch (InterruptedException | IOException e) {}
        }
    };

    private Thread getThread() {
        return thread;
    }

    public static void Start() {
        if (messageQueue == null) {
            messageQueue = new MessageQueue();
        }

        messageQueue.getThread().start();
    }
}
