package com.runningracehisotry.models;

/**
 * create by NTQ
 */
public class Shoe{
    private int shoeId;
    private String brand;
    private String model;
    private String imageUrl;
    private float totalMiles;
    private int userId;

    public Shoe(){
    }

    public Shoe(int shoeId, String brand, String model, String imageUrl, float totalMiles, int userId){
        this.setShoeId(shoeId);
        this.setBrand(brand);
        this.setModel(model);
        this.setImageUrl(imageUrl);
        this.setTotalMiles(totalMiles);
        this.setUserId(userId);
    }

    public int getShoeId() {
        return shoeId;
    }

    public void setShoeId(int shoeId) {
        this.shoeId = shoeId;
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

    public float getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(float totalMiles) {
        this.totalMiles = totalMiles;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}