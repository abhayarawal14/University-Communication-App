package com.abhay.universityapplication.model;

public class Chat {
    private String message;
    private String msgReceiver;
    private String msgSender;
    private String type;

    public Chat(String message, String msgReceiver, String msgSender, String type) {
        this.message = message;
        this.msgReceiver = msgReceiver;
        this.msgSender = msgSender;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgReceiver() {
        return msgReceiver;
    }

    public void setMsgReceiver(String msgReceiver) {
        this.msgReceiver = msgReceiver;
    }

    public String getMsgSender() {
        return msgSender;
    }

    public void setMsgSender(String msgSender) {
        this.msgSender = msgSender;
    }
}
