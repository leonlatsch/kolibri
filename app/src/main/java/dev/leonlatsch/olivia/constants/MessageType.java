package dev.leonlatsch.olivia.constants;

/**
 * Used to determine the type of a {@link dev.leonlatsch.olivia.database.model.Message}
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class MessageType {

    private static final String UNDEFINED = "UNDEFINED";
    public static final String TEXT = "TEXT";
    public static final String IMAGE = "IMAGE";
    public static final String AUDIO = "AUDIO";
    public static final String VIDEO = "VIDEO";

    private MessageType() {}
}
