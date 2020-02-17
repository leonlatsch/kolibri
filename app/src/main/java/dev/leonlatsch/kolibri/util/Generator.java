package dev.leonlatsch.kolibri.util;

import java.util.UUID;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public class Generator {

    public static String genUUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
