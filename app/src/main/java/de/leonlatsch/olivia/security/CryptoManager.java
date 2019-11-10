package de.leonlatsch.olivia.security;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.leonlatsch.olivia.util.Base64;

public class CryptoManager {

    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 2048;

    public static KeyPair genKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null; // Should never happen case
        }
    }

    private static PublicKey decodePublicKey(String encodedPublicKey) {
        PublicKey publicKey;

        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.toBytes(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            publicKey = null;
        }

        return publicKey;
    }

    private static PrivateKey decodePrivateKey(String encodedPrivateKey) {
        PrivateKey privateKey;

        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.toBytes(encodedPrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            privateKey = null;
        }

        return privateKey;
    }

    private static byte[] encrypt(byte[] data, PublicKey publicKey) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException(e.getMessage(), e.getCause());
        }
    }

    private static byte[] decrypt(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException(e.getMessage(), e.getCause());
        }
    }

    public static String encryptAndEncode(byte[] data, String encodedPublicKey) {
        try {
            PublicKey publicKey = decodePublicKey(encodedPublicKey);
            byte[] rawData = encrypt(data, publicKey);
            return Base64.toBase64(rawData);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    public static byte[] decryptAndDecode(String encodedData, String encodedPrivateKey) {
        try {
            PrivateKey privateKey = decodePrivateKey(encodedPrivateKey);
            return decrypt(Base64.toBytes(encodedData), privateKey);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }
}
