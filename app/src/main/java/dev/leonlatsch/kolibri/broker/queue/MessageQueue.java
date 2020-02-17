package dev.leonlatsch.kolibri.broker.queue;

import java.io.IOException;
import java.util.List;

import dev.leonlatsch.kolibri.broker.MessageConsumer;
import dev.leonlatsch.kolibri.database.DatabaseMapper;
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.Message;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.service.ChatService;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import retrofit2.Response;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public class MessageQueue {

    private static final String THREAD_NAME = "MESSAGE-QUEUE-THREAD";

    private static boolean running = false;
    private static MessageQueue messageQueue; // Singleton

    private ChatService chatService;
    private ChatInterface chatInterface;
    private UserInterface userInterface;
    private DatabaseMapper databaseMapper;

    private Thread thread;
    private Runnable runnable = () -> {
        while (running) {
            try {
                Thread.sleep(2000);

                List<Message> messages = chatInterface.getAllUnsentMessages();

                for (Message message : messages) {
                    Response<Container<String>> response = chatService.send(userInterface.getAccessToken(), databaseMapper.toDto(message)).execute();
                    if (response.isSuccessful()) {
                        message.setSent(true);
                        chatInterface.setMessageSent(message);
                        MessageConsumer.notifyMessageRecyclerChangedFromExternal(message);
                    }
                }
            } catch (InterruptedException | IOException e) {
            }
        }
    };

    private MessageQueue() {
        chatService = RestServiceFactory.getChatService();
        chatInterface = ChatInterface.getInstance();
        userInterface = UserInterface.getInstance();
        databaseMapper = DatabaseMapper.getInstance();

        thread = new Thread(runnable, THREAD_NAME);
    }

    public static void stop() {
        running = false;
    }

    public static void start() {
        if (!running) {
            messageQueue = new MessageQueue();

            running = true;
            messageQueue.getThread().start();
        }
    }

    private Thread getThread() {
        return thread;
    }
}
