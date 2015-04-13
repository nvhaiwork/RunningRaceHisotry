package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * create by NTQ
 */
public class Shoe{
    @SerializedName("id")
    private int id;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("miles_on_shoes")
    private float milesOnShoes;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("races")
    private List<Race> races;

    @SerializedName("miles_shoes_histories")
    private List<History> milesShoesHistories;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getMilesOnShoes() {
        return milesOnShoes;
    }

    public void setMilesOnShoes(float milesOnShoes) {
        this.milesOnShoes = milesOnShoes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public List<Race> getRaces() {
        return races;
    }

    public void setRaces(List<Race> races) {
        this.races = races;
    }

    public List<History> getMilesShoesHistories() {
        return milesShoesHistories;
    }

    public void setMilesShoesHistories(List<History> milesShoesHistories) {
        this.milesShoesHistories = milesShoesHistories;
    }
}