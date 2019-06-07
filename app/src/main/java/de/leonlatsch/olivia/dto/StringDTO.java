package de.leonlatsch.olivia.dto;

public class StringDTO extends BaseDTO {

    private String message;

    public StringDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
