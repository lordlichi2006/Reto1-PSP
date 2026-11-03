package reto.model;

public class Message {
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

    @Override
    public String toString() {
        switch (type) {
            case PRIVATE:
                return "[Private] | " + messengerName + " says: " + messageContents;
            case PUBLIC:
                if (!recipientName.equals("")) {
                    return messengerName + " mentions @" + recipientName + ": " + messageContents;
                }
                return messengerName + " says: " + messageContents;
            default:
                return "<<"+messageContents+">>";
        }
    }

}
