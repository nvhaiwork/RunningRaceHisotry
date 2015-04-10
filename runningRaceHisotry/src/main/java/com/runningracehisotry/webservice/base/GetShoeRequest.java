package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 4/10/15.
 */
public class GetShoeRequest extends BaseGetRequest{

    public GetShoeRequest(int shoeId) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_SHOES_BY_ID + String.valueOf(shoeId));
    }
    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_SHOES_BY_ID;
    }
}