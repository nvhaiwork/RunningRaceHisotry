package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by QuyNguyen on 4/12/2015.
 */
public class Runner {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("email")
    private String email;

    @SerializedName("type")
    private int type;

    @SerializedName("createdAt")
    private String created_at;

    @SerializedName("updatedAt")
    private String updated_at;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getEmail() {
        return email;
    }

    public int getType() {
        return type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
