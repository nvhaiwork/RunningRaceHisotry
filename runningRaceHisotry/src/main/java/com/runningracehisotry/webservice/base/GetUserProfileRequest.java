package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class GetUserProfileRequest extends BaseGetRequest {

    public GetUserProfileRequest() {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_USER_PROFILE);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_USER_PROFILE;
    }
}
