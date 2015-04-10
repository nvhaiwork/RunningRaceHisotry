package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetRaceRequest extends BaseGetRequest{

    public GetRaceRequest(int raceId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_RACE_BY_ID + String.valueOf(raceId));

    }

    public GetRaceRequest(String raceId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_RACE_BY_ID + raceId);

    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_RACE_BY_ID;
    }
}