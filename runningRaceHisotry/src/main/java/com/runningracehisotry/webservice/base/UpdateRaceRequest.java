package com.runningracehisotry.webservice.base;

import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;


/**
 * Created by manh on 04/09/15.
 */
public class UpdateRaceRequest extends BasePutRequest {

    public UpdateRaceRequest(String bibUrl, String city, String createAt, String eventType,
                             String finisherDateTime, String raceId, String medalUrl, String raceName,
                             String personUrl, String raceDate, String shoesId, String state,
                             String updateAt, String userId, String website) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_UPDATE_RACE);
        setPostParam("bib_url", bibUrl);
        setPostParam("city", city);
        setPostParam("create_at", createAt);
        setPostParam("event_type", eventType);
        setPostParam("finisher_date_time", finisherDateTime);
        setPostParam("id", raceId);
        setPostParam("medal_url", medalUrl);
        setPostParam("race_name", raceName);
        setPostParam("person_url", personUrl);
        setPostParam("race_date", raceDate);
        setPostParam("shoes_id", shoesId);
        setPostParam("state", state);
        setPostParam("update_at", updateAt);
        setPostParam("user_id", userId);
        setPostParam("website", website);
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_UPDATE_RACE;
    }
}
