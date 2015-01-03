/**
 * 
 */
package com.runningracehisotry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

/**
 * @author nvhaiwork
 *
 */
public class LoginChoiceScreen extends BaseActivity {

	private Dialog mLoadingDialog;
	private ImageView mCreateBtn, mLoginFbBtn, mLoginTwitterBtn, mContactUsBtn;

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
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

			mLoadingDialog = CustomLoadingDialog.show(LoginChoiceScreen.this,
					"", "", false, false);
			// new LoginFbAsync().execute();
			List<String> permissions = Arrays.asList("public_profile",
					"user_friends", "email");
			ParseFacebookUtils.logIn(permissions, LoginChoiceScreen.this,
					new LogInCallback() {

						@Override
						public void done(ParseUser user, ParseException arg1) {
							// TODO Auto-generated method stub
							if (user == null) {

								Utilities
										.showAlertMessage(
												LoginChoiceScreen.this,
												getString(R.string.dialog_facebook_login_fail),
												getString(R.string.dialog_sign_in));
								mLoadingDialog.dismiss();
							} else if (user.isNew()) {

								requestUserInfo();
							} else {

								try {

									user.fetch();
									mUser = user;
									if (!mUser.containsKey(Constants.KIND)) {

										mUser.put(Constants.KIND,
												"fb:" + user.getUsername());
									}

									mHistory = user.getList(Constants.DATA);
									mShoes = user.getList(Constants.SHOES);
									mFriends = user.getList(Constants.FRIENDS);
									user.setPassword("kki");
									CustomSharedPreferences.setPreferences(
											Constants.PREF_USERNAME,
											user.getUsername());
									CustomSharedPreferences.setPreferences(
											Constants.PREF_PASSWORD, "kki");
									Intent selectRaceIntent = new Intent(
											LoginChoiceScreen.this,
											SelectRaceActivity.class);
									selectRaceIntent
											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
													| Intent.FLAG_ACTIVITY_NEW_TASK);
									selectRaceIntent
											.putExtra(
													Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
													-1);
									startActivity(selectRaceIntent);
									mLoadingDialog.dismiss();
									finish();
								} catch (ParseException e) {

									mLoadingDialog.dismiss();
								}
							}
						}
					});
			break;
		case R.id.login_with_twitter:

			mLoadingDialog = CustomLoadingDialog.show(LoginChoiceScreen.this,
					"", "", false, false);
			ParseTwitterUtils.logIn(LoginChoiceScreen.this,
					new LogInCallback() {

						@Override
						public void done(ParseUser user, ParseException arg1) {
							// TODO Auto-generated method stub

							if (user == null) {

								Utilities
										.showAlertMessage(
												LoginChoiceScreen.this,
												"Uh oh. The user cancelled the Twitter login.",
												getString(R.string.dialog_sign_in));
								mLoadingDialog.dismiss();
							} else if (user.isNew()) {

								String twitterName = ParseTwitterUtils
										.getTwitter().getScreenName();

								mShoes = new ArrayList<ParseObject>();
								mHistory = new ArrayList<HashMap<String, Object>>();
								mFriends = new ArrayList<ParseUser>();
								user.put(Constants.SHOES, mShoes);
								user.put(Constants.DATA, mHistory);
								user.put(Constants.FRIENDS, mFriends);
								user.put(Constants.FULLNAME, twitterName);
								user.put(Constants.KIND, "tw:" + twitterName);
								user.setPassword("kki");
								user.saveInBackground();
								mUser = user;
								CustomSharedPreferences.setPreferences(
										Constants.PREF_USERNAME,
										user.getUsername());
								CustomSharedPreferences.setPreferences(
										Constants.PREF_PASSWORD, "kki");
								Intent selectRaceIntent = new Intent(
										LoginChoiceScreen.this,
										SelectRaceActivity.class);
								selectRaceIntent
										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
												| Intent.FLAG_ACTIVITY_NEW_TASK);
								selectRaceIntent
										.putExtra(
												Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
												-1);
								startActivity(selectRaceIntent);
								mLoadingDialog.dismiss();
								finish();
							} else {

								try {

									user.fetch();
									mUser = user;
									if (!mUser.containsKey(Constants.KIND)) {

										String twitterName = ParseTwitterUtils
												.getTwitter().getScreenName();
										mUser.put(Constants.KIND, "tw:"
												+ twitterName);
									}

									mHistory = user.getList(Constants.DATA);
									mShoes = user.getList(Constants.SHOES);
									mFriends = user.getList(Constants.FRIENDS);
									user.setPassword("kki");
									CustomSharedPreferences.setPreferences(
											Constants.PREF_USERNAME,
											user.getUsername());
									CustomSharedPreferences.setPreferences(
											Constants.PREF_PASSWORD, "kki");
									Intent selectRaceIntent = new Intent(
											LoginChoiceScreen.this,
											SelectRaceActivity.class);
									selectRaceIntent
											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
													| Intent.FLAG_ACTIVITY_NEW_TASK);
									selectRaceIntent
											.putExtra(
													Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
													-1);
									startActivity(selectRaceIntent);
									mLoadingDialog.dismiss();
									finish();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									mLoadingDialog.dismiss();
								}
							}
						}
					});
			break;
		case R.id.login_contact_us:

			Utilities.contactUs(LoginChoiceScreen.this);
			break;
		}
	}

	private void requestUserInfo() {

		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {
						// TODO Auto-generated method stub

						if (user != null) {

							ParseUser pUser = ParseUser.getCurrentUser();
							mShoes = new ArrayList<ParseObject>();
							mHistory = new ArrayList<HashMap<String, Object>>();
							mFriends = new ArrayList<ParseUser>();
							pUser.setEmail(user.getProperty("email").toString());
							pUser.put(Constants.SHOES, mShoes);
							pUser.put(Constants.DATA, mHistory);
							pUser.put(Constants.FRIENDS, mFriends);
							pUser.put(Constants.FULLNAME, user.getFirstName()
									+ " " + user.getLastName());
							pUser.put(Constants.KIND, "fb:" + user.getId());
							pUser.setPassword("kki");
							pUser.saveInBackground();
							mUser = pUser;

							CustomSharedPreferences.setPreferences(
									Constants.PREF_USERNAME,
									pUser.getUsername());
							CustomSharedPreferences.setPreferences(
									Constants.PREF_PASSWORD, "kki");

							Intent selectRaceIntent = new Intent(
									LoginChoiceScreen.this,
									SelectRaceActivity.class);
							selectRaceIntent
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
											| Intent.FLAG_ACTIVITY_NEW_TASK);
							selectRaceIntent.putExtra(
									Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
									-1);
							startActivity(selectRaceIntent);
							mLoadingDialog.dismiss();
							finish();
						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {

								LogUtil.e("makeMeRequestForUserData",
										"The facebook session was invalidated.");
							} else {

								LogUtil.e("makeMeRequestForUserData",
										"Some other error: "
												+ response.getError()
														.getErrorMessage());
							}

							mLoadingDialog.dismiss();
						}
					}
				});

		request.executeAsync();
	}
}
