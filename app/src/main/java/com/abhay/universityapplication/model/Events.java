package com.abhay.universityapplication.model;

public class Events {
    String postId;
    String content;
    String eventImage;
    String userId;
    String type;

    public Events() {
    }

    public Events(String postId, String content, String eventImage, String userId, String type) {
        this.postId = postId;
        this.content = content;
        this.eventImage = eventImage;
        this.userId = userId;
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
