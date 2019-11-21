package dev.leonlatsch.olivia.util;

import java.util.UUID;

public class Generator {

    public static String genUUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
