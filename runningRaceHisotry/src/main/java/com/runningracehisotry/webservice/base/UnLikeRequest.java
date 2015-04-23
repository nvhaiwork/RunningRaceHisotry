package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by quynt3 on 2015/04/23.
 */
public class UnLikeRequest  extends BasePostRequest {

    public UnLikeRequest(String raceId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_REMOVE_LIKE_OF_RACE);
        setPostParam("race_id", raceId);
    }


    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_REMOVE_LIKE;
    }
}
