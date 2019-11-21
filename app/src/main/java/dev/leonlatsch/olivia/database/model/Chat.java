package dev.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "chat")
public class Chat extends Model {

    @Column(name = "cid", index = true)
    private String cid;

    @Column(name = "uid")
    private String uid;

    @Column(name = "unread_messages")
    private int unreadMessages;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_timestamp")
    private String lastTimestamp;

    public Chat() {}

    public Chat(String cid, String uid, int unreadMessages, String lastMessage, String lastTimestamp) {
        this.cid = cid;
        this.uid = uid;
        this.unreadMessages = unreadMessages;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(String lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
