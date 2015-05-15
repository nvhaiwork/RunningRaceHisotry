package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;


/**
 * Created by manh on 04/09/15.
 */
public class UpdateShoeRequest extends BasePutRequest {

    public UpdateShoeRequest(String brand, String shoesId, String imageUrl, String milesHistories,
                             String milesOnShoes, String model, String userId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_UPDATE_SHOES + shoesId);
        setPostParam("brand", brand);
        setPostParam("image_url", imageUrl);
        setPostParam("miles_histories", milesHistories);
        setPostParam("miles_on_shoes", milesOnShoes);
        setPostParam("model", model);
        setPostParam("user_id", userId);
        setPostParam("id", shoesId);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_UPDATE_SHOES;
    }
}
