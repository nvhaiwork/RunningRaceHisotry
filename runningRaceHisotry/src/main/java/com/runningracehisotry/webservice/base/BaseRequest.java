/*© TOSHIBA CORPORATION 2015*/
package com.runningracehisotry.webservice.base;

import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.client.methods.HttpRequestBase;

abstract class BaseRequest implements Runnable, Comparable<BaseRequest> {
	private AndroidHttpClient mHttpClient;
	private String mAddress;
	private Handler mHandler;
	private IWsdl2CodeEvents mListener;

	public BaseRequest(String url) {
		this.mAddress = url;
		mHandler = new Handler(Looper.getMainLooper());
	}

	public void setListener(IWsdl2CodeEvents listener) {
		mListener = listener;
	}

	/**
	 * Called when request completed, on background thread. Class which extends
	 * from this class have to override this method,
	 * <p>
	 * do its stuff like parsing data, after that, child class must call this
	 * super onComplete to deliver onComplete to main thread (listener)
	 * 
	 * @param response
	 *            Response from server, in text format.
	 */
	protected void onCompleted(final String response) {
        LogUtil.d("BaseRequest", "API response: " + response);
		
		closeClient();
		if (mListener != null) {
			if(response != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.Wsdl2CodeFinished(getRequestName(), response);
                    }
                });

                LogUtil.d("BaseRequest", "call complete");
			}
		}
	}
	

	/**
	 * Please see {@link com.runningracehisotry.webservice.base.BaseRequest#onCompleted}
	 * 
	 * @param e
	 *            Message to deliver to listener (running on main thread).
	 */
	protected void onFailed(final Exception e) {
		closeClient();
		if (mListener != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mListener.Wsdl2CodeFinishedWithException(e);
				}
			});
		}
	}

    protected void onStart(){
        mListener.Wsdl2CodeStartedRequest();
    }

    protected void onEnded(){
        mListener.Wsdl2CodeEndedRequest();
    }
	
	@Override
	public int compareTo(BaseRequest another) {
		return 0;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	public AndroidHttpClient getHttpClient() {
		try{
			this.mHttpClient = AndroidHttpClient.newInstance("");
		} catch (Exception e) {
			this.mHttpClient.close();
		}
		return mHttpClient;
	}

	public void setHttpClient(AndroidHttpClient mHttpClient) {
		this.mHttpClient = mHttpClient;
	}
	
	private void closeClient() {
		if(this.mHttpClient != null) {
			this.mHttpClient.close();
		}
	}

    protected abstract String getRequestName();
}
