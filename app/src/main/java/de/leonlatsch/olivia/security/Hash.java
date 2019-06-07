package de.leonlatsch.olivia.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash {

    private static final String SHA256 = "SHA256";

    public static String createHexHash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256);
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return createHexString(bytes);
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    private static String createHexString(byte[] digest) {
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            if ((0xff & digest[i]) < 0x10) {
                hex.append("0" + Integer.toHexString((0xFF & digest[i])));
            } else {
                hex.append(Integer.toHexString(0xFF & digest[i]));
            }
        }
        return hex.toString();
    }
}
