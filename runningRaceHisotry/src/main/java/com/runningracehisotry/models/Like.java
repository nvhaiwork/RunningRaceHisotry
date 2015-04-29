package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by quynt3 on 2015/04/19.
 */
public class Like {

    @SerializedName("id")
    private int id;

    @SerializedName("race_id")
    private int shoeID;

    @SerializedName("user_id")
    private String userID;
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public Like(int id, String userId){
        setId(id);
        setUserID(userId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShoeID() {
        return shoeID;
    }

    public void setShoeID(int shoeID) {
        this.shoeID = shoeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
