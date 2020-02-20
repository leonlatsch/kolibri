package dev.leonlatsch.kolibri.broker

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AlreadyClosedException
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dev.leonlatsch.kolibri.database.DatabaseMapper
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.interfaces.KeyPairInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.database.model.Message
import dev.leonlatsch.kolibri.main.chat.ChatActivity
import dev.leonlatsch.kolibri.rest.dto.MessageDTO
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import dev.leonlatsch.kolibri.security.CryptoManager
import dev.leonlatsch.kolibri.settings.Config
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeoutException

/**
 * This class controls the receiving of messages.
 * It also controls the technical aspects of rabbtimq
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class MessageConsumer private constructor(context: Context) {
    private var connectionFactory: ConnectionFactory? = null
    private var callback: DeliverCallback? = null
    private var connection: Connection? = null
    private var userInterface: UserInterface? = null
    private var contactInterface: ContactInterface? = null
    private var chatInterface: ChatInterface? = null
    private var keyPairInterface: KeyPairInterface? = null
    private var userService: UserService? = null
    private var databaseMapper: DatabaseMapper? = null

    init {
        initialize(context)
    }

    /**
     * Initialize the consumer with services, interfaces and a connection factory from the shared preferences
     *
     * @param context
     */
    private fun initialize(context: Context) {
        userInterface = UserInterface.getInstance()
        contactInterface = ContactInterface.getInstance()
        chatInterface = ChatInterface.getInstance()
        keyPairInterface = KeyPairInterface.getInstance()
        userService = RestServiceFactory.getUserService()
        databaseMapper = DatabaseMapper.getInstance()
        val preferences = Config.getSharedPreferences(context)

        // Initialize config
        connectionFactory = ConnectionFactory()
        connectionFactory!!.setHost(preferences.getString(Config.KEY_BACKEND_BROKER_HOST, null))
        connectionFactory!!.setPort(preferences.getInt(Config.KEY_BACKEND_BROKER_PORT, 0))
        connectionFactory!!.setUsername(userInterface!!.getUser().getUid())
        connectionFactory!!.setPassword(userInterface!!.getAccessToken())
        connectionFactory!!.setAutomaticRecoveryEnabled(true)
        connectionFactory!!.setNetworkRecoveryInterval(1000)
        connectionFactory!!.setConnectionTimeout(5000)

        callback = { consumerTag, message ->
            val messageDTO = ObjectMapper().readValue(String(message.getBody(), StandardCharsets.UTF_8), MessageDTO::class.java)
            if (messageDTO != null) {
                when (messageDTO!!.getType()) {
                    TEXT -> processTextMessage(databaseMapper!!.toModel(messageDTO))
                    IMAGE -> {
                    }
                    AUDIO -> {
                    }
                    VIDEO -> {
                    }
                    else -> log.warn("Received message with type: " + messageDTO!!.getType())
                }//TODO: Process Image
                //TODO: Process Audio
                //TODO Process Video
            }
        }
    }

    /**
     * Run a new thread and start consuming the own queue
     */
    private fun run() {
        Thread({
            try {
                connection = connectionFactory!!.newConnection()
                val channel = connection!!.createChannel()
                channel.basicConsume(USER_QUEUE_PREFIX + userInterface!!.getUser().getUid(), true, callback, { consumerTag -> })
                isRunning = true
            } catch (e: IOException) {
                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    log.warn("" + ex)
                }

                run()
            } catch (e: TimeoutException) {
                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    log.warn("" + ex)
                }

                run()
            }
        }, THREAD_NAME).start()
    }

    /**
     * Proceed a text message and notify components
     *
     * @param message
     */
    private fun processTextMessage(message: Message) {
        val decryptedData = CryptoManager.decryptAndDecode(message.getContent(), keyPairInterface!!.get(userInterface!!.getUser().getUid()).getPrivateKey())
        val content = String(decryptedData, StandardCharsets.UTF_8)
        message.setContent(content)
        if (!chatInterface!!.messageExists(message)) {
            var chat = chatInterface!!.getChatFromMessage(message)
            if (chat != null) {
                message.setCid(chat!!.getCid())
            }

            if (chat == null) {
                try {
                    val userResponse = userService!!.get(userInterface!!.getAccessToken(), message.getFrom()).execute()
                    val publicKeyResponse = userService!!.getPublicKey(userInterface!!.getAccessToken(), message.getFrom()).execute()

                    if (userResponse.isSuccessful()) {
                        contactInterface!!.save(userResponse.body().getContent(), publicKeyResponse.body().getContent())
                        val unreadMessages = if (ChatActivity.isActive) 0 else 1
                        chat = Chat(message.getCid(), message.getFrom(), unreadMessages, message.getContent(), message.getTimestamp())
                        chatInterface!!.saveChat(chat)
                        notifyChatListChangeListener(chat)
                    }
                } catch (e: IOException) {
                    return
                }

            } else {
                chat!!.setLastMessage(message.getContent())
                chat!!.setLastTimestamp(message.getTimestamp())
                if (ChatActivity.isActive) {
                    notifyMessageRecyclerChangeListener(message)
                } else {
                    chat!!.setUnreadMessages(chat!!.getUnreadMessages() + 1)
                }
                chatInterface!!.updateChat(chat)
                notifyChatListChangeListener(chat)
            }
            chatInterface!!.saveMessage(message)
        }
    }

    private fun disconnect() {
        Thread({
            try {
                connection!!.close()
            } catch (e: IOException) {
            } catch (e: AlreadyClosedException) {
            }
        }, THREAD_NAME).start()
    }

    private fun notifyMessageRecyclerChangeListener(message: Message) {
        if (messageRecyclerChangeListener != null) {
            messageRecyclerChangeListener!!.receive(message)
        }
    }

    private fun notifyChatListChangeListener(chat: Chat) {
        if (chatListChangeListener != null) {
            chatListChangeListener!!.chatChanged(chat)
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(MessageConsumer::class.java)

        private val USER_QUEUE_PREFIX = "queue.user."
        private val THREAD_NAME = "BROKER-NET-THREAD"

        private var consumer: MessageConsumer? = null // Singleton
        private var messageRecyclerChangeListener: MessageRecyclerChangeListener? = null
        private var chatListChangeListener: ChatListChangeListener? = null
        var isRunning = false
            private set

        /**
         * Notify if a chat has changed from external components
         *
         * @param chat
         */
        fun notifyChatListChangedFromExternal(chat: Chat) {
            if (consumer != null) {
                consumer!!.notifyChatListChangeListener(chat)
            }
        }

        /**
         * Notify if a message has changed from external components
         *
         * @param message
         */
        fun notifyMessageRecyclerChangedFromExternal(message: Message) {
            if (messageRecyclerChangeListener != null) {
                messageRecyclerChangeListener!!.receive(message)
            }
        }

        fun setMessageRecyclerChangeListener(listener: MessageRecyclerChangeListener) {
            messageRecyclerChangeListener = listener
        }

        fun setChatListChangeListener(listener: ChatListChangeListener) {
            chatListChangeListener = listener
        }

        fun start(context: Context) {
            if (!isRunning) {
                if (consumer == null) {
                    consumer = MessageConsumer(context)
                }

                consumer!!.run()
            }
        }

        fun stop() {
            if (isRunning) {
                consumer!!.disconnect()
                consumer = null
                isRunning = false
            }
        }
    }
}
