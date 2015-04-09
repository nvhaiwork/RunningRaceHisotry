/*© TOSHIBA CORPORATION 2015*/
package com.runningracehisotry.webservice.base;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.util.Base64;
import android.util.Log;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.webservice.ServiceApi;

/**
 * Base class for a request using GET method.
 * @author tiennt7
 *
 */
public abstract class BaseGetRequest extends BaseRequest{

	private HttpGet mHttpGet;
	private boolean mIsParamAdded;
	private final String logTag = "BaseGetRequest";
	
	public BaseGetRequest(String url) {
		super(url);
        mHttpGet = new HttpGet();
        if(!ServiceApi.API_LOGIN.equals(getRequestName())) {
            String userName = CustomSharedPreferences.getPreferences(Constants.PREF_USERNAME, "");
            String password = CustomSharedPreferences.getPreferences(Constants.PREF_PASSWORD, "");
            if(userName.length() * password.length() > 0) {
                String s = String.format("%s:%s", userName, password);
                mHttpGet.setHeader("Authorization", "Basic "+ Base64.encodeToString(s.getBytes(), Base64.NO_WRAP));
            }

        }
	}

	public void setHeader(String name, String value) {
		mHttpGet.setHeader(name, value);
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
	
	@Override
	public void run() {
		try {
//            onStart();
			mHttpGet.setURI(new URI(getAddress()));
			
			HttpResponse httpResponse = getHttpClient().execute(mHttpGet);
			String response = EntityUtils.toString(httpResponse.getEntity());
			
			onCompleted(response);
		} catch (Exception ex) {
			onFailed(ex);
			Log.e(logTag, ex.getMessage());
		} finally {
            onEnded();
        }
    }
}
