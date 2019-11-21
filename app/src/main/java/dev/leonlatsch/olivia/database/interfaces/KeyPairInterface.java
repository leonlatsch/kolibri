package dev.leonlatsch.olivia.database.interfaces;

public class KeyPairInterface extends BaseInterface{

    private static KeyPairInterface keyPairInterface; // Singleton

    private KeyPairInterface() {}

    public static KeyPairInterface getInstance() {
        if (keyPairInterface == null) {
            keyPairInterface = new KeyPairInterface();
        }

        return keyPairInterface;
    }
}
