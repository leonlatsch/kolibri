package de.leonlatsch.olivia.dto;

import java.sql.Timestamp;

public class MessageDTO {

    private String mid;
    private int from;
    private int to;
    private String type;
    private String timestamp;
    private String content;
    private String cid;

    public MessageDTO() {}

    public MessageDTO(String mid, int from, int to, String type, String timestamp, String content, String cid) {
        this.mid = mid;
        this.from = from;
        this.to = to;
        this.type = type;
        this.timestamp = timestamp;
        this.content = content;
        this.cid = cid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
