package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/12/2015.
 */
public class AddGroupRequest extends BasePostRequest {

    public AddGroupRequest() {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_ADD_GROUP);
        setPostParam("name", "Friend");

    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_ADD_GROUP;
    }
}
