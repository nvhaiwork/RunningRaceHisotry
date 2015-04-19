package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class RegisterFacebookRequest extends BasePostRequest {

    public RegisterFacebookRequest(String name, String fullName, String avatar) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_OAUTH_REGISTER);
        setPostParam("name", name);
        setPostParam("full_name", fullName);
        setPostParam("avatar", avatar);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_OAUTH_REGISTER;
    }
}
