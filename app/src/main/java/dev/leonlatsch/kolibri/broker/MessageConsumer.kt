package dev.leonlatsch.kolibri.broker

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.*
import dev.leonlatsch.kolibri.database.DatabaseMapper
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.interfaces.KeyPairInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.database.model.Message
import dev.leonlatsch.kolibri.database.model.MessageType
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
    private var deliverCallback: DeliverCallback? = null
    private var cancelCallback: CancelCallback? = null
    private var connection: Connection? = null
    private var userService: UserService? = null

    init {
        initialize(context)
    }

    /**
     * Initialize the consumer with services, interfaces and a connection factory from the shared preferences
     *
     * @param context
     */
    private fun initialize(context: Context) {
        userService = RestServiceFactory.getUserService()
        val preferences = Config.getSharedPreferences(context)

        // Initialize config
        connectionFactory = ConnectionFactory()
        connectionFactory!!.host = preferences.getString(Config.KEY_BACKEND_BROKER_HOST, null)
        connectionFactory!!.port = preferences.getInt(Config.KEY_BACKEND_BROKER_PORT, 0)
        connectionFactory!!.username = UserInterface.user?.uid!!
        connectionFactory!!.password = UserInterface.accessToken!!
        connectionFactory!!.isAutomaticRecoveryEnabled = true
        connectionFactory!!.run {

            // Initialize config
            connectionFactory = ConnectionFactory()
            host = preferences.getString(Config.KEY_BACKEND_BROKER_HOST, null)
            port = preferences.getInt(Config.KEY_BACKEND_BROKER_PORT, 0)
            username = UserInterface.user?.uid!!
            password = UserInterface.accessToken!!
            isAutomaticRecoveryEnabled = true
            setNetworkRecoveryInterval(1000)
            connectionTimeout = 5000
        }

        deliverCallback = DeliverCallback { _, message ->
            val messageDTO = ObjectMapper().readValue(String(message.body, StandardCharsets.UTF_8), MessageDTO::class.java)
            if (messageDTO != null) {
                when (messageDTO.type) {
                    MessageType.TEXT -> processTextMessage(DatabaseMapper.toModel(messageDTO)!!)
                    MessageType.IMAGE -> TODO("Implement Image messages")
                    MessageType.AUDIO -> TODO("Implement Audio messages")
                    MessageType.VIDEO -> TODO("Implement Video messages")
                    else -> log.warn("Cloud not process message with type: ${messageDTO.type}")
                }
            }
        }

        cancelCallback = CancelCallback {} // Do nothing on cancel
    }

    /**
     * Run a new thread and start consuming the own queue
     */
    private fun run() {
        Thread({
            try {
                connection = connectionFactory!!.newConnection()
                val channel = connection!!.createChannel()
                channel!!.basicConsume(USER_QUEUE_PREFIX + UserInterface.user?.uid!!, deliverCallback, cancelCallback)
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
        val decryptedData = CryptoManager.decryptAndDecode(message.content!!, KeyPairInterface.get(UserInterface.user?.uid!!).privateKey!!)
        val content = String(decryptedData!!, StandardCharsets.UTF_8)
        message.content = content
        if (!ChatInterface.messageExists(message)) {
            var chat = ChatInterface.getChatFromMessage(message)
            message.cid = chat?.cid

            if (chat == null) {
                try {
                    val userResponse = userService!!.get(UserInterface.accessToken!!, message.from!!).execute()
                    val publicKeyResponse = userService!!.getPublicKey(UserInterface.accessToken!!, message.from!!).execute()

                    if (userResponse.isSuccessful) {
                        ContactInterface.save(userResponse.body()?.content!!, publicKeyResponse.body()?.content!!)
                        val unreadMessages = if (ChatActivity.isActive) 0 else 1
                        chat = Chat(message.cid, message.from, unreadMessages, message.content, message.timestamp)
                        ChatInterface.saveChat(chat)
                        notifyChatListChangeListener(chat)
                    }
                } catch (e: IOException) {
                    return
                }

            } else {
                chat.lastMessage = message.content
                chat.lastTimestamp = message.timestamp
                if (ChatActivity.isActive) {
                    notifyMessageRecyclerChangeListener(message)
                } else {
                    chat.unreadMessages = chat.unreadMessages + 1
                }
                ChatInterface.updateChat(chat)
                notifyChatListChangeListener(chat)
            }
            ChatInterface.saveMessage(message)
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

        private const val USER_QUEUE_PREFIX = "queue.user."
        private const val THREAD_NAME = "BROKER-NET-THREAD"

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
