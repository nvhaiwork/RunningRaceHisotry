/**
 * 
 */
package com.runningracehisotry;

import java.util.ArrayList;
import java.util.HashMap;

/*import com.parse.GetCallback;
import com.parse.LogInCallback;*/
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author nvhaiwork
 *
 */
public class SignInActivity extends BaseActivity {

	private TextView mRegisBtn, mForgotBtn;
	private CustomLoadingDialog mLoadingDialog;
	private LinearLayout mRegisLayout, mOptionLayout;
	private EditText mUsernameEdt, mPasswordEdt, mConfirmEdt, mEmailEdt;
    private String logTag = "SignInActivity";
    private ServiceApi sv;
    private String usernameStr;
    private String passwordStr;

    /*
     * (non-Javadoc)
     *
     * @see com.runningracehisotry.BaseActivity#addContent()
     */
	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_signin;
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

		isHideActionBarButtons = true;
		mRegisBtn = (TextView) findViewById(R.id.sign_in_regis_btn);
		mEmailEdt = (EditText) findViewById(R.id.sign_in_email_edt);
		mForgotBtn = (TextView) findViewById(R.id.sign_in_forgot_btn);
		mPasswordEdt = (EditText) findViewById(R.id.sign_in_password_edt);
		mUsernameEdt = (EditText) findViewById(R.id.sign_in_username_edt);
		mOptionLayout = (LinearLayout) findViewById(R.id.sign_in_option_layout);
		mConfirmEdt = (EditText) findViewById(R.id.sign_in_confirm_password_edt);
		mRegisLayout = (LinearLayout) findViewById(R.id.sign_in_register_layout);

		mBottomBtnLayout.setBackgroundColor(getResources().getColor(
				R.color.text_button_bg_sign_in));
		mBotLeftBtnTxt.setVisibility(View.VISIBLE);
		mBotRightBtnTxt.setVisibility(View.VISIBLE);
		mRegisBtn.setOnClickListener(this);
		mForgotBtn.setOnClickListener(this);

        sv = new ServiceApi(callBackEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.runningracehisotry.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.bottom_button_right_text:

			doLoginOrSignup();
			return;
		case R.id.sign_in_regis_btn:

			mOptionLayout.setVisibility(View.GONE);
			mRegisLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.sign_in_forgot_btn:

			showForgotPasswordDialog();
			break;
		}

		super.onClick(v);
	}

	/**
	 * Do user login or register
	 * */
	private void doLoginOrSignup() {

		usernameStr = mUsernameEdt.getText().toString();
		passwordStr = mPasswordEdt.getText().toString();
        // Register
		if (mRegisLayout.getVisibility() == View.VISIBLE) {
			String passwordConfirmStr = mConfirmEdt.getText().toString();
			String emailStr = mEmailEdt.getText().toString();
			if (usernameStr.equals("") || passwordStr.equals("")
					|| passwordConfirmStr.equals("") || emailStr.equals("")) {
				Utilities.showAlertMessage(SignInActivity.this,
						getString(R.string.dialog_fill_information),
						getString(R.string.dialog_sign_up));
			} else {
				if (passwordConfirmStr.equals(passwordStr)) {
					mLoadingDialog = CustomLoadingDialog.show(
							SignInActivity.this, "", "", false, false);
                    // Call api Register
					final ParseUser user = new ParseUser();
					user.setUsername(usernameStr);
					user.setPassword(passwordStr);
					user.setEmail(emailStr);
					user.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(ParseException error) {
							// TODO Auto-generated method stub
							if (error == null) {

								mShoes = new ArrayList<ParseObject>();
								mHistory = new ArrayList<HashMap<String, Object>>();
								mFriends = new ArrayList<ParseUser>();
								user.put(Constants.SHOES, mShoes);
								user.put(Constants.DATA, mHistory);
								user.put(Constants.FRIENDS, mFriends);
								user.put(Constants.FULLNAME, usernameStr);
								user.saveInBackground();
								mUser = user;
								finishLoginOrSignup(usernameStr, passwordStr);
							} else {

								Utilities.showAlertMessage(SignInActivity.this,
										error.getMessage(),
										getString(R.string.dialog_sign_up));
								mLoadingDialog.dismiss();
							}
						}
					});
				} else {
					Utilities.showAlertMessage(
                            SignInActivity.this,
                            getString(R.string.dialog_confirm_password_does_not_match),
                            getString(R.string.dialog_sign_up));
				}
			}
		}
        // Login
        else {
			if (usernameStr.equals("") || passwordStr.equals("")) {
				Utilities.showAlertMessage(SignInActivity.this,
						getString(R.string.dialog_fill_information),
						getString(R.string.dialog_sign_in));
			} else {
                try {
                    sv.Login(usernameStr, passwordStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
			}
		}
	}

	/**
	 * Show dialog allows user input email address to reset password
	 * */
	private void showForgotPasswordDialog() {

		final Dialog dialog = new Dialog(SignInActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_forgot_password);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);

		final EditText emailEdt = (EditText) dialog
				.findViewById(R.id.alert_reset_email);
		TextView resetBtn = (TextView) dialog
				.findViewById(R.id.alert_forgot_password_reset_btn);
		resetBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub

				if (!emailEdt.getText().toString().equals("")) {

					final Dialog loadingDialog = CustomLoadingDialog.show(
							SignInActivity.this, "", "", false, false);
					ParseUser.requestPasswordResetInBackground(emailEdt
							.getText().toString(),
							new RequestPasswordResetCallback() {

								@Override
								public void done(ParseException error) {
									// TODO Auto-generated method stub

									loadingDialog.dismiss();
									Utilities
											.showAlertMessage(
													SignInActivity.this,
													getString(R.string.dialog_forgot_pass_message),
													getString(R.string.dialog_forgot_pass_title));
									dialog.dismiss();
								}
							});
				}
			}
		});

		dialog.show();
	}

	/**
	 * Finish user login/sign-up process
	 * */
	private void finishLoginOrSignup(String username, String password) {
		CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME, username);
		CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD, password);
		Intent selectRaceIntent = new Intent(SignInActivity.this, SelectRaceActivity.class);
		selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
		mLoadingDialog.dismiss();
		startActivity(selectRaceIntent);
		finish();
	}

    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
            mLoadingDialog = CustomLoadingDialog.show(SignInActivity.this,"", "", false, false);
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, Object Data) {
            LogUtil.i(logTag, Data.toString());
            if (methodName.equals(ServiceConstants.METHOD_LOGIN)) {
                try {
                    JSONObject jsonObjectReceive = new JSONObject(Data.toString());
                    boolean result = jsonObjectReceive.getBoolean("result");
                    // Login success
                    if (result) {
                        LogUtil.d(logTag, "Login success has data!!!");
                        finishLoginOrSignup(usernameStr, passwordStr);
                        // Get data of login user
                        /*mUser = user;
                        mHistory = object.getList(Constants.DATA);
                        mShoes = object.getList(Constants.SHOES);
                        mFriends = object.getList(Constants.FRIENDS);*/
                    }
                    // Login fail
                    else {
                        LogUtil.d(logTag, "Login fail!!!");
                        // Show dialog notify login fail
                        Utilities.showAlertMessage(
                                SignInActivity.this,
                                getString(R.string.dialog_login_email_fails),
                                "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utilities.showAlertMessage(
                            SignInActivity.this,
                            getString(R.string.dialog_login_email_fails),
                            "");
                } finally {
                    if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                }
            }
        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };
}
