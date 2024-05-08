package me.maxpro.workscheduler.client;

public class ClientException extends RuntimeException {

    private final String displayText;

    public ClientException(String text, String displayText) {
        super(text);
        this.displayText = displayText;
    }

    public ClientException(String text, Throwable t, String displayText) {
        super(text, t);
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

}
