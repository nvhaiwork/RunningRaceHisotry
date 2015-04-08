package com.runningracehisotry.models;

/**
 * Created by manh on 04/08/15.
 */
public class User {
    private String id;
    private String name;
    private String full_name;
    private String email;
    private String type;
    private String profile_image;
    private String created_at;
    private String updated_at;
    private String android_token;
    private String ios_token;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getAndroid_token() {
        return android_token;
    }

    public String getIos_token() {
        return ios_token;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setAndroid_token(String android_token) {
        this.android_token = android_token;
    }

    public void setIos_token(String ios_token) {
        this.ios_token = ios_token;
    }
}
