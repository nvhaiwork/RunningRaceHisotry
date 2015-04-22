/**
 * 
 */
package com.runningracehisotry;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.User;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetUserProfileRequest;
import com.runningracehisotry.webservice.base.LoginRequest;
import com.runningracehisotry.webservice.base.RegisterFacebookRequest;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
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
public class LoginChoiceScreen extends BaseActivity implements IWsdl2CodeEvents {

	private Dialog mLoadingDialog;
	private ImageView mCreateBtn, mLoginFbBtn, mLoginTwitterBtn, mContactUsBtn;
    private final String logTag = "LoginChoiceScreen";
    private Twitter twitter;
    private RequestToken requestToken;
    private String fbID, fullName, email, avatar;

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
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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
//			List<String> permissions = Arrays.asList("public_profile",
//					"user_friends", "email");
//			ParseFacebookUtils.logIn(permissions, LoginChoiceScreen.this,
//					new LogInCallback() {
//
//						@Override
//						public void done(ParseUser user, ParseException arg1) {
//							// TODO Auto-generated method stub
//							if (user == null) {
//
//								Utilities
//										.showAlertMessage(
//												LoginChoiceScreen.this,
//												getString(R.string.dialog_facebook_login_fail),
//												getString(R.string.dialog_sign_in));
//								mLoadingDialog.dismiss();
//							} else if (user.isNew()) {
//
//								requestUserInfo();
//							} else {
//
//								try {
//
//									user.fetch();
//									mUser = user;
//									if (!mUser.containsKey(Constants.KIND)) {
//
//										mUser.put(Constants.KIND,
//												"fb:" + user.getUsername());
//									}
//
//									mHistory = user.getList(Constants.DATA);
//									mShoes = user.getList(Constants.SHOES);
//									mFriends = user.getList(Constants.FRIENDS);
//									user.setPassword("kki");
//									CustomSharedPreferences.setPreferences(
//											Constants.PREF_USERNAME,
//											user.getUsername());
//									CustomSharedPreferences.setPreferences(
//											Constants.PREF_PASSWORD, "kki");
//									Intent selectRaceIntent = new Intent(
//											LoginChoiceScreen.this,
//											SelectRaceActivity.class);
//									selectRaceIntent
//											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//													| Intent.FLAG_ACTIVITY_NEW_TASK);
//									selectRaceIntent
//											.putExtra(
//													Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
//													-1);
//									startActivity(selectRaceIntent);
//									mLoadingDialog.dismiss();
//									finish();
//								} catch (ParseException e) {
//
//									mLoadingDialog.dismiss();
//								}
//							}
//						}
//					});

            performFacebookLogin();
			break;
		case R.id.login_with_twitter:

