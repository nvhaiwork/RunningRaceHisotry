package com.runningracehisotry.models;

/**
 * Created by ngocm on 04/20/15.
 */
public class Message {
    private String userID;
    private String content;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message(String userID, String content) {
        this.userID = userID;
        this.content = content;
    }
}
