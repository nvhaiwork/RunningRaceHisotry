package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetFriendRunnerRequest extends BaseGetRequest{

    /*public GetFriendRunnerRequest() {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_FRIEND_RUNNER);
    }*/

    public GetFriendRunnerRequest(String username, String password, int friendId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_FRIEND_RUNNER);
        this.setParam("name", username);
        this.setParam("password", password);
        this.setParam("friendId", friendId);
    }
    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_FRIEND_RUNNER;
    }
}
