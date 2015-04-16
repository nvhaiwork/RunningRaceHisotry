package com.runningracehisotry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.runningracehisotry.adapters.NewShoeDistanceHistoryAdapter;
import com.runningracehisotry.adapters.ShoeDistanceHistoryAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.History;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddShoeRequest;
import com.runningracehisotry.webservice.base.DeleteShoeRequest;
import com.runningracehisotry.webservice.base.UpdateShoeRequest;

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

import org.json.JSONException;
import org.json.JSONObject;

public class AddShoeActivity extends BaseActivity {
	private ParseObject mShoe;
	private ImageView mShoeImage;
	private ListView mShoeDistanceListview;
	private TextView mAddMilesBtn, mMilesTxt;
	private EditText mShoeBrandEdt, mShoeModelEdt, mAddMilesEdt;

    private int shoeIdUpdate;
    private int shoeIdAdd;
    private float lastMileOfShoe;
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
        shoeIdUpdate = 0;
        if (selectedShoePosition >= 0) {
            String shoeJson = getIntent().getStringExtra(Constants.INTENT_UPDATE_SHOE);
            LogUtil.d(mCurrentClassName, "receive shoe update: " + shoeJson);
            if(shoeJson != null){
                Shoe shoe = Utilities.fromJson(shoeJson);
                if(shoe != null){
                    LogUtil.d(mCurrentClassName, "receive shoe !null, update shoeId = " + shoe.getId());
                    shoeIdUpdate = shoe.getId();
                    lastMileOfShoe = shoe.getMilesOnShoes();
                    // Data
                    mMilesTxt.setText(String.format("%.2f", (float) shoe.getMilesOnShoes()));
                    mShoeBrandEdt.setText(shoe.getBrand());
                    mShoeModelEdt.setText(shoe.getModel());
                    List<History> history = shoe.getMilesShoesHistories();
                    if(history != null && history.size()>0){
                        for(History his : history){
                            LogUtil.d(mCurrentClassName, "Shoe update infor: " + his.getCreatedAt()
                                    +": you added " + his.getMiles() + " miles");
                        }
                        NewShoeDistanceHistoryAdapter adapter = new NewShoeDistanceHistoryAdapter(this,history);
                        mShoeDistanceListview.setAdapter(adapter);
                    }

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
                float newMile = 0;
                if(!mMilesTxt.getText().toString().isEmpty()) {
                    newMile = Float.parseFloat(mMilesTxt.getText().toString());
                }
                float addMile =  newMile - lastMileOfShoe;
                if(shoeIdUpdate > 0){
                    //call update
                    if(addMile > 0){//has new info to update
                        String param = String.format("%.2f", addMile);
                        callUpdateShoe(param);
                    }
                    else{
                        //back to shoe list
                        Intent resultIntent = new Intent("updateShoeCallBack");
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
                else{
                    //process Add Shoe
                    //if(addMile > 0){
                    //not check when add
                    String param = String.format("%.2f", addMile);
                    callAddShoe(param);
                    //}
                }
                //process add or update
				//new SaveShoeAsync().execute();
			}
			break;
		case R.id.add_shoe_image:

			Utilities.showPickerImageDialog(AddShoeActivity.this,
					Constants.REQUETS_CODE_ADD_SHOE_TAKE_IMAGE,
					Constants.REQUETS_CODE_ADD_SHOE_CHO0SE_IMAGE);
			break;
		}
	}


    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
            //mLoadingDialog = CustomLoadingDialog.show(MyShoesActivity.this,"", "", false, false);
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, final Object data) {

            if (methodName.equals(ServiceConstants.METHOD_ADD_SHOES)) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processAfterAddShoe(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertMessage(AddShoeActivity.this, "Error Parse when get list shoes", "");
                        } finally {
                        }
                    }
                });


            }
            else if (methodName.equals(ServiceConstants.METHOD_UPDATE_SHOES)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processAfterUpdateShoe(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertMessage(AddShoeActivity.this, "Error Parse when get list shoes", "");
                        } finally {
                        }
                    }
                });
            }
            /*if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }*/
        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

            /*if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }*/
        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };
    private void callAddShoe(String miles) {
        String brand = mShoeBrandEdt.getText().toString();
        String model = mShoeModelEdt.getText().toString();
        String imageUrl = "picture/shoe.png";
        String param = "1";
        //String param = String.format("%.0f", addMile);

        LogUtil.d(mCurrentClassName, "Request Add Shoe: " + brand+"|"+imageUrl +"|"+param+"|"+model);
        AddShoeRequest request = new AddShoeRequest(brand, imageUrl, param, model);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }
    private void processAfterAddShoe(Object data ) throws  JSONException{
        JSONObject obj = new JSONObject(data.toString());
        String result = obj.getString("result");
        LogUtil.d(mCurrentClassName, "Response Add Shoe: " + result);
        if(result.equalsIgnoreCase("true")){
            Intent resultIntent = new Intent("addShoeCallBack");

            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();
        }
        else{
            LogUtil.d(mCurrentClassName, "Response Add Shoe Failed ");
        }

    }

    private void callUpdateShoe(String miles) {

        String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
        String brand = mShoeBrandEdt.getText().toString();
        String model = mShoeModelEdt.getText().toString();
        String imageUrl = "picture/shoe.png";
        String mileOnShoe = "0";
        LogUtil.d(mCurrentClassName, "Request UPdate Shoe: " + brand+"|"+shoeIdUpdate+"|"+imageUrl +"|"
                +miles+"|"+mileOnShoe+"|"+model+"|"+userId);

        UpdateShoeRequest request = new UpdateShoeRequest(brand, String.valueOf(shoeIdUpdate),
                imageUrl, miles, mileOnShoe, model, userId);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }
    private void processAfterUpdateShoe(Object data) throws  JSONException{
        JSONObject obj = new JSONObject(data.toString());
        String result = obj.getString("result");
        LogUtil.d(mCurrentClassName, "Response UPdate Shoe: " + result);
        if(result.equalsIgnoreCase("true")){
            Intent resultIntent = new Intent("updateShoeCallBack");
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else{
            LogUtil.d(mCurrentClassName, "Response UPdate Shoe Failed ");
        }
    }

	/*private class SaveShoeAsync extends AsyncTask<Void, Void, Void> {

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
	}*/
}
