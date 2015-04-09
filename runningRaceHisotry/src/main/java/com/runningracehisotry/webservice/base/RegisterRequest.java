package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class RegisterRequest extends BasePostRequest {

    public RegisterRequest(String name, String password, String email) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_REGISTER);
        setPostParam("name", name);
        setPostParam("password", password);
        setPostParam("email", email);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_REGISTER;
    }
}
