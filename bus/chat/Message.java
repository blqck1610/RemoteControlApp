package bus.chat;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public final static int STRING_MESSAGE = 0x1;

    private int currentType;
    private String sender;

    public Message() {
        // TODO: undefined message
        this.currentType = 0x1;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return this.sender;
    }
}
