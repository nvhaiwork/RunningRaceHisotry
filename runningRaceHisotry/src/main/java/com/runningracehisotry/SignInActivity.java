/**
 * 
 */
package com.runningracehisotry;

import java.util.ArrayList;
import java.util.HashMap;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;

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

/**
 * @author nvhaiwork
 *
 */
public class SignInActivity extends BaseActivity {

	private TextView mRegisBtn, mForgotBtn;
	private CustomLoadingDialog mLoadingDialog;
	private LinearLayout mRegisLayout, mOptionLayout;
	private EditText mUsernameEdt, mPasswordEdt, mConfirmEdt, mEmailEdt;

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

		final String usernameStr = mUsernameEdt.getText().toString();
		final String passwordStr = mPasswordEdt.getText().toString();
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

					Utilities
							.showAlertMessage(
									SignInActivity.this,
									getString(R.string.dialog_confirm_password_does_not_match),
									getString(R.string.dialog_sign_up));
				}

			}
		} else {

			if (usernameStr.equals("") || passwordStr.equals("")) {

				Utilities.showAlertMessage(SignInActivity.this,
						getString(R.string.dialog_fill_information),
						getString(R.string.dialog_sign_in));
			} else {

				mLoadingDialog = CustomLoadingDialog.show(SignInActivity.this,
						"", "", false, false);
				ParseUser.logInInBackground(usernameStr, passwordStr,
						new LogInCallback() {

							@Override
							public void done(final ParseUser user,
									ParseException error) {
								// TODO Auto-generated method stub

								if (user != null) {

									user.fetchInBackground(new GetCallback<ParseObject>() {

										@Override
										public void done(ParseObject object,
												ParseException ex) {
											// TODO Auto-generated method stub

											mUser = user;
											mHistory = object
													.getList(Constants.DATA);
											mShoes = object
													.getList(Constants.SHOES);
											mFriends = object
													.getList(Constants.FRIENDS);
											finishLoginOrSignup(usernameStr,
													passwordStr);
										}
									});
								} else {

									Utilities
											.showAlertMessage(
													SignInActivity.this,
													getString(R.string.dialog_login_email_fails),
													"");
									mLoadingDialog.dismiss();
								}
							}
						});

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
	 * Finsih user login/signup process
	 * */
	private void finishLoginOrSignup(String username, String password) {

		CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME,
				username);
		CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD,
				password);
		Intent selectRaceIntent = new Intent(SignInActivity.this,
				SelectRaceActivity.class);
		selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		selectRaceIntent
				.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
		mLoadingDialog.dismiss();
		startActivity(selectRaceIntent);
		finish();
	}
}
