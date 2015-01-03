package com.runningracehisotry;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;

import android.content.Intent;
import android.os.Handler;
import android.view.Window;

public class SplashScreenActivity extends BaseActivity {

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
		final String savedPassword = CustomSharedPreferences.getPreferences(
				Constants.PREF_PASSWORD, "");
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
			ParseUser.logInInBackground(savedUsername, savedPassword,
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

										Intent selectRaceIntent = new Intent(
												SplashScreenActivity.this,
												SelectRaceActivity.class);
										selectRaceIntent
												.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
														| Intent.FLAG_ACTIVITY_NEW_TASK);
										selectRaceIntent
												.putExtra(
														Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
														-1);
										startActivity(selectRaceIntent);
										finish();
									}
								});
							} else {

								new Handler().postDelayed(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub

										CustomSharedPreferences.setPreferences(
												Constants.PREF_USERNAME, "");
										CustomSharedPreferences.setPreferences(
												Constants.PREF_PASSWORD, "");
										Intent loginIntent = new Intent(
												SplashScreenActivity.this,
												LoginChoiceScreen.class);
										startActivity(loginIntent);
									}
								}, 2000);
							}
						}
					});
		}
	}

}
