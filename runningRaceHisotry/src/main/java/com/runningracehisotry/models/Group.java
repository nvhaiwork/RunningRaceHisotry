package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by quynt3 on 2015/04/18.
 */
public class Group {
    @SerializedName("id")
    private String groupId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("name")
    private String groupName;

    @SerializedName("created_at")
    private String createDate;

    @SerializedName("update_at")
    private String updateDate;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
}
