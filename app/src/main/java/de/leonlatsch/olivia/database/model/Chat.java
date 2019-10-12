package de.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "chat")
public class Chat extends Model {

    @Column(name = "cid", index = true)
    private String cid;

    @Column(name = "contact_id")
    private String contactId;

    public Chat() {}

    public Chat(String cid, String contactId) {
        this.cid = cid;
        this.contactId = contactId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
