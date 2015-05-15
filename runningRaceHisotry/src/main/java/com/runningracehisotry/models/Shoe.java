package com.runningracehisotry.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.runningracehisotry.utilities.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private List<History> milesShoesHistories;

    @SerializedName("miles_shoes_histories")
    //private List<History> milesShoesHistories;
    private Object milesShoesHistoriesString;

    public Shoe(int shoeId, String brand, String model, String imageUrl, float miles, int userId) {
        this.id = shoeId;
        this.brand = brand;
        this.model = model;
        this.imageUrl = imageUrl;
        this.milesOnShoes = miles;
        this.userId = userId;
    }

    public Shoe() {

    }

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
        Gson gson = new Gson();

        Type listType= null;

        try {
            LogUtil.d("UTILITY", "error milesShoesHistoriesString: " + milesShoesHistoriesString);
            if (milesShoesHistoriesString instanceof JSONArray) {
                LogUtil.d("UTILITY", "error Array: " + milesShoesHistoriesString);
                listType = new TypeToken<List<History>>() {
                }.getType();
                milesShoesHistories = gson.fromJson(((JSONArray)milesShoesHistoriesString).toString(), listType);

            }
            else if (milesShoesHistoriesString instanceof JSONObject) {
                LogUtil.d("UTILITY", "error Object: " + milesShoesHistoriesString);
                listType = new TypeToken<Map<Integer, Shoe>>() {
                }.getType();
                Map<Integer, History> map = gson.fromJson(((JSONObject)milesShoesHistoriesString).toString(), listType);
                if (map != null && map.size() > 0) {
                    milesShoesHistories = new ArrayList<History>();
                    for (Integer integer : map.keySet()) {
                        milesShoesHistories.add(map.get(integer));
                    }
                }
            }
            else{
                LogUtil.d("UTILITY", "error NONE");
            }
        }
        catch (Exception e) {
            LogUtil.d("UTILITY", "error when getn history shoe: " +e.getMessage());
                    e.printStackTrace();
        }
        LogUtil.d("UTILITY", "error milesShoesHistories: " +(milesShoesHistories == null));
        //LogUtil.d("UTILITY", "error milesShoesHistories: " +milesShoesHistories.size());
        return milesShoesHistories;
    }

    public void setMilesShoesHistories(List<History> milesShoesHistories) {
        this.milesShoesHistories = milesShoesHistories;
    }

    public Object getMilesShoesHistoriesString() {
        return milesShoesHistoriesString;
    }

    public void setMilesShoesHistoriesString(Object milesShoesHistoriesString) {
        this.milesShoesHistoriesString = milesShoesHistoriesString;
    }
}