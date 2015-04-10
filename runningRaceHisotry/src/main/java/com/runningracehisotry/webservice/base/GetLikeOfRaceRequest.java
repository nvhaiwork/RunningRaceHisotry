package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetLikeOfRaceRequest extends BaseGetRequest{

    public GetLikeOfRaceRequest(int raceId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_LIKE_OF_RACE);
        this.setParam("race_id", raceId);
    }

    public GetLikeOfRaceRequest(String raceId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_LIKE_OF_RACE);
        this.setParam("race_id", raceId);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_LIKE_OF_RACE;
    }
}