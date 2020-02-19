package dev.leonlatsch.kolibri.util

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
object Base64 {

    fun toBase64(bytes: ByteArray): String {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    fun toBytes(base64: String): ByteArray {
        return android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
    }
}
