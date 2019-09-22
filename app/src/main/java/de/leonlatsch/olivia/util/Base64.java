package de.leonlatsch.olivia.util;

public class Base64 {

    public static String toBase64(byte[] bytes) {
        return android.util.Base64.encodeToString(bytes, 0);
    }

    public static byte[] toBytes(String base64) {
        return android.util.Base64.decode(base64, 0);
    }
}
