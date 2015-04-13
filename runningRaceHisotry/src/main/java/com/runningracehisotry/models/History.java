package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ngoc yen on 4/12/2015.
 */
public class History {
    @SerializedName("id")
    private int id;

    @SerializedName("shoes_id")
    private int shoesID;

    @SerializedName("miles")
    private String miles;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShoesID() {
        return shoesID;
    }

    public void setShoesID(int shoesID) {
        this.shoesID = shoesID;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
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
