package dev.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Set;
import com.activeandroid.query.Update;

import java.util.List;

import dev.leonlatsch.olivia.database.model.Chat;
import dev.leonlatsch.olivia.database.model.Message;

public class ChatInterface extends BaseInterface {

    private static final String QUEUE_CID_WHERE = "cid = ?";
    private static final String QUEUE_MID_WHERE = "mid = ?";
    private static final String QUEUE_UID_WHERE = "uid = ?";

    private static ChatInterface chatInterface; // Singleton

    private ChatInterface() {
    }

    public void saveMessage(Message message) {
        Message savedMessage = new Select().from(Message.class).where(QUEUE_MID_WHERE, message.getMid()).executeSingle();
        if (savedMessage == null) {
            message.save();
        }
    }

    public void saveChat(Chat chat) {
        if (!chatExists(chat)) {
            chat.save();
        }
    }

    public void updateChat(Chat chat) {
        new Update(Chat.class).set("cid = ?, uid = ?, unread_messages = ?, last_message = ?, last_timestamp = ?", chat.getCid(),
                chat.getUid(), chat.getUnreadMessages(), chat.getLastMessage(), chat.getLastTimestamp())
                .where(QUEUE_CID_WHERE, chat.getCid()).execute();
    }

    public void setMessageSent(Message message) {
        new Update(Message.class).set("sent = ?", message.isSent() ? 1 : 0).where(QUEUE_MID_WHERE, message.getMid()).execute();
    }

    public boolean messageExists(Message message) {
        return new Select().from(Message.class).where(QUEUE_MID_WHERE, message.getMid()).executeSingle() != null;
    }

    public boolean chatExists(Chat chat) {
        return new Select().from(Chat.class).where(QUEUE_CID_WHERE, chat.getCid()).executeSingle() != null;
    }

    public void deleteChat(String cid) {
        new Delete().from(Chat.class).where(QUEUE_CID_WHERE, cid).execute();
        new Delete().from(Message.class).where(QUEUE_CID_WHERE, cid).execute();
    }

    public void deleteAll() {
        new Delete().from(Chat.class).execute();
        new Delete().from(Message.class).execute();
    }

    public List<Chat> getALl() {
        return new Select().from(Chat.class).execute();
    }

    public List<Message> getAllUnsentMessages() {
        return new Select().from(Message.class).where("sent = ?", false).execute();
    }

    public Chat getChat(String cid) {
        return new Select().from(Chat.class).where(QUEUE_CID_WHERE, cid).executeSingle();
    }

    public Chat getChatFromMessage(Message message) {
        return new Select().from(Chat.class).where(QUEUE_UID_WHERE, message.getFrom()).executeSingle();
    }

    public Chat getChatForContact(String uid) {
        return new Select().from(Chat.class).where(QUEUE_UID_WHERE, uid).executeSingle();
    }

    public List<Message> getMessagesForChat(String cid) {
        return new Select().from(Message.class).where(QUEUE_CID_WHERE, cid).execute();
    }

    public Message getMessage(String mid) {
        return new Select().from(Message.class).where(QUEUE_MID_WHERE, mid).executeSingle();
    }

    public static ChatInterface getInstance() {
        if (chatInterface == null) {
            chatInterface = new ChatInterface();
        }

        return chatInterface;
    }
}
