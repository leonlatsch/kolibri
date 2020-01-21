package dev.leonlatsch.olivia.security;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import dev.leonlatsch.olivia.database.model.KeyPair;
import dev.leonlatsch.olivia.util.Base64;

/**
 * @author Leon Latsch
 * @since 1.0.0
 * <p>
 * A Util class to manage end-to-end encryption of messages
 */
public class CryptoManager {

    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 2048;

    /**
     * Generate a {@link KeyPair} with RSA 2048 bit
     *
     * @return The generated RSA KeyPair
     */
    public static KeyPair genKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(KEY_SIZE);
            java.security.KeyPair keyPair = keyPairGenerator.genKeyPair();
            return new KeyPair(null, Base64.toBase64(keyPair.getPublic().getEncoded()), Base64.toBase64(keyPair.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            return null; // Should never happen case
        }
    }

    /**
     * Decode a base64 public key received from the backend
     *
     * @param encodedPublicKey
     * @return The decoded public key
     */
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

    /**
     * Decode a base64 private key
     *
     * @param encodedPrivateKey
     * @return The decoded private key
     */
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

    /**
     * Encrypt a byte[] with a decoded public key
     *
     * @param data      The plain byte[]
     * @param publicKey The public key to use for encryption
     * @return The encrypted data
     * @throws GeneralSecurityException
     */
    private static byte[] encrypt(byte[] data, PublicKey publicKey) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Decrypt a byte[] with a decoded private key
     *
     * @param data       The encrypted byte[]
     * @param privateKey The decoded private key
     * @return The decrypted plain byte[]
     * @throws GeneralSecurityException
     */
    private static byte[] decrypt(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Encrypt and encode a byte[] to a base64 String
     *
     * @param data             The plain byte[]
     * @param encodedPublicKey The encoded public key
     * @return The encrypted data as a base64 encoded String
     */
    public static String encryptAndEncode(byte[] data, String encodedPublicKey) {
        try {
            PublicKey publicKey = decodePublicKey(encodedPublicKey);
            byte[] rawData = encrypt(data, publicKey);
            return Base64.toBase64(rawData);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    /**
     * Decrypt and decode a encoded base64 String
     *
     * @param encodedData       The encoded base64 String
     * @param encodedPrivateKey The encoded private key
     * @return The decrypted data as plain byte[]
     */
    public static byte[] decryptAndDecode(String encodedData, String encodedPrivateKey) {
        try {
            PrivateKey privateKey = decodePrivateKey(encodedPrivateKey);
            return decrypt(Base64.toBytes(encodedData), privateKey);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }
}
