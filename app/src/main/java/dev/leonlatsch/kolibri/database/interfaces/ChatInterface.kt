package dev.leonlatsch.kolibri.database.interfaces

import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.activeandroid.query.Update

import dev.leonlatsch.kolibri.database.model.Chat
import dev.leonlatsch.kolibri.database.model.Message

/**
 * Database interface to persist chats and messages
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object ChatInterface {

    private const val QUEUE_CID_WHERE = "cid = ?"
    private const val QUEUE_MID_WHERE = "mid = ?"
    private const val QUEUE_UID_WHERE = "uid = ?"

    val all: List<Chat> = Select().from(Chat::class.java).execute()

    val allUnsentMessages: List<Message> = Select().from(Message::class.java).where("sent = ?", false).execute()

    fun saveMessage(message: Message) {
        val savedMessage = Select().from(Message::class.java).where(QUEUE_MID_WHERE, message.mid).executeSingle<Message>()
        if (savedMessage == null) {
            message.save()
        }
    }

    fun saveChat(chat: Chat) {
        if (!chatExists(chat)) {
            chat.save()
        }
    }

    fun updateChat(chat: Chat) {
        Update(Chat::class.java).set("cid = ?, uid = ?, unread_messages = ?, last_message = ?, last_timestamp = ?", chat.cid,
                chat.uid, chat.unreadMessages, chat.lastMessage, chat.lastTimestamp)
                .where(QUEUE_CID_WHERE, chat.cid).execute()
    }

    fun setMessageSent(message: Message) {
        Update(Message::class.java).set("sent = ?", if (message.isSent) 1 else 0).where(QUEUE_MID_WHERE, message.mid).execute()
    }

    fun messageExists(message: Message): Boolean =
            Select().from(Message::class.java).where(QUEUE_MID_WHERE, message.mid).executeSingle<Message>() != null

    fun chatExists(chat: Chat): Boolean =
            Select().from(Chat::class.java).where(QUEUE_CID_WHERE, chat.cid).executeSingle<Chat>() != null

    fun deleteChat(cid: String) {
        Delete().from(Chat::class.java).where(QUEUE_CID_WHERE, cid).execute<Chat>()
        Delete().from(Message::class.java).where(QUEUE_CID_WHERE, cid).execute<Chat>()
    }

    fun deleteAll() {
        Delete().from(Chat::class.java).execute<Chat>()
        Delete().from(Message::class.java).execute<Message>()
    }

    fun getChat(cid: String): Chat = Select().from(Chat::class.java).where(QUEUE_CID_WHERE, cid).executeSingle()

    fun getChatFromMessage(message: Message): Chat {
        return Select().from(Chat::class.java).where(QUEUE_UID_WHERE, message.from).executeSingle()
    }

    fun getChatForContact(uid: String): Chat {
        return Select().from(Chat::class.java).where(QUEUE_UID_WHERE, uid).executeSingle()
    }

    fun getMessagesForChat(cid: String): List<Message> {
        return Select().from(Message::class.java).where(QUEUE_CID_WHERE, cid).execute()
    }

    fun getMessage(mid: String): Message {
        return Select().from(Message::class.java).where(QUEUE_MID_WHERE, mid).executeSingle()
    }
}
