package com.runningracehisotry.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * create by NTQ
 */
public class Race {
    @SerializedName("id")
    private int id;

    @SerializedName("shoes_id")
    private int shoeID;

    @SerializedName("user_id")
    private int userID;

    @SerializedName("name")
    private String name;

    @SerializedName("state")
    private String state;

    @SerializedName("event_type")
    private int evenType;

    @SerializedName("person_url")
    private String personUrl;

    @SerializedName("bib_url")
    private String bibUrl;

    @SerializedName("city")
    private String city;

    @SerializedName("website")
    private String website;

    @SerializedName("medal_url")
    private String medalUrl;

    @SerializedName("race_date")
    private String raceDate;

    @SerializedName("finisher_time")
    private String finisherTime;

    @SerializedName("finisher_date_time")
    private String finisherDateTime;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("race_miles")
    private String raceMiles;

    @SerializedName("race_km")
    private String raceKm;


    @SerializedName("average_pace")
    private String averagePace;

    @SerializedName("time_ago")
    private String timeAgo;

    @SerializedName("shoes")
    private Shoe shoe = new Shoe();;

    @SerializedName("likes")
    private List<Like> likes = new ArrayList<Like>();

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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getEvenType() {
        return evenType;
    }

    public void setEvenType(int evenType) {
        this.evenType = evenType;
    }

    public String getPersonUrl() {
        return personUrl;
    }

    public void setPersonUrl(String personUrl) {
        this.personUrl = personUrl;
    }

    public String getBibUrl() {
        return bibUrl;
    }

    public void setBibUrl(String bibUrl) {
        this.bibUrl = bibUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMedalUrl() {
        return medalUrl;
    }

    public void setMedalUrl(String medalUrl) {
        this.medalUrl = medalUrl;
    }

    public String getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(String raceDate) {
        this.raceDate = raceDate;
    }

    public String getFinisherTime() {
        return finisherTime;
    }

    public void setFinisherTime(String finisherTime) {
        this.finisherTime = finisherTime;
    }

    public String getFinisherDateTime() {
        return finisherDateTime;
    }

    public void setFinisherDateTime(String finisherDateTime) {
        this.finisherDateTime = finisherDateTime;
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

    public String getRaceMiles() {
        return raceMiles;
    }

    public void setRaceMiles(String raceMiles) {
        this.raceMiles = raceMiles;
    }

    public String getRaceKm() {
        return raceKm;
    }

    public void setRaceKm(String raceKm) {
        this.raceKm = raceKm;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public String getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(String averagePace) {
        this.averagePace = averagePace;
    }
}
