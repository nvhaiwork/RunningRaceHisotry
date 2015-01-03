package com.runningracehisotry;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ProfileActivity extends BaseActivity {

	private ImageView mProfileImg;
	private boolean isSocialUser, isImageChanged;
	private EditText mOldPassEdt, mNewPassEdt, mConfirmPassEdt, mNameEdt;

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

		mNameEdt.setText(mUser.getUsername());

		// User image
		if (mUser.containsKey(Constants.PICTURE)) {

			ParseObject image = mUser.getParseObject(Constants.PICTURE);
			Utilities.displayParseImage(image, mProfileImg, 0);
		} else {

			String imgUrl = Utilities.getUserProfileImage(mUser);
			mImageLoader.displayImage(imgUrl, mProfileImg, mOptions);
		}

		if (mUser.containsKey(Constants.KIND)) {

			isSocialUser = true;
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
			} else if (requestCode == Constants.REQUEST_CODE_PROFILE_IMAGE_GALLERY) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(ProfileActivity.this,
						Constants.REQUEST_CODE_PROFILE_IMAGE_CROP, imageUri);
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
				new UpdateProfileAsyncTask().execute();
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {

		backToHome();
	}

	private class UpdateProfileAsyncTask extends AsyncTask<Void, Void, Integer> {

		Dialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(ProfileActivity.this, "", "",
					false, false);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (isImageChanged) {

				if (mUser.containsKey(Constants.PICTURE)) {

					ParseObject picture = mUser
							.getParseObject(Constants.PICTURE);
					try {

						picture.delete();
					} catch (ParseException e) {

						LogUtil.e("UpdateProfileAsyncTask", e.getMessage());
					}

					mUser.remove(Constants.PICTURE);
					ParseObject object = new ParseObject(Constants.IMAGE);
					Bitmap bitmap = (Bitmap) mProfileImg.getTag();
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] bitmapdata = stream.toByteArray();
					ParseFile file = new ParseFile(bitmapdata);
					try {

						object.put(Constants.DATA.toUpperCase(), file);
						object.save();
						mUser.put(Constants.PICTURE, object);
						mUser.save();
					} catch (ParseException e) {

						LogUtil.e("Save image", e.getMessage());
					}
				}
			}

			String savedUsername = CustomSharedPreferences.getPreferences(
					Constants.PREF_USERNAME, "");
			String newUsername = mNameEdt.getText().toString().trim();
			if (!savedUsername.equals(newUsername)) {

				ParseQuery<ParseUser> query = ParseUser.getQuery();
				query.whereEqualTo(Constants.USERNAME, newUsername);
				try {
					List<ParseUser> results = query.find();
					if (results.size() > 0) {

						return 0;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block

					return -1;
				}
			}

			mUser.setUsername(newUsername);
			if (!isSocialUser) {

				mUser.setPassword(mNewPassEdt.getText().toString().trim());
				CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD,
						mNewPassEdt.getText().toString().trim());
			}

			try {

				mUser.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return -1;
			}

			CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME,
					mUser.getUsername());
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// Result -1: exceptions
			// Result 0 : Username existed
			// Result 1 : Update successfully
			dialog.dismiss();
			if (result == -1) {

				Utilities.showAlertMessage(ProfileActivity.this,
						getString(R.string.dialog_profile_update_fails),
						getString(R.string.dialog_profile_title));
			} else if (result == 0) {

				Utilities.showAlertMessage(ProfileActivity.this,
						getString(R.string.dialog_profile_username_used),
						getString(R.string.dialog_profile_title));
			} else {

				Utilities.showAlertMessage(ProfileActivity.this,
						getString(R.string.dialog_profile_update_successfully),
						getString(R.string.dialog_profile_title));
			}
		}
	}
}
