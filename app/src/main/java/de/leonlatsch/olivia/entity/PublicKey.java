package de.leonlatsch.olivia.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "public_key")
public class PublicKey extends Model {

    @Column(name = "key", index = true)
    private String key;

    @Column(name = "uid")
    private int uid;

    public PublicKey() {}

    public PublicKey(String key, int uid) {
        this.key = key;
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
