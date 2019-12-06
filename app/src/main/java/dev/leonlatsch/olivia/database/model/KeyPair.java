package dev.leonlatsch.olivia.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "key_pair")
public class KeyPair extends Model {

    @Column(name = "uid", index = true)
    private String uid;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "private_key")
    private String privateKey;

    public KeyPair() {}

    public KeyPair(String uid, String publicKey, String privateKey) {
        this.uid = uid;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
