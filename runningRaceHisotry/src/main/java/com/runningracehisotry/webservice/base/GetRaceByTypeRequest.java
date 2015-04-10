package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetRaceByTypeRequest extends BaseGetRequest{

    public GetRaceByTypeRequest(String date, int typeId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_RACES_BY_TYPE);
        //this.setParam("sort", "date");
        this.setParam("sort", date);
        this.setParam("type", typeId);

    }

    public GetRaceByTypeRequest(String date, String typeId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_RACES_BY_TYPE);
        //this.setParam("sort", "date");
        this.setParam("sort", date);
        this.setParam("type", typeId);

    }
    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_RACES_BY_TYPE;
    }
}