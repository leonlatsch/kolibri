package dev.leonlatsch.kolibri.security

import dev.leonlatsch.kolibri.util.toHexString
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
object Hash {

    private val SHA256 = "SHA256"

    /**
     * Create a hex hash from String
     *
     * @param data
     * @return A hex hash String
     */
    fun createHexHash(data: String): String? {
        return try {
            val md = MessageDigest.getInstance(SHA256)
            val bytes = md.digest(data.toByteArray(StandardCharsets.UTF_8))
            bytes.toHexString()
        } catch (e: Throwable) {
            System.err.println(e)
            null
        }

    }
}
