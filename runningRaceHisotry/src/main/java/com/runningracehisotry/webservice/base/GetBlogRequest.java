package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by QuyNguyen on 5/1/2015.
 */
public class GetBlogRequest extends BaseGetRequest{

    public GetBlogRequest() {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_GET_BLOG);

    }



    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_GET_BLOG;

    }
}
