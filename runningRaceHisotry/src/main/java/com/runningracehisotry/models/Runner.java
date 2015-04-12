package com.runningracehisotry.models;

/**
 * Created by QuyNguyen on 4/12/2015.
 */
public class Runner {
    private int runnerId;
    private String username;
    private String avatarUrl;
    public Runner(int userId, String username, String img){
        this.runnerId = userId;
        this.username = username;
        this.avatarUrl = img;
    }

    public int getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(int runnerId) {
        this.runnerId = runnerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
