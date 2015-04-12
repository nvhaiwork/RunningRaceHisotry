package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/12/2015.
 */
public class AddGroupMemberRequest  extends BasePostRequest {

    public AddGroupMemberRequest(int friendId, int groupId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_ADD_GROUP_MEMBER);
        setParam("user_id", friendId);
        setParam("group_id", groupId);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_ADD_GROUP_MEMBER;
    }
}
