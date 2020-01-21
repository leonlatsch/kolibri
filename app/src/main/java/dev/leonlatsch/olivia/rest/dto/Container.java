package dev.leonlatsch.olivia.rest.dto;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public class Container<T> {

    private int code;
    private String message;
    private String timestamp;
    private T content;

    public Container() {
    }

    public Container(int code, String message, String timestamp, T content) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
