/*© TOSHIBA CORPORATION 2015*/
package com.runningracehisotry.webservice.base;

import android.util.Base64;

import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.webservice.ServiceApi;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a request using GET method.
 * @author tiennt7
 *
 */
public abstract class BasePutRequest extends BaseRequest{

	private HttpPut mHttpPut;
	private boolean mIsParamAdded;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

	private final String logTag = "BasePostRequest";

	public BasePutRequest(String url) {
		super(url);
		mHttpPut = new HttpPut();
        if(!ServiceApi.API_LOGIN.equals(getRequestName())
                && !ServiceApi.API_OAUTH_REGISTER.equals(getRequestName())
                && !ServiceApi.API_REGISTER.equals(getRequestName())) {
            String userName = CustomSharedPreferences.getPreferences(Constants.PREF_USERNAME, "");
            String password = CustomSharedPreferences.getPreferences(Constants.PREF_PASSWORD, "");
            String fbID = CustomSharedPreferences.getPreferences(Constants.PREF_FB_ID, "");

            if(RunningRaceApplication.getInstance().isSocialLogin()) {
                String s = String.format("%s:%s", fbID, "123456");
                mHttpPut.setHeader("Authorization", "Basic "+ Base64.encodeToString(s.getBytes(), Base64.NO_WRAP));
            } else if(userName.length() * password.length() > 0) {
                String s = String.format("%s:%s", userName, password);
                mHttpPut.setHeader("Authorization", "Basic "+ Base64.encodeToString(s.getBytes(), Base64.NO_WRAP));
            }

        }
	}

	public void setHeader(String name, String value) {
		mHttpPut.setHeader(name, value);
	}
	
	public void setParam(String name, String value) {
		if (mIsParamAdded) {
			setAddress(getAddress() + String.format("&%s=%s", name, value));
		} else {
			setAddress(getAddress() + String.format("?%s=%s", name, value));
			mIsParamAdded = true;
		}
	}
	
	public void setParam(String name, int value) {
		if (mIsParamAdded) {
			setAddress(getAddress() + String.format("&%s=%d", name, value));
		} else {
			setAddress(getAddress() + String.format("?%s=%d", name, value));
			mIsParamAdded = true;
		}
	}

    public void setPostParam(String name, String value) {
        if (mIsParamAdded) {
            params.add(new BasicNameValuePair(name, value));
        } else {
            params.add(new BasicNameValuePair(name, value));
            mIsParamAdded = true;
        }
    }

	@Override
	public void run() {
		try {
            onStart();
			URL url = new URL(getAddress());
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			
			mHttpPut.setURI(uri);
            mHttpPut.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse httpResponse = getHttpClient().execute(mHttpPut);
			String response = EntityUtils.toString(httpResponse.getEntity());
			
			onCompleted(response);
		} catch (Exception ex) {
			onFailed(ex);
		}  finally {
            onEnded();
        }
	}
}
