package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;


/**
 * Created by manh on 04/09/15.
 */
public class DeleteRaceRequest extends BaseDeleteRequest {

    public DeleteRaceRequest(String id) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_DELETE_RACE_BY_ID + id);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_DELETE_RACE_BY_ID;
    }
}
