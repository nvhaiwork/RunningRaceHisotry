package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class LoginRequest extends BasePostRequest {

    public LoginRequest(String name, String password, String email) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_LOGIN);
        setPostParam("name", name);
        setPostParam("password", password);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_LOGIN;
    }
}
