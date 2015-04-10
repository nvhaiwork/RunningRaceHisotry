package com.runningracehisotry.webservice.base;

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
        setPostParam("race_name", raceName);
        setPostParam("person_url", personUrl);
        setPostParam("race_date", raceDate);
        setPostParam("shoes_id", shoesId);
        setPostParam("state", state);
        setPostParam("website", website);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_ADD_RACE;
    }
}
