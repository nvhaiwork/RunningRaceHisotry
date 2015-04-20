package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class UpdateUserProfileRequest extends BasePutRequest {

    public UpdateUserProfileRequest(String id, String fullName, String profileImage,
                                    String oldPassword, String password, String confirmPassword) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_UPDATE_USER_PROFILE + id);
        setPostParam("full_name", fullName);
        setPostParam("profile_image", profileImage);
        setPostParam("oldPassword", oldPassword);
        setPostParam("password", password);
        setPostParam("confirmPassword", confirmPassword);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_UPDATE_USER_PROFILE;
    }
}
