package dev.leonlatsch.kolibri.security

import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import javax.crypto.Cipher

import dev.leonlatsch.kolibri.database.model.KeyPair
import dev.leonlatsch.kolibri.util.Base64

/**
 * @author Leon Latsch
 * @since 1.0.0
 *
 *
 * A Util class to manage end-to-end encryption of messages
 */
object CryptoManager {

    private const val RSA = "RSA"
    private const val KEY_SIZE = 2048

    /**
     * Generate a [KeyPair] with RSA 2048 bit
     *
     * @return The generated RSA KeyPair
     */
    fun genKeyPair(): KeyPair? {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(RSA)
            keyPairGenerator.initialize(KEY_SIZE)
            val keyPair = keyPairGenerator.genKeyPair()
            return KeyPair(null, Base64.toBase64(keyPair.getPublic().getEncoded()), Base64.toBase64(keyPair.getPrivate().getEncoded()))
        } catch (e: NoSuchAlgorithmException) {
            return null // Should never happen case
        }

    }

    /**
     * Decode a base64 public key received from the backend
     *
     * @param encodedPublicKey
     * @return The decoded public key
     */
    private fun decodePublicKey(encodedPublicKey: String): PublicKey? {
        var publicKey: PublicKey?

        try {
            val keySpec = X509EncodedKeySpec(Base64.toBytes(encodedPublicKey))
            val keyFactory = KeyFactory.getInstance(RSA)
            publicKey = keyFactory.generatePublic(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            publicKey = null
        } catch (e: InvalidKeySpecException) {
            publicKey = null
        }

        return publicKey
    }

    /**
     * Decode a base64 private key
     *
     * @param encodedPrivateKey
     * @return The decoded private key
     */
    private fun decodePrivateKey(encodedPrivateKey: String): PrivateKey? {
        var privateKey: PrivateKey?

        try {
            val keySpec = PKCS8EncodedKeySpec(Base64.toBytes(encodedPrivateKey))
            val keyFactory = KeyFactory.getInstance(RSA)
            privateKey = keyFactory.generatePrivate(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            privateKey = null
        } catch (e: InvalidKeySpecException) {
            privateKey = null
        }

        return privateKey
    }

    /**
     * Encrypt a byte[] with a decoded public key
     *
     * @param data      The plain byte[]
     * @param publicKey The public key to use for encryption
     * @return The encrypted data
     * @throws GeneralSecurityException
     */
    @Throws(GeneralSecurityException::class)
    private fun encrypt(data: ByteArray, publicKey: PublicKey?): ByteArray {
        try {
            val cipher = Cipher.getInstance(RSA)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return cipher.doFinal(data)
        } catch (e: GeneralSecurityException) {
            throw GeneralSecurityException(e.message, e.cause)
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
    @Throws(GeneralSecurityException::class)
    private fun decrypt(data: ByteArray, privateKey: PrivateKey?): ByteArray {
        try {
            val cipher = Cipher.getInstance(RSA)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return cipher.doFinal(data)
        } catch (e: GeneralSecurityException) {
            throw GeneralSecurityException(e.message, e.cause)
        }

    }

    /**
     * Encrypt and encode a byte[] to a base64 String
     *
     * @param data             The plain byte[]
     * @param encodedPublicKey The encoded public key
     * @return The encrypted data as a base64 encoded String
     */
    fun encryptAndEncode(data: ByteArray, encodedPublicKey: String): String? {
        try {
            val publicKey = decodePublicKey(encodedPublicKey)
            val rawData = encrypt(data, publicKey)
            return Base64.toBase64(rawData)
        } catch (e: GeneralSecurityException) {
            return null
        }

    }

    /**
     * Decrypt and decode a encoded base64 String
     *
     * @param encodedData       The encoded base64 String
     * @param encodedPrivateKey The encoded private key
     * @return The decrypted data as plain byte[]
     */
    fun decryptAndDecode(encodedData: String, encodedPrivateKey: String): ByteArray? {
        try {
            val privateKey = decodePrivateKey(encodedPrivateKey)
            return decrypt(Base64.toBytes(encodedData), privateKey)
        } catch (e: GeneralSecurityException) {
            return null
        }

    }
}
