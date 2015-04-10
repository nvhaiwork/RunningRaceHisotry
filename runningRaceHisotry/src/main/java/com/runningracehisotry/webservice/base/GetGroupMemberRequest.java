package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetGroupMemberRequest extends BaseGetRequest{

    public GetGroupMemberRequest(int groupId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_GROUP_OF_USER);
        this.setParam("groupId", groupId);
    }

    public GetGroupMemberRequest(String groupId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_GROUP_OF_USER);
        this.setParam("groupId", groupId);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_ALL_GROUP;
    }
}