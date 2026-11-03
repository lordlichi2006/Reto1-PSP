package reto.model;

public class Message {
    private String messageContents;
    private String recipientName;
    private String messengerName;
    private boolean isPrivate;

    public Message(String messageContents, String recipientName, boolean isPrivate, String messengerName) {
        this.messageContents = messageContents;
        this.recipientName = recipientName;
        this.isPrivate = isPrivate;
        this.messengerName = messengerName;
    }

    public Message(String messageContents, String messengerName) {
        this.messageContents = messageContents;
        this.messengerName = messengerName;
        this.isPrivate = false;
        this.recipientName = null;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getMessengerName() {
        return messengerName;
    }

    public void setMessengerName(String messengerName) {
        this.messengerName = messengerName;
    }

    @Override
    public String toString() {
        if (isPrivate) {
            return "[Private] | " + messengerName + " says: " + messageContents;
        }
        if (!recipientName.equals("")) {
            return messengerName + " mentions @" + recipientName + ": " + messageContents;
        }
        return messengerName + " says: " + messageContents;
    }

}
