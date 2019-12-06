package dev.leonlatsch.olivia.util;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public class Base64 {

    public static String toBase64(byte[] bytes) {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    public static byte[] toBytes(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.NO_WRAP);
    }
}
