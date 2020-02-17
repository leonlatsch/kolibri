package dev.leonlatsch.olivia.broker;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import dev.leonlatsch.olivia.database.DatabaseMapper;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.interfaces.ContactInterface;
import dev.leonlatsch.olivia.database.interfaces.KeyPairInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.Chat;
import dev.leonlatsch.olivia.database.model.Message;
import dev.leonlatsch.olivia.main.chat.ChatActivity;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.MessageDTO;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import dev.leonlatsch.olivia.security.CryptoManager;
import dev.leonlatsch.olivia.settings.Config;
import retrofit2.Response;

/**
 * This class controls the receiving of messages.
 * It also controls the technical aspects of rabbtimq
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    private static final String USER_QUEUE_PREFIX = "queue.user.";
    private static final String THREAD_NAME = "BROKER-NET-THREAD";

    private static MessageConsumer consumer; // Singleton
    private static MessageRecyclerChangeListener messageRecyclerChangeListener;
    private static ChatListChangeListener chatListChangeListener;
    private static boolean isRunning = false;
    private ConnectionFactory connectionFactory;
    private DeliverCallback callback;
    private Connection connection;
    private UserInterface userInterface;
    private ContactInterface contactInterface;
    private ChatInterface chatInterface;
    private KeyPairInterface keyPairInterface;
    private UserService userService;
    private DatabaseMapper databaseMapper;

    private MessageConsumer(Context context) {
        initialize(context);
    }

    /**
     * Notify if a chat has changed from external components
     *
     * @param chat
     */
    public static void notifyChatListChangedFromExternal(Chat chat) {
        if (consumer != null) {
            consumer.notifyChatListChangeListener(chat);
        }
    }

    /**
     * Notify if a message has changed from external components
     *
     * @param message
     */
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

    public static boolean isRunning() {
        return isRunning;
    }

    public static void start(Context context) {
        if (!isRunning()) {
            if (consumer == null) {
                consumer = new MessageConsumer(context);
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

    /**
     * Initialize the consumer with services, interfaces and a connection factory from the shared preferences
     *
     * @param context
     */
    private void initialize(Context context) {
        userInterface = UserInterface.getInstance();
        contactInterface = ContactInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
        keyPairInterface = KeyPairInterface.getInstance();
        userService = RestServiceFactory.getUserService();
        databaseMapper = DatabaseMapper.getInstance();
        SharedPreferences preferences = Config.getSharedPreferences(context);

        // Initialize config
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(preferences.getString(Config.KEY_BACKEND_BROKER_HOST, null));
        connectionFactory.setPort(preferences.getInt(Config.KEY_BACKEND_BROKER_PORT, 0));
        connectionFactory.setUsername(userInterface.getUser().getUid());
        connectionFactory.setPassword(userInterface.getAccessToken());
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(1000);
        connectionFactory.setConnectionTimeout(5000);

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
                        log.warn("Received message with type: " + messageDTO.getType());
                        break;
                }
            }
        });
    }

    /**
     * Run a new thread and start consuming the own queue
     */
    private void run() {
        new Thread(() -> {
            try {
                connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
                channel.basicConsume(USER_QUEUE_PREFIX + userInterface.getUser().getUid(), true, callback, consumerTag -> {
                });
                isRunning = true;
            } catch (IOException | TimeoutException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log.warn("" + ex);
                }
                run();
            }
        }, THREAD_NAME).start();
    }

    /**
     * Proceed a text message and notify components
     *
     * @param message
     */
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
            } catch (IOException | AlreadyClosedException e) {
            }
        }, THREAD_NAME).start();
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
}
