package com.runningracehisotry;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.model.GraphUser;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.User;
import com.runningracehisotry.service.MessageService;
import com.runningracehisotry.service.SinchService;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetUserProfileRequest;
import com.runningracehisotry.webservice.base.LoginRequest;
import com.runningracehisotry.webservice.base.RegisterFacebookRequest;
import com.sinch.android.rtc.SinchError;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author nvhaiwork
 *
 */
public class LoginChoiceScreen extends BaseActivity implements IWsdl2CodeEvents, SinchService.StartFailedListener {

    private Dialog mLoadingDialog;
    private ImageView mCreateBtn, mLoginFbBtn, mLoginTwitterBtn, mContactUsBtn;
    private final String logTag = "LoginChoiceScreen";
    private Twitter twitter;
    private RequestToken requestToken;
    private String fbID, fullName, email, avatar;

    static String TWITTER_CONSUMER_KEY = "CMt7BmVQ5cn8AzV1lfHqziwE5"; // place your cosumer key here
    static String TWITTER_CONSUMER_SECRET = "Rf4zRtxZbkeUErnr7nO5YEC0TQO3Lo8Cle1QEDmrSYfOj1mY3H"; // place your consumer secret here

    // Preference Constants
    /* Shared preference keys */
    public static final String PREF_NAME = "sample_twitter_pref";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    public static final String PREF_USER_NAME = "twitter_user_name";

    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 4367;

    // Twitter oauth urls

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    public LoginChoiceScreen() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.runningracehisotry.BaseActivity#addContent()
     */
    @Override
    protected int addContent() {
        // TODO Auto-generated method stub
        return R.layout.activity_login_choice;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.runningracehisotry.BaseActivity#initView()
     */
    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        printHashKey();


        super.initView();

        isHideActionBarButtons = true;
        mLoginFbBtn = (ImageView) findViewById(R.id.login_with_fb);
        mContactUsBtn = (ImageView) findViewById(R.id.login_contact_us);
        mCreateBtn = (ImageView) findViewById(R.id.login_create_account);
        mLoginTwitterBtn = (ImageView) findViewById(R.id.login_with_twitter);

        mCreateBtn.setOnClickListener(this);
        mLoginFbBtn.setOnClickListener(this);
        mContactUsBtn.setOnClickListener(this);
        mLoginTwitterBtn.setOnClickListener(this);
    }

    public void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.runningracehisotry",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("TEMPTAGHASH KEY:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WEBVIEW_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                String verifier = data.getExtras().getString(oAuthVerifier);

