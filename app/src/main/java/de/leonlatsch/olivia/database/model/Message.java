package de.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.sql.Timestamp;

@Table(name = "message")
public class Message extends Model {

    @Column(name = "mid", index = true)
    private String mid;

    @Column(name = "cid")
    private String cid;

    @Column(name = "uid_from")
    private int from;

    @Column(name = "uid_to")
    private int to;

    @Column(name = "type")
    private String type;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "content")
    private String content;

    public Message() {}

    public Message(String mid, String cid, int from, int to, String type, Timestamp timestamp, String content) {
        this.mid = mid;
        this.cid = cid;
        this.from = from;
        this.to = to;
        this.type = type;
        this.timestamp = timestamp;
        this.content = content;
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

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
