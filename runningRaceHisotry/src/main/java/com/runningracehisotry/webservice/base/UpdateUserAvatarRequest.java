package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/22/2015.
 */
public class UpdateUserAvatarRequest extends BasePostRequest {

    public UpdateUserAvatarRequest(String id, String email, String fullName, String name, String avatarUrl) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_UPDATE_USER_PROFILE + id);
        setPostParam("id", id);
        setPostParam("email", email);
        setPostParam("full_name", fullName);
        setPostParam("name", name);
        setPostParam("profile_image", avatarUrl);
    }
    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_UPDATE_USER_AVATAR;
    }
}
