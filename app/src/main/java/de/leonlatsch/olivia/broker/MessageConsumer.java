package de.leonlatsch.olivia.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import de.leonlatsch.olivia.chat.ChatActivity;
import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.database.interfaces.ChatInterface;
import de.leonlatsch.olivia.database.interfaces.ContactInterface;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Message;
import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.security.CryptoManager;
import retrofit2.Response;

import static de.leonlatsch.olivia.constants.MessageType.*;

public class MessageConsumer {

    private static final String USER_PREFIX = "user.";
    private static final String USER_QUEUE_PREFIX = "queue.user.";
    private static final String THREAD_NAME = "BROKER-NET-THREAD";

    private static MessageConsumer consumer; // Singleton

    private ConnectionFactory connectionFactory;
    private DeliverCallback callback;
    private Connection connection;

    private UserInterface userInterface;
    private ContactInterface contactInterface;
    private ChatInterface chatInterface;
    private UserService userService;
    private DatabaseMapper databaseMapper;

    private static MessageRecyclerChangeListener messageRecyclerChangeListener;
    private static ChatListChangeListener chatListChangeListener;
    private static boolean isRunning = false;

    private MessageConsumer() {
        initialize();
    }

    private void initialize() {
        userInterface = UserInterface.getInstance();
        contactInterface = ContactInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
        userService = RestServiceFactory.getUserService();
        databaseMapper = DatabaseMapper.getInstance();

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
                       processTextMessage(databaseMapper.toModel(messageDTO));
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

    private void processTextMessage(Message message) {
        byte[] decryptedData = CryptoManager.decryptAndDecode(message.getContent(), userInterface.getUser().getPrivateKey());
        String content = new String(decryptedData, StandardCharsets.UTF_8);
        message.setContent(content);
        if (!chatInterface.messageExists(message)) {
            Chat chat = chatInterface.getChat(message.getCid());

            if (chat == null) {
                try {
                    Response<Container<UserDTO>> userResponse = userService.get(userInterface.getAccessToken(), message.getFrom()).execute();
                    Response<Container<String>> publicKeyResponse = userService.getPublicKey(userInterface.getAccessToken(), message.getFrom()).execute();

                    if (userResponse.isSuccessful()) {
                        contactInterface.save(userResponse.body().getContent(), publicKeyResponse.body().getContent());
                        chat = new Chat(message.getCid(), message.getFrom(), 0, message.getContent(), message.getTimestamp());
                        chatInterface.saveChat(chat);
                        notifyChatListChangeListener(chat);
                    }
                } catch (IOException e) {
                    return;
                }
            } else {
                chat.setLastMessage(message.getContent());
                chat.setLastTimestamp(message.getTimestamp());
                if (ChatActivity.isActive) {
                    notifyMessageRecyclerChangeListener(message);
                } else {
                    chat.setUnreadMessages(chat.getUnreadMessages() + 1);
                }
                notifyChatListChangeListener(chat);
            }
            chatInterface.saveMessage(message);
        }
    }

    private void disconnect() {
        new Thread(() -> {
            try {
                connection.close();
            } catch (IOException | AlreadyClosedException e) {}
        }, THREAD_NAME).start();
    }

    public static void notifyChatListChangedFromExternal(Chat chat) {
        consumer.notifyChatListChangeListener(chat);
    }

    public static void setMessageRecyclerChangeListener(MessageRecyclerChangeListener listener) {
        messageRecyclerChangeListener = listener;
    }

    public static void setChatListChangeListener(ChatListChangeListener listener) {
        chatListChangeListener = listener;
    }

    private void notifyMessageRecyclerChangeListener(Message message) {
        messageRecyclerChangeListener.receive(message);
    }

    private void notifyChatListChangeListener(Chat chat) {
        chatListChangeListener.addChat(chat);
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
