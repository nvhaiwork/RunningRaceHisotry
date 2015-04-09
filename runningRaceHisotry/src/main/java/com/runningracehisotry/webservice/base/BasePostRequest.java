/*© TOSHIBA CORPORATION 2015*/
package com.runningracehisotry.webservice.base;

import android.util.Base64;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Base class for a request using GET method.
 * @author tiennt7
 *
 */
public abstract class BasePostRequest extends BaseRequest{

	private HttpPost mHttpPost;
	private boolean mIsParamAdded;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	private final String logTag = "BasePostRequest";
	
	public BasePostRequest(String url) {
		super(url);
		mHttpPost = new HttpPost();
        if(!RequestUtil.lOGIN_REQUEST.equals(getRequestName())) {
            mHttpPost.setHeader("Authorization", "Basic "+ Base64.encodeToString("nhanthevu:123456".getBytes(), Base64.NO_WRAP));
        }
	}

	public void setHeader(String name, String value) {
		mHttpPost.setHeader(name, value);
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
            params.add(new BasicNameValuePair("name", value));
        } else {
            params.add(new BasicNameValuePair("name", value));
            mIsParamAdded = true;
        }
    }

	@Override
	public void run() {
		try {
            onStart();
			URL url = new URL(getAddress());
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			
			mHttpPost.setURI(uri);
            mHttpPost.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse httpResponse = getHttpClient().execute(mHttpPost);
			String response = EntityUtils.toString(httpResponse.getEntity());
			
			onCompleted(response);
		} catch (Exception ex) {
			onFailed(ex);
		}  finally {
            onEnded();
        }
	}
}
