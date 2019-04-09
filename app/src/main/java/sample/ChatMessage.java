package sample;

/**
 * Created by atul on 6/4/19.
 */

public class ChatMessage {

    public ChatMessage(String message, int sent) {
        this.message = message;
        this.sent = sent;
    }

    private String message;
    private int sent;

    public String getMessage() {
        return message;
    }

    public int getSent() {
        return sent;
    }
}
