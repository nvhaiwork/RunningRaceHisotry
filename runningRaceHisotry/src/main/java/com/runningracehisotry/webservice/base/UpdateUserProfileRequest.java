package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class UpdateUserProfileRequest extends BasePostRequest {

    public UpdateUserProfileRequest(String email, String fullName, String id, String name, String profileImage) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_UPDATE_USER_PROFILE + id);
        setPostParam("email", email);
        setPostParam("full_name", fullName);
        setPostParam("id", id);
        setPostParam("name", name);
        setPostParam("profile_image", profileImage);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_UPDATE_USER_PROFILE;
    }
}
