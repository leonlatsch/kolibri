package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import de.leonlatsch.olivia.entity.PublicKey;
import de.leonlatsch.olivia.util.Base64;

public class PublicKeyInterface {

    private static final String QUEUE_WHERE_UID = "uid = ?";

    private static PublicKeyInterface publicKeyInterface; // Singleton

    private PublicKeyInterface() {}

    public byte[] getKey(int uid) {
        String raw = getKeyString(uid);
        return Base64.toBytes(raw);
    }

    public String getKeyString(int uid) {
        PublicKey publicKey = getRaw(uid);
        if (publicKey == null) {
            return null;
        } else {
            return publicKey.getKey();
        }
    }

    public PublicKey getRaw(int uid) {
        return new Select()
                .from(PublicKey.class)
                .where(QUEUE_WHERE_UID, uid)
                .executeSingle();
    }

    public void save(PublicKey publicKey) {
        PublicKey saved = getRaw(publicKey.getUid());
        if (saved != null) {
            saved.delete();
        }
        publicKey.save();
    }

    public static PublicKeyInterface getInstance() {
        if (publicKeyInterface == null) {
            publicKeyInterface = new PublicKeyInterface();
        }

        return publicKeyInterface;
    }
}