                new GetTokenTask().execute(verifier);
            } else {
                if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
//            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.runningracehisotry.BaseActivity#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_create_account:

                Intent signInIntent = new Intent(LoginChoiceScreen.this,
                        SignInActivity.class);
                startActivity(signInIntent);
                break;
            case R.id.login_with_fb:

                performFacebookLogin();
                break;
            case R.id.login_with_twitter:

                mLoadingDialog = CustomLoadingDialog.show(LoginChoiceScreen.this,
                        "", "", false, false);

                loginToTwitter();

                break;
            case R.id.login_contact_us:

                Utilities.contactUs(LoginChoiceScreen.this);
                break;
        }
    }


    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        twitter4j.User user;
        try {
            user = twitter.showUser(userID);

            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            CustomSharedPreferences.setPreferences(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            CustomSharedPreferences.setPreferences(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            CustomSharedPreferences.setPreferences(PREF_KEY_TWITTER_LOGIN, true);
            CustomSharedPreferences.setPreferences(PREF_USER_NAME, username);

        } catch (TwitterException e1) {
            //e1.printStackTrace();
        }
    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);

        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);

        final Configuration configuration = builder.build();
        final TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();
    }

    private void loginToTwitter() {
        boolean isLoggedIn = CustomSharedPreferences.getPreferences(PREF_KEY_TWITTER_LOGIN, false);
//        isLoggedIn = false;

        if (!isLoggedIn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        initTwitterConfigs();

                        requestToken = twitter.getOAuthRequestToken(callbackUrl);

                        /**
                         *  Loading twitter login page on webview for authorization
                         *  Once authorized, results are received at onActivityResult
                         *  */
                        final Intent intent = new Intent(LoginChoiceScreen.this, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                        startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } else {

            String token = CustomSharedPreferences.getPreferences(PREF_KEY_OAUTH_TOKEN, "");
            String secret = CustomSharedPreferences.getPreferences(PREF_KEY_OAUTH_SECRET, "");

            new GetTokenTask().execute(new String[]{token, secret});
        }
    }




    private void performFacebookLogin() {
        LogUtil.d("FACEBOOK", "performFacebookLogin");
//            final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("email"));
//            Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//                @Override
//                public void call(Session session, SessionState state, Exception exception) {
//                    Log.d("FACEBOOK", "call");
//                    if (session.isOpened()) {
//                        Log.d("FACEBOOK", "if (session.isOpened() && !isFetching)");
////                        session.requestNewReadPermissions(newPermissionsRequest);
//                        Request getMe = Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//                            @Override
//                            public void onCompleted(GraphUser user, Response response) {
//                                mLoadingDialog.dismiss();
//                                Log.d("FACEBOOK", "onCompleted");
//                                if (user != null) {
//                                    Log.d("FACEBOOK", "user != null");
//                                    String id = user.getId();
//                                    email = user.getProperty("email").toString();
//
//                                    if (email == null || email.length() < 0) {
//                                        Toast.makeText(LoginChoiceScreen.this, "login fail", Toast.LENGTH_LONG);
//                                        return;
//                                    } else {
//                                        fbID = id;
//                                        fullName = user.getName();
//                                        avatar = "http://graph.facebook.com/"+ id+ "/picture?type=large";
//
//                                        RegisterFacebookRequest request = new RegisterFacebookRequest(id, fullName, avatar);
//                                        request.setListener(LoginChoiceScreen.this);
//                                        new Thread(request).start();
//                                    }
//                                }
//                            }
//
//
//                        });
//                        getMe.executeAsync();
//                    } else {
//                        if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
//                            mLoadingDialog.dismiss();
//                        }
////                        Utilities.showAlertMessage(
////                                LoginChoiceScreen.this,getString(R.string.login_disconnect),"");
//                    }
//                }
//            });

        LoginManager loginManager = LoginManager.getInstance();

        loginManager.logInWithReadPermissions(this, Arrays.asList(new String[]{"public_profile", "email"}));

        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        LogUtil.d(logTag, loginResult.toString());
                        fetchUserInfo();
                        mLoadingDialog = CustomLoadingDialog.show(LoginChoiceScreen.this,
                                "", "", false, false);
                    }

                    @Override
                    public void onCancel() {
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        Utilities.showAlertMessage(LoginChoiceScreen.this,getString(R.string.login_disconnect),"");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        LogUtil.d(logTag, exception.toString());
                        Utilities.showAlertMessage(LoginChoiceScreen.this,getString(R.string.login_disconnect),"");
                    }

                });
    }

    private void fetchUserInfo() {
        final com.facebook.AccessToken accessToken = com.facebook.AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            Log.d(logTag, response.toString());

                            try {
                                String id = me.getString("id");
                                email = me.getString("email");

                                if (email == null || email.length() < 0) {
                                    Toast.makeText(LoginChoiceScreen.this, "login fail", Toast.LENGTH_LONG);
                                    return;
                                } else {
                                    fbID = id;
                                    fullName = me.getString("name");
                                    avatar = "http://graph.facebook.com/" + id + "/picture?type=large";

                                    RegisterFacebookRequest request = new RegisterFacebookRequest(id, fullName, avatar);

                                    request.setListener(LoginChoiceScreen.this);
                                    new Thread(request).start();
                                }
                            } catch (JSONException e) {
                                //e.printStackTrace();
                                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                        }
                    }

            );
            GraphRequest.executeBatchAsync(request);
        } else {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }


    @Override
    public void Wsdl2CodeStartedRequest() {

    }

    @Override
    public void Wsdl2CodeFinished(String methodName, Object data) {
        if (methodName.equals(ServiceConstants.METHOD_LOGIN)) {
            handlerResponseLogin(data);

        }  else if(methodName.equals(ServiceConstants.METHOD_GET_USER_PROFILE)) {
            handleResponseGetUserInfo(data);

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

    private void getCurrentUserData() {
        GetUserProfileRequest request = new GetUserProfileRequest();
        RunningRaceApplication.getInstance().setSocialLogin(true);
        request.setListener(this);
        new Thread(request).start();
    }

    private void handleResponseGetUserInfo(Object data) {
        if(RunningRaceApplication.getInstance().isSocialLogin()){
            LogUtil.e(Constants.LOG_TAG,"SNS login result: " + data.toString());
        }
        Gson gson = new Gson();
        User user = gson.fromJson(data.toString(), User.class);
        if(user != null) {
            RunningRaceApplication.getInstance().setCurrentUser(user);
            CustomSharedPreferences.setPreferences(Constants.PREF_USER_ID, user.getId());

            if(RunningRaceApplication.getInstance().isSocialLogin()){
                LogUtil.e(Constants.LOG_TAG,"SNS login result userID: " + user.getId()
                        + "|" + CustomSharedPreferences.getPreferences(Constants.PREF_FB_ID, ""));
                CustomSharedPreferences.setPreferences(
                        Constants.PREF_SNS_ID, fbID);
                CustomSharedPreferences.setPreferences(
                        Constants.PREF_SNS_FULL_NAME, fullName);
                CustomSharedPreferences.setPreferences(
                        Constants.PREF_SNS_AVATAR, avatar);
            }
            CustomSharedPreferences.setPreferences(Constants.PREF_USER_LOGGED_OBJECT, data.toString());

            mLoadingDialog.dismiss();

            callService();

        }
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
                mSinchServiceInterface.setStartListener(LoginChoiceScreen.this);
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
        Intent selectRaceIntent = new Intent(this, SelectRaceActivity.class);
        selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);

        startActivity(selectRaceIntent);
        finish();
    }

    private void handleResponseRegister(Object data) {
        CustomSharedPreferences.setPreferences(Constants.PREF_FB_ID, fbID);
        RunningRaceApplication.getInstance().setSocialLogin(true);
        getCurrentUserData();

//        User newUser = new User();
//        newUser.setId(fbID);
//        newUser.setEmail(email);
//        newUser.setFull_name(fullName);
//        newUser.setName(fullName);
//        newUser.setProfile_image(avatar);
//        newUser.setType("fb:" + fbID);
//
//        RunningRaceApplication.getInstance().setCurrentUser(newUser);
//
//        Gson gson  = new Gson();
//        String serialUSer = gson.toJson(newUser);
//
//        CustomSharedPreferences.setPreferences(Constants.PREF_USER_LOGGED_OBJECT, serialUSer);
//        //CustomSharedPreferences.setPreferences(Constants.PREF_USER_ID, fbID);
//
//
//        Intent selectRaceIntent = new Intent(this, SelectRaceActivity.class);
//        selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
//
//        mLoadingDialog.dismiss();
//
//        RunningRaceApplication.getInstance().setSocialLogin(true);
//        getCurrentUserData();
        /*startActivity(selectRaceIntent);
        finish();*/



    }

    private void handlerResponseLogin(Object data) {
        try {
            JSONObject jsonObjectReceive = new JSONObject(data.toString());
            boolean result = jsonObjectReceive.getBoolean("result");
            // Login success
            if (result) {
                LogUtil.d(logTag, "Login success has data!!!");
                CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME, fbID);
                CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD, "kki");
                getCurrentUserData();
            }
            // Login fail
            else {
                LogUtil.d(logTag, "Login fail!!!");
                // Show dialog notify login fail
//                performFacebookLogin();
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            Utilities.showAlertMessage(
                    this,
                    getString(R.string.dialog_login_email_fails),
                    "");
        } finally {
                    /*if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }*/
        }
    }


    private class GetTokenTask extends AsyncTask <String, Void, AccessToken> {
        private boolean isSuccess;

        @Override
        protected AccessToken doInBackground(String... params) {

            AccessToken accessToken = null;
            try {

                if(params.length == 1) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                } else if(params.length == 2) {
                    accessToken = new AccessToken(params[0], params[1]);
                    final ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setDebugEnabled(true)
                            .setOAuthConsumerKey(consumerKey)
                            .setOAuthConsumerSecret(consumerSecret)
                            .setOAuthAccessToken(params[0])
                            .setOAuthAccessTokenSecret(params[1]);
                    builder.setOAuthConsumerKey(consumerKey);
                    builder.setOAuthConsumerSecret(consumerSecret);

                    final Configuration configuration = builder.build();
                    final TwitterFactory factory = new TwitterFactory(configuration);
                    twitter = factory.getInstance();
                }

                long userID = accessToken.getUserId();
                final twitter4j.User user;
                try {
                    user = twitter.showUser(userID);
                    fullName = user.getName();
                    avatar = user.getProfileImageURL();
                    fbID = String.valueOf(userID);

                    saveTwitterInfo(accessToken);
                    isSuccess = true;
                } catch (TwitterException e) {
                    isSuccess = false;
                    //e.printStackTrace();
                }
            } catch (TwitterException e) {
                isSuccess = false;
                //e.printStackTrace();
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            super.onPostExecute(accessToken);

            if(isSuccess) {
                RegisterFacebookRequest request = new RegisterFacebookRequest(fbID, fullName, avatar);
                request.setListener(LoginChoiceScreen.this);
                new Thread(request).start();
            } else {
                if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Utilities.showAlertMessage(
                        LoginChoiceScreen.this,getString(R.string.login_disconnect),"");
            }
        }
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