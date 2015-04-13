package com.runningracehisotry;

import com.google.gson.Gson;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.User;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetUserProfileRequest;
import com.runningracehisotry.webservice.base.LoginRequest;

import android.content.Intent;
import android.os.Handler;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SplashScreenActivity extends BaseActivity implements IWsdl2CodeEvents {
    private final String logTag = "SplashScreenActivity";
    private String savedUserName;
    private String savedPassword;

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

		if (savedPassword.equals("") | savedUsername.equals("")) {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					Intent loginIntent = new Intent(SplashScreenActivity.this,
							LoginChoiceScreen.class);
					startActivity(loginIntent);
				}
			}, 2000);
		} else {
            LoginRequest request = new LoginRequest(savedUsername, savedPassword);
            request.setListener(this);
            new Thread(request).start();
		}
	}

    @Override
    public void Wsdl2CodeStartedRequest() {

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
                                e.printStackTrace();
                            }
                        }
                    });

                }
                // Login fail
                else {
                    LogUtil.d(logTag, "Login fail!!!");
                    // Show dialog notify login fail
                    Utilities.showAlertMessage(
                            this,
                            getString(R.string.dialog_login_email_fails),
                            "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showAlertMessage(
                        this,
                        getString(R.string.dialog_login_email_fails),
                        "");
            } finally {
                    /*if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }*/
            }
        } else if(methodName.equals(ServiceConstants.METHOD_GET_USER_PROFILE)) {
            handleResponseGetUserInfo(data);

        } else if(methodName.equals(ServiceConstants.METHOD_GET_SHOES_BY_ID)) {
            handleResponseGetShoe(data);

        } else if(methodName.equals(ServiceConstants.METHOD_GET_FRIEND_RUNNER)) {
            handleResponseGetFriend(data);

        } else if(methodName.equals(ServiceConstants.METHOD_GET_RACE_BY_ID)) {
            handleResponseGetRace(data);

        }

    }

    @Override
    public void Wsdl2CodeFinishedWithException(Exception ex) {

    }

    @Override
    public void Wsdl2CodeEndedRequest() {

    }

    private void handleResponseGetUserInfo(Object data) {
        Gson gson = new Gson();
        User user = gson.fromJson(data.toString(), User.class);
        if(user != null) {
            RunningRaceApplication.getInstance().setCurrentUser(user);

            Intent selectRaceIntent = new Intent(this, SelectRaceActivity.class);
            selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
            startActivity(selectRaceIntent);
            finish();
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
}