			mLoadingDialog = CustomLoadingDialog.show(LoginChoiceScreen.this,
					"", "", false, false);
//			ParseTwitterUtils.logIn(LoginChoiceScreen.this,
//					new LogInCallback() {
//
//						@Override
//						public void done(ParseUser user, ParseException arg1) {
//							// TODO Auto-generated method stub
//
//							if (user == null) {
//
//								Utilities
//										.showAlertMessage(
//												LoginChoiceScreen.this,
//												"Uh oh. The user cancelled the Twitter login.",
//												getString(R.string.dialog_sign_in));
//								mLoadingDialog.dismiss();
//							} else if (user.isNew()) {
//
//								String twitterName = ParseTwitterUtils
//										.getTwitter().getScreenName();
//
//								mShoes = new ArrayList<ParseObject>();
//								mHistory = new ArrayList<HashMap<String, Object>>();
//								mFriends = new ArrayList<ParseUser>();
//								user.put(Constants.SHOES, mShoes);
//								user.put(Constants.DATA, mHistory);
//								user.put(Constants.FRIENDS, mFriends);
//								user.put(Constants.FULLNAME, twitterName);
//								user.put(Constants.KIND, "tw:" + twitterName);
//								user.setPassword("kki");
//								user.saveInBackground();
//								mUser = user;
//								CustomSharedPreferences.setPreferences(
//										Constants.PREF_USERNAME,
//										user.getUsername());
//								CustomSharedPreferences.setPreferences(
//										Constants.PREF_PASSWORD, "kki");
//								Intent selectRaceIntent = new Intent(
//										LoginChoiceScreen.this,
//										SelectRaceActivity.class);
//								selectRaceIntent
//										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//												| Intent.FLAG_ACTIVITY_NEW_TASK);
//								selectRaceIntent
//										.putExtra(
//												Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
//												-1);
//								startActivity(selectRaceIntent);
//								mLoadingDialog.dismiss();
//								finish();
//							} else {
//
//								try {
//
//									user.fetch();
//									mUser = user;
//									if (!mUser.containsKey(Constants.KIND)) {
//
//										String twitterName = ParseTwitterUtils
//												.getTwitter().getScreenName();
//										mUser.put(Constants.KIND, "tw:"
//												+ twitterName);
//									}
//
//									mHistory = user.getList(Constants.DATA);
//									mShoes = user.getList(Constants.SHOES);
//									mFriends = user.getList(Constants.FRIENDS);
//									user.setPassword("kki");
//									CustomSharedPreferences.setPreferences(
//											Constants.PREF_USERNAME,
//											user.getUsername());
//									CustomSharedPreferences.setPreferences(
//											Constants.PREF_PASSWORD, "kki");
//									Intent selectRaceIntent = new Intent(
//											LoginChoiceScreen.this,
//											SelectRaceActivity.class);
//									selectRaceIntent
//											.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//													| Intent.FLAG_ACTIVITY_NEW_TASK);
//									selectRaceIntent
//											.putExtra(
//													Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
//													-1);
//									startActivity(selectRaceIntent);
//									mLoadingDialog.dismiss();
//									finish();
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									mLoadingDialog.dismiss();
//								}
//							}
//						}
//					});
//            Uri uri = getIntent().getData();
//
//            if (uri != null && uri.toString().startsWith(callbackUrl)) {
//
//                String verifier = uri.getQueryParameter(oAuthVerifier);
//
//                try {
//
//					/* Getting oAuth authentication token */
//                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
//
//					/* Getting user id form access token */
//                    long userID = accessToken.getUserId();
//                    final User user = twitter.showUser(userID);
//                    final String username = user.getName();
//
//					/* save updated token */
//                    saveTwitterInfo(accessToken);
//
//                    loginLayout.setVisibility(View.GONE);
//                    shareLayout.setVisibility(View.VISIBLE);
//                    userName.setText(getString(R.string.hello) + username);
//
//                } catch (Exception e) {
//                    Log.e("Failed to login Twitter!!", e.getMessage());
//                }
//            }
			break;
		case R.id.login_contact_us:

			Utilities.contactUs(LoginChoiceScreen.this);
			break;
		}
	}

        private void performFacebookLogin() {
            Log.d("FACEBOOK", "performFacebookLogin");
//            final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("email"));
            Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback() {

                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    Log.d("FACEBOOK", "call");
                    if (session.isOpened()) {
                        Log.d("FACEBOOK", "if (session.isOpened() && !isFetching)");
//                        session.requestNewReadPermissions(newPermissionsRequest);
                        Request getMe = Request.newMeRequest(session, new Request.GraphUserCallback() {

                            @Override
                            public void onCompleted(GraphUser user, Response response) {
                                mLoadingDialog.dismiss();
                                Log.d("FACEBOOK", "onCompleted");
                                if (user != null) {
                                    Log.d("FACEBOOK", "user != null");
                                    String id = user.getId();
                                    email = user.getProperty("email").toString();

                                    if (email == null || email.length() < 0) {
                                        Toast.makeText(LoginChoiceScreen.this, "login fail", Toast.LENGTH_LONG);
                                        return;
                                    } else {
                                        fbID = id;
                                        fullName = user.getName();
                                        avatar = "http://graph.facebook.com/"+ id+ "/picture?type=large";

                                        RegisterFacebookRequest request = new RegisterFacebookRequest(id, fullName, avatar);
                                        request.setListener(LoginChoiceScreen.this);
                                        new Thread(request).start();
                                    }
                                }
                            }
                        });
                        getMe.executeAsync();
                    }
                }
            });
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

    private void handleResponseRegister(Object data) {
        CustomSharedPreferences.setPreferences(Constants.PREF_FB_ID, fbID);
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
//        Intent selectRaceIntent = new Intent(this, SelectRaceActivity.class);
//        selectRaceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        selectRaceIntent.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
//
//        mLoadingDialog.dismiss();
//        startActivity(selectRaceIntent);
//        finish();

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
                performFacebookLogin();
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
    }
}
