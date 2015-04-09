package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class AddShoeRequest extends BasePostRequest {

    public AddShoeRequest(String brand, String imageUrl,String milesHistories, String model) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_ADD_SHOES);
        setPostParam("brand", brand);
        setPostParam("image_url", imageUrl);
        setPostParam("miles_histories", milesHistories);
        setPostParam("model", model);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_ADD_SHOES;
    }
}
