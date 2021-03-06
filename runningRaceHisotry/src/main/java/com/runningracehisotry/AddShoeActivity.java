package com.runningracehisotry;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.runningracehisotry.adapters.NewShoeDistanceHistoryAdapter;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.History;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddShoeRequest;

import com.runningracehisotry.webservice.base.UpdateShoeRequest;
import com.runningracehisotry.webservice.base.UploadImageRequest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddShoeActivity extends BaseActivity {
	//private ParseObject mShoe;
	private ImageView mShoeImage, mShoeDelete;
	private ListView mShoeDistanceListview;
	private TextView mAddMilesBtn, mMilesTxt;
	private EditText mShoeBrandEdt, mShoeModelEdt, mAddMilesEdt;

    private int shoeIdUpdate;
    private CustomLoadingDialog mLoadingDialog;
    private float lastMileOfShoe;

    private String mShoeImgPath;
    private Uri mUriImageUpload;
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
                mUriImageUpload = imageUri;
                //mShoeDelete.setVisibility(View.VISIBLE);
                LogUtil.d(mCurrentClassName, "Take image: " + mUriImageUpload);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_SHOE_CHO0SE_IMAGE) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(AddShoeActivity.this,
						Constants.REQUETS_CODE_ADD_SHOE_IMAGE_CROP, imageUri);
                mUriImageUpload = imageUri;
                //mShoeDelete.setVisibility(View.VISIBLE);
                LogUtil.d(mCurrentClassName, "Choose image: " + mUriImageUpload);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_SHOE_IMAGE_CROP) {

				Bundle extras = data.getExtras();
				Bitmap imgBmp = extras.getParcelable("data");
				mShoeImage.setImageBitmap(Utilities.getRoundedCornerBitmap(
						imgBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner)));
				mShoeImage.setTag(imgBmp);
                mUriImageUpload = getImageUri(this, imgBmp);
                mShoeDelete.setVisibility(View.VISIBLE);
                LogUtil.d(mCurrentClassName, "Crop image: " + mUriImageUpload);
				return;
			}
		}
	}

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		mBotLeftBtnTxt.setVisibility(View.VISIBLE);
		mBotRightBtnTxt.setVisibility(View.VISIBLE);
		mBottomBtnLayout.setBackgroundColor(getResources().getColor(
				R.color.text_button_bg_my_shoes));
        mShoeDelete = (ImageView) findViewById(R.id.delete_shoe_image);
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
        mShoeDelete.setVisibility(View.GONE);
        if (selectedShoePosition >= 0) {
            mAddMilesEdt.setHint("");
            String shoeJson = getIntent().getStringExtra(Constants.INTENT_UPDATE_SHOE);
            LogUtil.d(mCurrentClassName, "receive shoe update: " + shoeJson);
            if(shoeJson != null){
                Shoe shoe = Utilities.fromJson(shoeJson);
                if(shoe != null){
                    LogUtil.d(mCurrentClassName, "receive shoe !null, update shoeId = "+shoe.getMilesOnShoes()
                            +"|"+ shoe.getId() +
                    "|"+ String.format(Locale.US,"%.2f", shoe.getMilesOnShoes()));
                    shoeIdUpdate = shoe.getId();
                    lastMileOfShoe = shoe.getMilesOnShoes();
                    // Data
                    mMilesTxt.setText(String.format(Locale.US, "%.2f", shoe.getMilesOnShoes()));
                    mShoeBrandEdt.setText(shoe.getBrand());
                    mShoeModelEdt.setText(shoe.getModel());
                    List<History> history = null;//shoe.getMilesShoesHistories();
                    JSONArray arrShoe = null;
                    JSONObject objShoe = null;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(shoeJson);
                        arrShoe = obj.getJSONArray("miles_shoes_histories");
                    }
                    catch(JSONException ex){
                        try {
                            objShoe = obj.getJSONObject("miles_shoes_histories");
                        }
                        catch(Exception exx){}
                    }
                    if(arrShoe != null){
                        LogUtil.d(mCurrentClassName, "History string array: " + arrShoe.toString());
                        shoe.setMilesShoesHistoriesString(arrShoe);
                    }
                    else if(objShoe != null){
                        LogUtil.d(mCurrentClassName, "History string object: "  + objShoe.toString());
                        shoe.setMilesShoesHistoriesString(objShoe);
                    }
                    //LogUtil.d(mCurrentClassName, "History string array: " + arrShoe.toString());
                    history = shoe.getMilesShoesHistories();
                    //LogUtil.d(mCurrentClassName, "History string: " + history.toString());
                    if(history != null && history.size()>0){
                        for(History his : history){
                            LogUtil.d(mCurrentClassName, "Shoe update infor: " + his.getCreatedAt()
                                    +": you added " + his.getMiles() + " miles");
                        }
                        NewShoeDistanceHistoryAdapter adapter = new NewShoeDistanceHistoryAdapter(this,history);
                        mShoeDistanceListview.setAdapter(adapter);
                    }
                    mShoeImgPath = shoe.getImageUrl();
                    if((shoe.getImageUrl() != null) && (!shoe.getImageUrl().isEmpty())){
                        mOptions = new DisplayImageOptions.Builder()
                                .displayer(new RoundedBitmapDisplayer(R.dimen.image_round_conner_new))
                                .showImageOnLoading(R.drawable.ic_photo_of_shoe)
                                .showImageForEmptyUri(R.drawable.ic_photo_of_shoe)
                                .showImageOnFail(R.drawable.ic_photo_of_shoe).cacheInMemory(true)
                                .cacheOnDisc(true).considerExifParams(true)
                                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
                        //mImageLoader.displayImage(ServiceApi.SERVICE_URL + shoe.getImageUrl(), mShoeImage, mOptions);
                        mImageLoader.loadImage(ServiceApi.SERVICE_URL + shoe.getImageUrl(), new SimpleImageLoadingListener()
                        {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage)
                            {
                                // Do whatever you want with Bitmap
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            Bitmap bmp = Utilities.getRoundedCornerBitmap(loadedImage, 14);
                                            mShoeImage.setImageBitmap(bmp);
                                            mShoeDelete.setVisibility(View.VISIBLE);
                                        }
                                        catch (Exception ex){
                                        }
                                    }
                                });

                            }
                        });
                        //Bitmap bitmap = mImageLoader.loadImageSync(ServiceApi.SERVICE_URL + shoe.getImageUrl());
                       /* Utilities.displayParseImage(
                                image,
                                mShoeImage,
                                getResources().getDimensionPixelSize(
                                        R.dimen.image_round_conner));*/
                    }

                }
                else{

                }
            }
		}

		mShoeImage.setOnClickListener(this);
        mShoeDelete.setOnClickListener(this);
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
                    mMilesTxt.setText(String.format(Locale.US, "%.2f", orgMile));
                } catch (Exception ex) {

                    LogUtil.e("add_show_add_miles_btn", "error when parse");
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
                    if (!mMilesTxt.getText().toString().isEmpty()) {
                        newMile = Float.parseFloat(mMilesTxt.getText().toString());
                    }
                    float addMile = newMile - lastMileOfShoe;
                    if (shoeIdUpdate > 0) {
                        //call update
                        if (addMile >= 0) {//has new info to update
                            //String param = String.format("%.2f", addMile);
                            if (mUriImageUpload != null) {
                                callUploadShoeImage();
                            } else {
                                callUpdateShoe();
                            }
                        }

                    } else {
                        if (mMilesTxt.getText().toString().isEmpty() || (mMilesTxt.getText().toString().equalsIgnoreCase("0.00"))) {
                            Utilities.showAlertMessage(AddShoeActivity.this,
                                    getString(R.string.dialog_add_shoe_fill_all_fields),
                                    getString(R.string.dialog_add_shoe_tile));
                        } else {


                            //process Add Shoe
                            //if(addMile > 0){
                            //not check when add
                            //String param = String.format("%.2f", addMile);
                            //callAddShoe(param);
                            if (mUriImageUpload != null) {
                                callUploadShoeImage();
                            } else {
                                callAddShoe();
                            }
                            //callUploadShoeImage();
                            //}
                        }
                    }
                    //process add or update
                    //new SaveShoeAsync().execute();
                }
                break;
            case R.id.delete_shoe_image:
                LogUtil.e(mCurrentClassName, "delete_shoe_image");
                showDialogDelete();

                break;
            case R.id.add_shoe_image:

                Utilities.showPickerImageDialog(AddShoeActivity.this,
                        Constants.REQUETS_CODE_ADD_SHOE_TAKE_IMAGE,
                        Constants.REQUETS_CODE_ADD_SHOE_CHO0SE_IMAGE);
                break;



        }
	}

    private void processDeleteImage(){
        if (mUriImageUpload != null) {
            //has upload
            //if add
            //clear uri
            mUriImageUpload = null;
            //set no image
            mShoeImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_of_shoe));
            //if edit TODO
            mShoeImgPath = "";
        }
        else{
            //no upload
            //if add: do nothing
            //if edit has image TODO
            mShoeImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_of_shoe));
            if(shoeIdUpdate > 0) {
                mShoeImgPath = "";
            }
        }
        mShoeDelete.setVisibility(View.GONE);
    }
    private void showDialogDelete() {
        final CustomAlertDialog dialog = new CustomAlertDialog(AddShoeActivity.this);
        dialog.setCancelableFlag(false);
        dialog.setTitle(getString(R.string.dialog_add_shoe_tile));
        dialog.setMessage(getString(R.string.dialog_delete_shoe_image));
        dialog.setNegativeButton(getString(R.string.no),
                new CustomAlertDialog.OnNegativeButtonClick() {

                    @Override
                    public void onButtonClick(final View view) {
                        // TODO Auto-generated method stub

                        dialog.dismiss();
                    }
                });
        dialog.setPositiveButton(getString(R.string.yes),
                new CustomAlertDialog.OnPositiveButtonClick() {

                    @Override
                    public void onButtonClick(View view) {
                        // TODO Auto-generated method stub

                        //saveUserData();
                        Toast.makeText(AddShoeActivity.this, "Choose URL IMAGE SHOE empty", Toast.LENGTH_SHORT);
                        LogUtil.e(mCurrentClassName, "delete_shoe_image CHOOSE");
                        processDeleteImage();
                        dialog.dismiss();
                    }
                });

        dialog.show();
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
                            Utilities.showAlertMessage(AddShoeActivity.this, getResources().getString(R.string.shoe_add_failed), "");
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
                            Utilities.showAlertMessage(AddShoeActivity.this, getResources().getString(R.string.shoe_update_failed), "");
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
                });
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
                                mShoeImgPath  = result;
                                LogUtil.d(Constants.LOG_TAG, "upload url: " + mShoeImgPath);
                                if(shoeIdUpdate > 0){
                                    callUpdateShoe();
                                }
                                else{
                                    callAddShoe();
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
                            Utilities.showAlertMessage(AddShoeActivity.this, getResources().getString(R.string.upload_image_failed), "");
                            //e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

            try{
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
            catch(Exception exc){
            }
        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };




    private void callAddShoe() {
        float newMile = 0;
        if(!mMilesTxt.getText().toString().isEmpty()) {
            try{
                newMile = Float.parseFloat(mMilesTxt.getText().toString());
            }
            catch(Exception ex){
                //ex.printStackTrace();
            }
        }
        float addMile =  newMile - lastMileOfShoe;
        String miles = "0.00";
        if(addMile > 0){
            miles = String.format(Locale.US, "%.2f", addMile);
        }
        String brand = mShoeBrandEdt.getText().toString();
        String model = mShoeModelEdt.getText().toString();
        String imageUrl = mShoeImgPath;
                //String imageUrl = "picture/shoe.png";
        //String param = "1";
        //String param = String.format("%.0f", addMile);
        if ((mLoadingDialog == null) || (!mLoadingDialog.isShowing())) {
            mLoadingDialog = CustomLoadingDialog.show(AddShoeActivity.this, "", "", false, false);
        }
        LogUtil.d(mCurrentClassName, "Request Add Shoe: " + brand+"|"+imageUrl +"|"+miles+"|"+model);
        AddShoeRequest request = new AddShoeRequest(brand, imageUrl, miles, model);
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

    private void callUploadShoeImage(){
        mLoadingDialog = CustomLoadingDialog.show(AddShoeActivity.this, "", "", false, false);
        UploadImageRequest request = new UploadImageRequest(mUriImageUpload);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }
    private void callUpdateShoe() {
        float newMile = 0;
        if(!mMilesTxt.getText().toString().isEmpty()) {
            try{
                newMile = Float.parseFloat(mMilesTxt.getText().toString());
            }
            catch(Exception ex){
                //ex.printStackTrace();
            }
        }
        float addMile =  newMile - lastMileOfShoe;
        String miles = "0";
        if(addMile > 0){
            miles = String.format(Locale.US, "%.2f", addMile);
        }
        String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
        String brand = mShoeBrandEdt.getText().toString();
        String model = mShoeModelEdt.getText().toString();
        String imageUrl = mShoeImgPath;
        String mileOnShoe = "0";
        LogUtil.d(mCurrentClassName, "Request UPdate Shoe: " + brand+"|"+shoeIdUpdate+"|"+imageUrl +"|"
                +miles+"|"+mileOnShoe+"|"+model+"|"+userId);

        if ((mLoadingDialog == null) || (!mLoadingDialog.isShowing())) {
            mLoadingDialog = CustomLoadingDialog.show(AddShoeActivity.this, "", "", false, false);
        }

            //update without image
            UpdateShoeRequest request = new UpdateShoeRequest(brand, String.valueOf(shoeIdUpdate),
                    imageUrl, miles, mileOnShoe, model, userId);
            request.setListener(callBackEvent);
            new Thread(request).start();

    }


    private void processAfterUpdateShoe(Object data) throws  JSONException{
        JSONObject obj = new JSONObject(data.toString());
        LogUtil.d(mCurrentClassName, "Response UPdate Shoe full:" + data.toString());
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
