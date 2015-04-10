package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;


/**
 * Created by manh on 04/09/15.
 */
public class ForgotPasswordRequest extends BasePostRequest {

    public ForgotPasswordRequest(String email) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_FORGOT_PASSWORD);
        setPostParam("email", email);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_FORGOT_PASSWORD;
    }
}
