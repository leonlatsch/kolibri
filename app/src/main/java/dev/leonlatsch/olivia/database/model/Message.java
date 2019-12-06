package dev.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "message")
public class Message extends Model {

    @Column(name = "mid", index = true)
    private String mid;

    @Column(name = "cid")
    private String cid;

    @Column(name = "uid_from")
    private String from;

    @Column(name = "uid_to")
    private String to;

    @Column(name = "type")
    private String type;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "content")
    private String content;

    @Column(name = "sent")
    private boolean sent;

    public Message() {}

    public Message(String mid, String cid, String from, String to, String type, String timestamp, String content) {
        this.mid = mid;
        this.cid = cid;
        this.from = from;
        this.to = to;
        this.type = type;
        this.timestamp = timestamp;
        this.content = content;
        this.sent = false;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
