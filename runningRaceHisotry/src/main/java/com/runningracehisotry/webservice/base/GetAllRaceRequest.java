package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetAllRaceRequest extends BaseGetRequest{

    public GetAllRaceRequest() {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_ALL_RACES);

    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_RACES_BY_TYPE;
    }
}