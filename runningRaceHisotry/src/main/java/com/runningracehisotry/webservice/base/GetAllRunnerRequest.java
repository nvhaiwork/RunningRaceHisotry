package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetAllRunnerRequest extends BaseGetRequest{

    public GetAllRunnerRequest() {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_ALL_RUNNERS);
    }
    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_ALL_RUNNERS;
    }
}
