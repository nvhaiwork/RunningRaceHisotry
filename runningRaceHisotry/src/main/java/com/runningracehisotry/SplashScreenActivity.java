package com.runningracehisotry;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.User;
import com.runningracehisotry.service.MessageService;
import com.runningracehisotry.service.SinchService;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetUserProfileRequest;
import com.runningracehisotry.webservice.base.LoginRequest;
import com.runningracehisotry.webservice.base.RegisterFacebookRequest;
import com.sinch.android.rtc.SinchError;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class SplashScreenActivity extends BaseActivity implements IWsdl2CodeEvents, SinchService.StartFailedListener {
    private final String logTag = "SplashScreenActivity";
    private String savedUserName;
    private String savedPassword;
    private String savedSNSID;
    private String savedSNSName;
    private String savedSNSAvatar;

    private SinchService.SinchServiceInterface mSinchServiceInterface;


    /*
     * (non-Javadoc)
     *
     * @see com.runningracehisotry.BaseActivity#addContent()
     */
    @Override
    protected int addContent() {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_splash_screen;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.runningracehisotry.BaseActivity#initView()
     */
    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();


        final String savedUsername = CustomSharedPreferences.getPreferences(
                Constants.PREF_USERNAME, "");
        this.savedUserName = savedUsername;
        final String savedPassword = CustomSharedPreferences.getPreferences(
                Constants.PREF_PASSWORD, "");
        this.savedPassword = savedPassword;

        this.savedSNSID = CustomSharedPreferences.getPreferences(
                Constants.PREF_SNS_ID, "");
        this.savedSNSName = CustomSharedPreferences.getPreferences(
                Constants.PREF_SNS_FULL_NAME, "");
        this.savedSNSAvatar = CustomSharedPreferences.getPreferences(
                Constants.PREF_SNS_AVATAR, "");


        if (savedPassword.equals("") | savedUsername.equals("")) {
            if(savedSNSID.length() > 0 && savedSNSName.length() > 0 && savedSNSAvatar.length() > 0) {
                RegisterFacebookRequest request = new RegisterFacebookRequest(savedSNSID, savedSNSName, savedSNSAvatar);

                request.setListener(this);
                new Thread(request).start();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent loginIntent = new Intent(SplashScreenActivity.this,
                                LoginChoiceScreen.class);
                        startActivity(loginIntent);
                    }
                }, 2000);
            }
        } else {
            LoginRequest request = new LoginRequest(savedUsername, savedPassword);
            request.setListener(this);
            new Thread(request).start();
            LogUtil.d(Constants.LOG_TAG, "Login again");
        }
    }


    @Override
    public void Wsdl2CodeStartedRequest() {
        LogUtil.d(Constants.LOG_TAG, "Wsdl2CodeStartedRequest");
    }


    @Override
    public void Wsdl2CodeFinished(String methodName, Object data) {
        if (methodName.equals(ServiceConstants.METHOD_LOGIN)) {
            try {
                JSONObject jsonObjectReceive = new JSONObject(data.toString());
                boolean result = jsonObjectReceive.getBoolean("result");
                // Login success 
                if (result) {
                    LogUtil.d(logTag, "Login success has data!!!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                handleResponseLogin(savedUserName, savedPassword);
                            } catch (Exception e) {
                                //e.printStackTrace(); 
                            }
                        }
                    });


                }
                // Login fail 
                else {
                    LogUtil.d(logTag, "Login fail!!!");
                    // Show dialog notify login fail 
//                    Utilities.showAlertMessage(
//                            this,
//                            getString(R.string.dialog_login_email_fails),
//                            "");
                    CustomSharedPreferences.setPreferences(
                            Constants.PREF_USERNAME, "");
                    CustomSharedPreferences.setPreferences(
                            Constants.PREF_PASSWORD, "");
                    Intent loginIntent = new Intent(SplashScreenActivity.this,
                            LoginChoiceScreen.class);
                    startActivity(loginIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showAlertMessage(
                        this,
                        getString(R.string.dialog_login_email_fails),
                        "");
            } finally {
            }
        } else if(methodName.equals(ServiceConstants.METHOD_GET_USER_PROFILE)) {
            handleResponseGetUserInfo(data);


        } else if(methodName.equals(ServiceConstants.METHOD_GET_SHOES_BY_ID)) {
            handleResponseGetShoe(data);


        } else if(methodName.equals(ServiceConstants.METHOD_GET_FRIEND_RUNNER)) {
            handleResponseGetFriend(data);


        } else if(methodName.equals(ServiceConstants.METHOD_GET_RACE_BY_ID)) {
            handleResponseGetRace(data);


        } else if(methodName.equals(ServiceConstants.METHOD_OAUTH_REGISTER)) {
            handleResponseRegister(data);
        }


    }


    @Override
    public void Wsdl2CodeFinishedWithException(Exception ex) {


    }


    @Override
    public void Wsdl2CodeEndedRequest() {


    }

    private void handleResponseRegister(Object data) {
        CustomSharedPreferences.setPreferences(Constants.PREF_FB_ID, savedSNSID);
        RunningRaceApplication.getInstance().setSocialLogin(true);
        getCurrentUserData();
    }


    private void handleResponseGetUserInfo(Object data) {
        Gson gson = new Gson();
        User user = gson.fromJson(data.toString(), User.class);
        if(user != null) {
            RunningRaceApplication.getInstance().setCurrentUser(user);
            CustomSharedPreferences.setPreferences(Constants.PREF_USER_ID, user.getId());
            CustomSharedPreferences.setPreferences(Constants.PREF_USER_LOGGED_OBJECT, data.toString());
            LogUtil.d(logTag, "Logged use data not null");

//            Intent selectRaceIntent = new Intent(SplashScreenActivity.this, SelectRaceActivity.class);
//            selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
//            startActivity(selectRaceIntent);
//            finish();

            callService();
        }
        else{
            LogUtil.d(logTag, "Logged use data null");
        }
    }


    private void handleResponseGetShoe(Object data) {


    }


    private void handleResponseGetFriend(Object data) {


    }


    private void handleResponseGetRace(Object data) {


    }




    /**
     * Finish user login/sign-up process 
     * */
    private void handleResponseLogin(String username, String password) {
        CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME, username);
        CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD, password);
        getCurrentUserData();
        //mLoadingDialog.dismiss(); 


    }


    private void getCurrentUserData() {
        GetUserProfileRequest request = new GetUserProfileRequest();
        request.setListener(this);
        new Thread(request).start();


    }

    private void callService() {
        bindService(new Intent(getApplicationContext(), SinchService.class), connection,
                BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (SinchService.class.getName().equals(componentName.getClassName())) {
                mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
                mSinchServiceInterface.setStartListener(SplashScreenActivity.this);
//                mSinchServiceInterface.startClient(RunningRaceApplication.getInstance().getCurrentUser().getName());
                registerInBackground();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("a", name.toShortString());
        }
    };

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                String regid = "";
                regid = CustomSharedPreferences.getPreferences(Constants.PREF_GCM_DEVICE_ID, "");
                if(regid.length() == 0) {
                    try {
                        String androidID = "984219596580";

                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(RunningRaceApplication.getInstance());
                        regid = gcm.register(androidID);
                        msg = "Device registered, registration ID=" + regid;


                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String regid) {
                if(regid.length() > 0) {
                    CustomSharedPreferences.setPreferences(Constants.PREF_GCM_DEVICE_ID, regid);
                }

                mSinchServiceInterface.startClient(RunningRaceApplication.getInstance().getCurrentUser().getName());
            }
        }.execute(null, null, null);
    }

    @Override
    public void onStartFailed(SinchError error) {
        LogUtil.d(logTag, "onStartFailed : " + error.getMessage());
    }

    @Override
    public void onStarted() {
        Intent selectRaceIntent = new Intent(SplashScreenActivity.this, SelectRaceActivity.class);
        selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
        startActivity(selectRaceIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            unbindService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}