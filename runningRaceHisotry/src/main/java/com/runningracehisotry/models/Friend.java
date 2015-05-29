package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;


/**
 * Created by quynt3 on 2015/04/18.
 */
public class Friend {

    @SerializedName("id")
    private String group;

    @SerializedName("user_id")
    private String friendId;

    @SerializedName("group_id")
    private int groupId;

    @SerializedName("created_at")
    private String createDate;

    @SerializedName("update_at")
    private String updateDate;

    @SerializedName("user")
    private User friend;

    private int newMessage;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public int getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(int newMessage) {
        this.newMessage = newMessage;
    }
}
