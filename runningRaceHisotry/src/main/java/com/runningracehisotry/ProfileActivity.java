package com.runningracehisotry;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.User;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.UpdateUserAvatarRequest;
import com.runningracehisotry.webservice.base.UpdateUserProfileRequest;
import com.runningracehisotry.webservice.base.UploadImageRequest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends BaseActivity {

	private ImageView mProfileImg;
	private boolean isSocialUser, isImageChanged;
	private EditText mOldPassEdt, mNewPassEdt, mConfirmPassEdt, mNameEdt;
    private Uri imageUriUpload;
    private String newAvatarUrl;
    private CustomLoadingDialog mLoadingDialog;

    @Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_profile;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		mNameEdt = (EditText) findViewById(R.id.profile_name_edt);
		mProfileImg = (ImageView) findViewById(R.id.profile_image);
		mOldPassEdt = (EditText) findViewById(R.id.profile_old_pass);
		mNewPassEdt = (EditText) findViewById(R.id.profile_new_pass);
		mConfirmPassEdt = (EditText) findViewById(R.id.profile_confirm_pass);
		mProfileImg.setOnClickListener(this);

		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_profile_bg)
				.showImageForEmptyUri(R.drawable.img_profile_bg)
				.showImageOnFail(R.drawable.img_profile_bg).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();

        fillUserProfile();

	}

    private void fillUserProfile() {
        String userStr = CustomSharedPreferences.getPreferences(Constants.PREF_USER_LOGGED_OBJECT, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, User.class);
        Log.d("QuyNT3", "Stored profile initial:" + userStr +"|" + (ServiceApi.SERVICE_URL + user.getProfile_image()));
        mNameEdt.setText(user.getFull_name());
        if(user.getProfile_image() != null && !user.getProfile_image().isEmpty()){
            mImageLoader.displayImage(ServiceApi.SERVICE_URL + user.getProfile_image(), mProfileImg, mOptions);
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == Constants.REQUEST_CODE_PROFILE_IMAGE_CAMERA) {

				Uri imageUri = Utilities.createImage();
				Utilities.startCropImage(ProfileActivity.this,
						Constants.REQUEST_CODE_PROFILE_IMAGE_CROP, imageUri);
                imageUriUpload = imageUri;
			} else if (requestCode == Constants.REQUEST_CODE_PROFILE_IMAGE_GALLERY) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(ProfileActivity.this,
						Constants.REQUEST_CODE_PROFILE_IMAGE_CROP, imageUri);
                imageUriUpload = imageUri;
			} else if (requestCode == Constants.REQUEST_CODE_PROFILE_IMAGE_CROP) {

				Bundle extras = data.getExtras();
				Bitmap imgBmp = extras.getParcelable("data");
				mProfileImg.setImageBitmap(imgBmp);
				mProfileImg.setTag(imgBmp);
				isImageChanged = true;
				return;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.profile_image:

			Utilities.showPickerImageDialog(ProfileActivity.this,
					Constants.REQUEST_CODE_PROFILE_IMAGE_CAMERA,
					Constants.REQUEST_CODE_PROFILE_IMAGE_GALLERY);
			break;
		case R.id.ic_action_save:
            if(isImageChanged){
                if(imageUriUpload != null){
                    //upload
                    if(mLoadingDialog == null) {
                        mLoadingDialog = CustomLoadingDialog.show(ProfileActivity.this, "", "", false, false);
                    }
                    UploadImageRequest request = new UploadImageRequest(imageUriUpload);
                    request.setListener(callBackEvent);
                    new Thread(request).start();
                }
            }
            else{
                String username = mNameEdt.getText().toString().trim();
                if (username.equals("")) {
                    Utilities.showAlertMessage(ProfileActivity.this,
                            getString(R.string.dialog_profile_input_username),
                            getString(R.string.dialog_profile_title));
                    return;
                }

			if (!isSocialUser) {

				String oldPassword = mOldPassEdt.getText().toString();
				String savedPassword = CustomSharedPreferences.getPreferences(
						Constants.PREF_PASSWORD, "");
				if (oldPassword.equals("")
						|| !oldPassword.equals(savedPassword)) {

					Utilities
							.showAlertMessage(
									ProfileActivity.this,
									getString(R.string.dialog_profile_old_password_incorrect),
									getString(R.string.dialog_profile_title));
					return;
				}

				String newPassword = mNewPassEdt.getText().toString().trim();
				String confirmPass = mConfirmPassEdt.getText().toString()
						.trim();
				if (newPassword.equals("") || confirmPass.equals("")) {

					Utilities
							.showAlertMessage(
									ProfileActivity.this,
									getString(R.string.dialog_profile_new_password_not_set),
									getString(R.string.dialog_profile_title));
					return;
				}

				if (!newPassword.equals(confirmPass)) {

					Utilities
							.showAlertMessage(
									ProfileActivity.this,
									getString(R.string.dialog_profile_new_password_not_match),
									getString(R.string.dialog_profile_title));
					return;
				}

                    // Save process
                    //new UpdateProfileAsyncTask().execute();
                    Log.d("QuyNT3", "udpate profile API 28: ");
                if(mLoadingDialog == null) {
                    mLoadingDialog = CustomLoadingDialog.show(ProfileActivity.this, "", "", false, false);
                }
                callUpdateProfile();
                }
            }

			break;
		default:
			break;
		}
	}

    /**
     * only update full NAme and password, not SNS account PUT method
     */
    private void callUpdateProfile() {
        String userStr = CustomSharedPreferences.getPreferences(Constants.PREF_USER_LOGGED_OBJECT, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, User.class);
        Log.d("QuyNT3", "Stored profile:" + userStr +"|" + (user != null));
        //mNameEdt.setText(user.getName());
        String id = user.getId();
        String oldPassword = mOldPassEdt.getText().toString();
        String newPassword = mNewPassEdt.getText().toString();
        String confirmPassword = mConfirmPassEdt.getText().toString();
        String fullName = mNameEdt.getText().toString();
        String profileImage = user.getProfile_image();
        UpdateUserProfileRequest request = new UpdateUserProfileRequest(id, fullName, profileImage,
                oldPassword, newPassword, confirmPassword);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    @Override
	public void onBackPressed() {

		backToHome();
	}

    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {

        }

        @Override
        public void Wsdl2CodeFinished(String methodName, final Object data) {
            LogUtil.i("Running", data.toString());
            if (methodName.equals(ServiceConstants.METHOD_UPDATE_USER_PROFILE)) {
                try {
                    JSONObject jsonObjectReceive = new JSONObject(data.toString());
                    boolean result = jsonObjectReceive.getBoolean("result");
                    // Login success
                    if (result) {
                        LogUtil.d("Running", "Update profile ok!: " + data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //update preferences and application variable
                                    Utilities.showAlertMessage(
                                            ProfileActivity.this,
                                            getString(R.string.dialog_profile_update_successfully),
                                            "");
                                    updateStoredInformation();
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                }
                            }
                        });


                    }
                    // Login fail
                    else {
                        LogUtil.d("Running", "Update profile fail!!!");
                        // Show dialog notify login fail
                        Utilities.showAlertMessage(
                                ProfileActivity.this,
                                getString(R.string.dialog_profile_update_fails),
                                "");
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utilities.showAlertMessage(
                            ProfileActivity.this,
                            getString(R.string.dialog_profile_update_fails),
                            "");
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
            else if (methodName.equals(ServiceConstants.METHOD_UPLOAD_IMAGE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.d(Constants.LOG_TAG, "upload response: " + data.toString());
                            JSONObject jsonObjectReceive = new JSONObject(data.toString());
                            String result = jsonObjectReceive.getString("name");
                            if(result != null && !result.isEmpty()){
                                callUpdateInfoProfile(result);
                                newAvatarUrl = result;
                            }
                            else{
                                try{
                                    if (mLoadingDialog.isShowing()) {
                                        mLoadingDialog.dismiss();
                                    }
                                }
                                catch(Exception ex){
                                }
                            }
                        } catch (Exception e) {
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                            }
                        }
                    }
                    });

            }

            else if (methodName.equals(ServiceConstants.METHOD_UPDATE_USER_AVATAR)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.d(Constants.LOG_TAG, "update Infor after upload response: " + data.toString());
                            JSONObject jsonObjectReceive = new JSONObject(data.toString());
                            String result = jsonObjectReceive.getString("result");
                            // Login success
                            if(result != null && result.equalsIgnoreCase("true")){
                                String userStr = CustomSharedPreferences.getPreferences(Constants.PREF_USER_LOGGED_OBJECT, "");
                                Gson gson = new Gson();
                                User user = gson.fromJson(userStr, User.class);
                                user.setProfile_image(newAvatarUrl);
                                RunningRaceApplication.getInstance().setCurrentUser(user);
                                String userJson = gson.toJson(user);
                                CustomSharedPreferences.setPreferences(Constants.PREF_USER_LOGGED_OBJECT, userJson);
                                LogUtil.d("Running", "update vars done after upload: " + (ServiceApi.SERVICE_URL + newAvatarUrl)
                                        + "userJson app and Preference: " + userJson);
                                mImageLoader.displayImage(ServiceApi.SERVICE_URL + newAvatarUrl, mProfileImg, mOptions);
                            }
                            else{
                                Utilities.showAlertMessage(
                                        ProfileActivity.this,
                                        getString(R.string.dialog_profile_update_fails),
                                        "");
                            }
                        } catch (Exception e) {
                            Utilities.showAlertMessage(
                                    ProfileActivity.this,
                                    getString(R.string.dialog_profile_update_fails),
                                    "");

                        }
                        finally {
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                            }
                        }
                    }
                });

            }

        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };

    private void callUpdateInfoProfile(String result) {
        String userStr = CustomSharedPreferences.getPreferences(Constants.PREF_USER_LOGGED_OBJECT, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, User.class);
        String id = user.getId();
        String email = user.getEmail();
        String fullName = user.getFull_name();
        String name = user.getName();

        UpdateUserAvatarRequest request = new UpdateUserAvatarRequest(id, email, fullName, name, result);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    private void updateStoredInformation() {
        String userStr = CustomSharedPreferences.getPreferences(Constants.PREF_USER_LOGGED_OBJECT, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, User.class);
        String confirmPassword = mConfirmPassEdt.getText().toString();
        CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD, confirmPassword);
        String fullName = mNameEdt.getText().toString();
        user.setFull_name(fullName);
        RunningRaceApplication.getInstance().setCurrentUser(user);
        String userJson = gson.toJson(user);
        CustomSharedPreferences.setPreferences(Constants.PREF_USER_LOGGED_OBJECT, userJson);
        LogUtil.d("Running","update vars done: "
            + "newPAssword: " + CustomSharedPreferences.getPreferences(Constants.PREF_PASSWORD,"")
            + "userJson app and Preference: " + userJson);
    }

}
