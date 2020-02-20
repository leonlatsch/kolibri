package dev.leonlatsch.kolibri.broker.queue

import dev.leonlatsch.kolibri.broker.MessageConsumer
import dev.leonlatsch.kolibri.database.DatabaseMapper
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.rest.service.ChatService
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import java.io.IOException

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
class MessageQueue private constructor() {

    private val chatService: ChatService
    private val chatInterface: ChatInterface
    private val userInterface: UserInterface
    private val databaseMapper: DatabaseMapper

    private val thread: Thread
    private val runnable = {
        while (running) {
            try {
                Thread.sleep(2000)

                val messages = chatInterface.getAllUnsentMessages()

                for (message in messages) {
                    val response = chatService.send(userInterface.getAccessToken(), databaseMapper.toDto(message)).execute()
                    if (response.isSuccessful()) {
                        message.setSent(true)
                        chatInterface.setMessageSent(message)
                        MessageConsumer.notifyMessageRecyclerChangedFromExternal(message)
                    }
                }
            } catch (e: InterruptedException) {
            } catch (e: IOException) {
            }

        }
    }

    init {
        chatService = RestServiceFactory.getChatService()
        chatInterface = ChatInterface.getInstance()
        userInterface = UserInterface.getInstance()
        databaseMapper = DatabaseMapper.getInstance()

        thread = Thread(runnable, THREAD_NAME)
    }

    companion object {

        private val THREAD_NAME = "MESSAGE-QUEUE-THREAD"

        private var running = false
        private var messageQueue: MessageQueue? = null // Singleton

        fun stop() {
            running = false
        }

        fun start() {
            if (!running) {
                messageQueue = MessageQueue()

                running = true
                messageQueue!!.thread.start()
            }
        }
    }
}
