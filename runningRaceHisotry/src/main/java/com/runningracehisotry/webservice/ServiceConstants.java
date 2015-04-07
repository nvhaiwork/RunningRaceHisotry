package com.runningracehisotry.webservice;

/**
 * Created by Mr J on 4/1/2015.
 */
public class ServiceConstants {

    // User method
    public static final String METHOD_REGISTER = "Register";
    public static final String METHOD_LOGIN = "Login";
    public static final String METHOD_GET_USER_PROFILE = "getUserProfile";
    public static final String METHOD_UPDATE_USER_PROFILE = "updateUserProfile";
    public static final String METHOD_GET_ALL_RUNNERS = "getAllRunners";

    // Shoes method
    public static final String METHOD_ADD_SHOES = "addShoes";
    public static final String METHOD_GET_ALL_SHOES_WITH_RELATE_OBJ = "getAllShoesWithRelateObj";
    public static final String METHOD_GET_ALL_SHOES = "getAllShoes";
    public static final String METHOD_UPDATE_SHOES = "updateShoes";
    public static final String METHOD_DELETE_SHOES_BY_ID = "deleteShoesById";
    public static final String METHOD_GET_SHOES_BY_ID = "getShoesById";

    // Race method
    public static final String METHOD_GET_RACE_BY_ID = "getRaceById";
    public static final String METHOD_ADD_RACE = "addRace";
    public static final String METHOD_UPDATE_RACE = "updateRace";
    public static final String METHOD_DELETE_RACE_BY_ID = "deleteRaceById";
}
