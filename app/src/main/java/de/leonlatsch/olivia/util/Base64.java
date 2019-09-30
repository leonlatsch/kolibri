package de.leonlatsch.olivia.util;

public class Base64 {

    public static String toBase64(byte[] bytes) {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    public static byte[] toBytes(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.NO_WRAP);
    }
}
