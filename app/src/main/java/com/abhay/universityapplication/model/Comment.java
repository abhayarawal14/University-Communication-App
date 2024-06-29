package com.abhay.universityapplication.model;

public class Comment {

    String id;
    String comment;
    String userId;

    public Comment(String id, String comment, String userId) {
        this.id = id;
        this.comment = comment;
        this.userId = userId;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
