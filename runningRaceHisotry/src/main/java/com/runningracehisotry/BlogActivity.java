package com.runningracehisotry;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetAboutUsRequest;
import com.runningracehisotry.webservice.base.GetBlogRequest;

import org.json.JSONObject;


public class BlogActivity extends BaseActivity {

    private CustomLoadingDialog mLoadingDialog;
    private WebView mBlog;

    @Override
    protected int addContent() {
        return R.layout.activity_blog;
    }

    @Override
    protected void initView() {
        super.initView();
        mBlog = (WebView ) findViewById(R.id.menu_blog);
        GetBlogRequest request = new GetBlogRequest();
        request.setListener(callBackEvent);
        new Thread(request).start();
        mLoadingDialog = CustomLoadingDialog.show(BlogActivity.this,"", "", false, false);
    }



    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, Object data) {
            LogUtil.i(Constants.LOG_TAG, data.toString());
            if (methodName.equals(ServiceConstants.METHOD_GET_BLOG)) {
                try {
                    // Login success
                    JSONObject jsonObjectReceive = new JSONObject(data.toString());
                    String mHtmlBlog = jsonObjectReceive.getString("main_content");
                    String mHtmlName = jsonObjectReceive.getString("name");
                    LogUtil.d(Constants.LOG_TAG, " Content blog: " + mHtmlBlog + "|" + mHtmlName);
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";
                    mBlog.loadDataWithBaseURL("",mHtmlName + mHtmlBlog, mimeType, encoding, "");
                } catch (Exception e) {

                    Utilities.showAlertMessage(BlogActivity.this, getResources().getString(R.string.menu_blog_cannot_get_content), "");

                } finally {
                    try{
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                    }
                    catch(Exception ex){
                    }
                }
            }



        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {
            try{
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
            catch(Exception exx){
            }
        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };
}
