package com.runningracehisotry.webservice.base;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

/**
 * Created by manh on 04/09/15.
 */
public class AddRaceRequest extends BasePostRequest {

    public AddRaceRequest(String bibUrl, String city, String eventType, String finisherDateTime,
                          String medalUrl, String raceName, String personUrl, String raceDate,
                          String shoesId, String state, String website) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_ADD_RACE);
        setPostParam("bib_url", bibUrl);
        setPostParam("city", city);
        setPostParam("event_type", eventType);
        setPostParam("finisher_date_time", finisherDateTime);
        setPostParam("medal_url", medalUrl);
        setPostParam("name", raceName);
        setPostParam("person_url", personUrl);
        setPostParam("race_date", raceDate);
        if((shoesId != null) && !shoesId.equalsIgnoreCase("0")) {
            LogUtil.d(Constants.LOG_TAG, "shoeID not 0 set");
            setPostParam("shoes_id", shoesId);
        }
        else{
            LogUtil.d(Constants.LOG_TAG, "shoeID 0 not set");
        }
        setPostParam("state", state);
        setPostParam("website", website);
    }

    public AddRaceRequest(String bibUrl, String city, String eventType, String finisherDateTime,
                          String medalUrl, String raceName, String personUrl, String raceDate,
                          String shoesId, String state, String website, String raceMiles) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_ADD_RACE);
        setPostParam("bib_url", bibUrl);
        setPostParam("city", city);
        setPostParam("event_type", eventType);
        setPostParam("finisher_date_time", finisherDateTime);
        setPostParam("medal_url", medalUrl);
        setPostParam("name", raceName);
        setPostParam("person_url", personUrl);
        setPostParam("race_date", raceDate);
        if((shoesId != null) && (!shoesId.equalsIgnoreCase("0"))) {
            LogUtil.d(Constants.LOG_TAG, "shoeID not 0 set");
            setPostParam("shoes_id", shoesId);
        }
        else{
            LogUtil.d(Constants.LOG_TAG, "shoeID 0 not set");
        }
        setPostParam("state", state);
        setPostParam("website", website);
        if(raceMiles != null){
            LogUtil.d(Constants.LOG_TAG, "set race miles for type 6: " + raceMiles);
            setPostParam("race_miles", raceMiles);
        }
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_ADD_RACE;
    }
}
