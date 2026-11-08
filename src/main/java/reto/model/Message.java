package reto.model;

import java.io.Serializable;

/**
 * Único objeto que viaja entre cliente y servidor (writeObject / readObject).
 * Tiene que ser Serializable y la clase tiene que ser idéntica en los dos lados.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Type type;
    private String messageContents;
    private String recipientName;
    private String messengerName;

    public Message(Type type, String messageContents, String recipientName, String messengerName) {
        this.type = type;
        this.messageContents = messageContents;
        this.recipientName = recipientName;
        this.messengerName = messengerName;
    }

    public Message(Type type, String messageContents, String messengerName) {
        this.type = type;
        this.messageContents = messageContents;
        this.messengerName = messengerName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessageContents() {
        return messageContents;
    }

    public void setMessageContents(String messageContents) {
        this.messageContents = messageContents;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getMessengerName() {
        return messengerName;
    }

    public void setMessengerName(String messengerName) {
        this.messengerName = messengerName;
    }

    public boolean isPrivate() {
        return type == Type.PRIVATE;
    }

    @Override
    public String toString() {
        switch (type) {
            case PRIVATE:
                return "[Privado] @" + messengerName + " para @" + recipientName + ": " + messageContents;
            case PUBLIC:
                if (recipientName != null && !recipientName.isEmpty()) {
                    return messengerName + " menciona a @" + recipientName + ": " + messageContents;
                }
                return messengerName + " dice: " + messageContents;
            default:
                return "[" + messageContents + "]";
        }
    }

}
