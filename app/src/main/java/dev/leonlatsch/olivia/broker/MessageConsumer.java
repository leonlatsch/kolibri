package dev.leonlatsch.olivia.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.ShutdownListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import dev.leonlatsch.olivia.chat.ChatActivity;
import dev.leonlatsch.olivia.database.DatabaseMapper;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.interfaces.ContactInterface;
import dev.leonlatsch.olivia.database.interfaces.KeyPairInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.Chat;
import dev.leonlatsch.olivia.database.model.Message;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.MessageDTO;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import dev.leonlatsch.olivia.security.CryptoManager;
import retrofit2.Response;

import static dev.leonlatsch.olivia.constants.MessageType.*;

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
    private KeyPairInterface keyPairInterface;
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
        keyPairInterface = KeyPairInterface.getInstance();
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

    private ShutdownListener shutdownListener = cause -> {
        initialize();
        run();
    };

    private void run() {
        new Thread(() -> {
            try {
                connection = connectionFactory.newConnection();
                connection.addShutdownListener(shutdownListener);
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
        byte[] decryptedData = CryptoManager.decryptAndDecode(message.getContent(), keyPairInterface.get(userInterface.getUser().getUid()).getPrivateKey());
        String content = new String(decryptedData, StandardCharsets.UTF_8);
        message.setContent(content);
        if (!chatInterface.messageExists(message)) {
            Chat chat = chatInterface.getChat(message.getCid());
            if (chat == null) {
                chat = chatInterface.getChatFromMessage(message);
                if (chat != null) {
                    message.setCid(chat.getCid());
                }
            }

            if (chat == null) {
                try {
                    Response<Container<UserDTO>> userResponse = userService.get(userInterface.getAccessToken(), message.getFrom()).execute();
                    Response<Container<String>> publicKeyResponse = userService.getPublicKey(userInterface.getAccessToken(), message.getFrom()).execute();

                    if (userResponse.isSuccessful()) {
                        contactInterface.save(userResponse.body().getContent(), publicKeyResponse.body().getContent());
                        int unreadMessages = ChatActivity.isActive ? 0 : 1;
                        chat = new Chat(message.getCid(), message.getFrom(), unreadMessages, message.getContent(), message.getTimestamp());
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
                chatInterface.updateChat(chat);
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
        if (consumer != null) {
            consumer.notifyChatListChangeListener(chat);
        }
    }

    public static void notifyMessageRecyclerChangedFromExternal(Message message) {
        if (messageRecyclerChangeListener != null) {
            messageRecyclerChangeListener.receive(message);
        }
    }

    public static void setMessageRecyclerChangeListener(MessageRecyclerChangeListener listener) {
        messageRecyclerChangeListener = listener;
    }

    public static void setChatListChangeListener(ChatListChangeListener listener) {
        chatListChangeListener = listener;
    }

    private void notifyMessageRecyclerChangeListener(Message message) {
        if (messageRecyclerChangeListener != null) {
            messageRecyclerChangeListener.receive(message);
        }
    }

    private void notifyChatListChangeListener(Chat chat) {
        if (chatListChangeListener != null) {
            chatListChangeListener.chatChanged(chat);
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