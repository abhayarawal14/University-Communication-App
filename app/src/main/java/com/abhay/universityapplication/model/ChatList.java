package com.abhay.universityapplication.model;

public class ChatList {
    String id ;
    String receiverid;

    public ChatList(String id, String receiverid) {
        this.id = id;
        this.receiverid = receiverid;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }
}
