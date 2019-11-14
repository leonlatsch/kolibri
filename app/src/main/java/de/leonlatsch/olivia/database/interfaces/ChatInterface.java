package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.database.model.Chat;
import de.leonlatsch.olivia.database.model.Message;

public class ChatInterface extends BaseInterface {

    private static final String QUEUE_CID_WHERE = "cid = ?";
    private static final String QUEUE_MID_WHERE = "mid = ?";
    private static final String QUEUE_UID_WHERE = "uid = ?";

    private static ChatInterface chatInterface; // Singleton

    private ChatInterface() {
    }

    public void saveMessage(Message message) {
        Message savedMessage = new Select().from(Message.class).where(QUEUE_MID_WHERE, message.getMid()).executeSingle();
        if (savedMessage != null) {
            message.save();
        }
    }

    public void saveChat(Chat chat) {
        chat.save();
    }

    public void saveMessage(MessageDTO message) {
        Message model = getDatabaseMapper().toModel(message);
        saveMessage(model);
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

    public Chat getChat(String cid) {
        return new Select().from(Chat.class).where(QUEUE_CID_WHERE, cid).executeSingle();
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
