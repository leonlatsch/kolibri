package dev.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.security.Key;

import dev.leonlatsch.olivia.database.model.KeyPair;

public class KeyPairInterface extends BaseInterface{

    private static final String QUEUE_UID_WHERE = "uid = ?";

    private static KeyPairInterface keyPairInterface; // Singleton

    private KeyPairInterface() {}

    public KeyPair createOrGet(KeyPair keyPair) {
        if (keyPair == null || keyPair.getUid() == null) {
            return null;
        }

        KeyPair saved = new Select().from(KeyPair.class).where(QUEUE_UID_WHERE, keyPair.getUid()).executeSingle();

        if (saved == null) {
            keyPair.save();
            return keyPair;
        } else {
            return saved;
        }
    }

    public KeyPair createOrGet(KeyPair keyPair, String uid) {
        if (keyPair == null) {
            return null;
        }

        keyPair.setUid(uid);
        return createOrGet(keyPair);
    }

    public KeyPair get(String uid) {
        return new Select().from(KeyPair.class).where(QUEUE_UID_WHERE, uid).executeSingle();
    }

    public static KeyPairInterface getInstance() {
        if (keyPairInterface == null) {
            keyPairInterface = new KeyPairInterface();
        }

        return keyPairInterface;
    }
}
