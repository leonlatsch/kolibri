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

    private static ChatInterface chatInterface; // Singleton

    private ChatInterface() {}

    public void saveMessage(Message message) {
        String cid = message.getCid();
        Chat chat = new Select().from(Chat.class).where(QUEUE_CID_WHERE, cid).executeSingle();

        if (chat == null) {
            chat = new Chat();
            chat.setCid(cid);
            //TODO: create new contact
            chat.save();
        }
        message.save();
    }

    public void saveMessage(MessageDTO message) {
        Message model = getDatabaseMapper().toModel(message);
        saveMessage(model);
    }

    public void deleteChat(String cid) {
        new Delete().from(Chat.class).where(QUEUE_CID_WHERE, cid).execute();
        new Delete().from(Message.class).where(QUEUE_CID_WHERE, cid).execute();
    }

    public Chat getChat(String cid) {
        return new Select().from(Chat.class).where(QUEUE_CID_WHERE, cid).executeSingle();
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
