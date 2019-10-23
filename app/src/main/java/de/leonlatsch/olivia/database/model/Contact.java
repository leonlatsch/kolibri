package de.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "contact")
public class Contact extends Model {

    @Column(name = "contact_id", index = true)
    private String contactId;

    @Column(name = "uid")
    private String uid;

    @Column(name = "username")
    private String username;

    @Column(name = "profile_pic_tn")
    private String profilePicTn;

    @Column(name = "public_key")
    private String publicKey;

    public Contact() {}

    public Contact(String contactId, String uid, String username, String profilePicTn, String publicKey) {
        this.contactId = contactId;
        this.uid = uid;
        this.username = username;
        this.profilePicTn = profilePicTn;
        this.publicKey = publicKey;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicTn() {
        return profilePicTn;
    }

    public void setProfilePicTn(String profilePicTn) {
        this.profilePicTn = profilePicTn;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
