package dev.leonlatsch.kolibri.security

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
        try {
            val md = MessageDigest.getInstance(SHA256)
            val bytes = md.digest(data.getBytes(StandardCharsets.UTF_8))
            return createHexString(bytes)
        } catch (e: Exception) {
            System.err.println(e)
            return null
        }

    }

    /**
     * Create a hex hash from byte[]
     *
     * @param digest
     * @return A hey hash String
     */
    private fun createHexString(digest: ByteArray): String {
        val hex = StringBuffer()
        for (i in digest.indices) {
            if (0xff and digest[i] < 0x10) {
                hex.append("0" + Integer.toHexString(0xFF and digest[i]))
            } else {
                hex.append(Integer.toHexString(0xFF and digest[i]))
            }
        }
        return hex.toString()
    }
}
