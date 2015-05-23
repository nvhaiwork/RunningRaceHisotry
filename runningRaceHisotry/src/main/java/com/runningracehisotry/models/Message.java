package com.runningracehisotry.models;

/**
 * Created by ngocm on 04/20/15.
 */
public class Message {
    private String userId;
    private String content;
    private String friendId;
    private String messageId;
    private long time;
    private String ownerId;



    public Message(String messageID, String userID, String receipID, String content, long time, String owner) {
        this.messageId = messageID;
        this.userId = userID;
        this.friendId = receipID;
        this.content = content;
        this.time = time;
        this.ownerId = owner;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
