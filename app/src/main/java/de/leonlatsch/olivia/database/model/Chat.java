package de.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "chat")
public class Chat extends Model {

    @Column(name = "cid", index = true)
    private String cid;

    @Column(name = "uid")
    private String uid;

    public Chat() {}

    public Chat(String cid, String uid) {
        this.cid = cid;
        this.uid = uid;
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

    public void setUid(String contactId) {
        this.uid = contactId;
    }
}
