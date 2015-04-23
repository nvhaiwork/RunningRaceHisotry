package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by quynt3 on 2015/04/23.
 */
public class AddLikeRequest extends BasePostRequest {

    public AddLikeRequest(String raceId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_ADD_LIKE_OF_RACE);
        setPostParam("race_id", raceId);
    }


    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_ADD_LIKE;
    }
}
