package com.runningracehisotry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.runningracehisotry.adapters.ShoeDistanceHistoryAdapter;
import com.runningracehisotry.constants.Constants;
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
import android.widget.ListView;
import android.widget.TextView;

public class AddShoeActivity extends BaseActivity {
	private ParseObject mShoe;
	private ImageView mShoeImage;
	private ListView mShoeDistanceListview;
	private TextView mAddMilesBtn, mMilesTxt;
	private EditText mShoeBrandEdt, mShoeModelEdt, mAddMilesEdt;

	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_shoe;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == Constants.REQUETS_CODE_ADD_SHOE_TAKE_IMAGE) {

				Uri imageUri = Utilities.createImage();
				Utilities.startCropImage(AddShoeActivity.this,
						Constants.REQUETS_CODE_ADD_SHOE_IMAGE_CROP, imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_SHOE_CHO0SE_IMAGE) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(AddShoeActivity.this,
						Constants.REQUETS_CODE_ADD_SHOE_IMAGE_CROP, imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_SHOE_IMAGE_CROP) {

				Bundle extras = data.getExtras();
				Bitmap imgBmp = extras.getParcelable("data");
				mShoeImage.setImageBitmap(Utilities.getRoundedCornerBitmap(
						imgBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner)));
				mShoeImage.setTag(imgBmp);
				return;
			}
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		mBotLeftBtnTxt.setVisibility(View.VISIBLE);
		mBotRightBtnTxt.setVisibility(View.VISIBLE);
		mBottomBtnLayout.setBackgroundColor(getResources().getColor(
				R.color.text_button_bg_my_shoes));

		mShoeImage = (ImageView) findViewById(R.id.add_shoe_image);
		mShoeModelEdt = (EditText) findViewById(R.id.add_shoe_model_edt);
		mShoeBrandEdt = (EditText) findViewById(R.id.add_shoe_brand_edt);
		mAddMilesBtn = (TextView) findViewById(R.id.add_show_add_miles_btn);
		mAddMilesEdt = (EditText) findViewById(R.id.add_shoe_add_miles_edt);
		mMilesTxt = (TextView) findViewById(R.id.add_shoe_miles_on_shoe_edt);
		mShoeDistanceListview = (ListView) findViewById(R.id.add_shoe_distance_history_list);
		mShoeImage.setTag(null);
		int selectedShoePosition = getIntent().getIntExtra(
				Constants.INTENT_ADD_SHOE, -1);
		if (selectedShoePosition >= 0) {

			mShoe = mShoes.get(selectedShoePosition);
			if (mShoe != null) {

				// Data
				mMilesTxt.setText(String.format("%.2f",
						(float) mShoe.getDouble(Constants.DISTANCE)));
				mShoeBrandEdt.setText(mShoe.getString(Constants.BRAND));
				mShoeModelEdt.setText(mShoe.getString(Constants.MODEL));
				if (mShoe.containsKey(Constants.IMAGE)) {
					ParseObject image = mShoe.getParseObject(Constants.IMAGE);
					Utilities.displayParseImage(
							image,
							mShoeImage,
							getResources().getDimensionPixelSize(
									R.dimen.image_round_conner));
				}

				if (mShoe.containsKey(Constants.HISTORY)) {

					List<HashMap<String, Object>> distHistories = mShoe
							.getList(Constants.HISTORY);
					ShoeDistanceHistoryAdapter adapter = new ShoeDistanceHistoryAdapter(
							AddShoeActivity.this, distHistories);
					mShoeDistanceListview.setAdapter(adapter);
				}
			}
		}

		mShoeImage.setOnClickListener(this);
		mAddMilesBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.add_show_add_miles_btn:

			String addMilesStr = mAddMilesEdt.getText().toString();
			try {

				float newMile = Float.valueOf(addMilesStr);
				String orgMileStr = mMilesTxt.getText().toString();
				float orgMile = 0;
				if (!orgMileStr.equals("")) {

					orgMile = Float.valueOf(orgMileStr);
				}

				orgMile += newMile;
				mMilesTxt.setText(String.format("%.2f", orgMile));
			} catch (Exception ex) {

				LogUtil.e("add_show_add_miles_btn", ex.getMessage());
			}

			mAddMilesEdt.setText("");
			break;
		case R.id.bottom_button_right_text:

			if (mShoeModelEdt.getText().toString().equals("")
					|| mShoeBrandEdt.getText().toString().equals("")) {

				Utilities.showAlertMessage(AddShoeActivity.this,
						getString(R.string.dialog_add_shoe_fill_all_fields),
						getString(R.string.dialog_add_shoe_tile));
			} else {

				new SaveShoeAsync().execute();
			}
			break;
		case R.id.add_shoe_image:

			Utilities.showPickerImageDialog(AddShoeActivity.this,
					Constants.REQUETS_CODE_ADD_SHOE_TAKE_IMAGE,
					Constants.REQUETS_CODE_ADD_SHOE_CHO0SE_IMAGE);
			break;
		}
	}

	private class SaveShoeAsync extends AsyncTask<Void, Void, Void> {

		private Dialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			dialog = CustomLoadingDialog.show(AddShoeActivity.this, "", "",
					false, false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			float origDist = 0, newDis = 0;
			List<HashMap<String, Object>> dictHistories = new ArrayList<HashMap<String, Object>>();
			if (mShoe != null) {

				try {

					if (mShoe.containsKey(Constants.HISTORY)) {

						List<HashMap<String, Object>> oldDist = mShoe
								.getList(Constants.HISTORY);
						dictHistories.addAll(oldDist);
					}

					origDist = (float) mShoe.getDouble(Constants.DISTANCE);
					mShoe.delete();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					LogUtil.e("bottom_button_center_text", e.getMessage());
				}

				mShoes.remove(mShoe);
			}

			ParseObject shoe = new ParseObject(Constants.SHOE);
			shoe.put(Constants.BRAND, mShoeBrandEdt.getText().toString());
			shoe.put(Constants.MODEL, mShoeModelEdt.getText().toString());
			try {

				newDis = Float.valueOf(mMilesTxt.getText().toString());
			} catch (Exception ex) {

				newDis = 0f;
			}

			if (newDis > origDist) {

				float diff = newDis - origDist;
				HashMap<String, Object> hist = new HashMap<String, Object>();
				hist.put(Constants.DATE, new Date());
				hist.put(Constants.ADDED, diff);
				dictHistories.add(hist);
			}

			shoe.put(Constants.DISTANCE, newDis);
			shoe.put(Constants.HISTORY, dictHistories);

			// Image
			try {

				Bitmap bitmap = (Bitmap) mShoeImage.getTag();
				if (bitmap != null) {
					ParseObject object = new ParseObject(Constants.IMAGE);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] bitmapdata = stream.toByteArray();
					ParseFile file = new ParseFile(bitmapdata);
					try {

						object.put(Constants.DATA.toUpperCase(), file);
						object.save();
					} catch (ParseException e) {

						LogUtil.e("Save image", e.getMessage());
					}

					shoe.put(Constants.IMAGE, object);
				}
			} catch (Exception ex) {

				LogUtil.e("Shoe file", ex.getMessage());
			}

			try {

				shoe.save();
				mShoes.add(shoe);
				mUser.put(Constants.SHOES, mShoes);
				mUser.saveInBackground();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				LogUtil.e("bottom_button_center_text", e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			setResult(RESULT_OK);
			dialog.dismiss();
			finish();
		}
	}
}
