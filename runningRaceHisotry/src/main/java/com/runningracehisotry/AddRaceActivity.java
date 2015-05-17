package com.runningracehisotry;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import com.google.gson.Gson;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Race;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.ImageLoaderMedal;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.MyTimePickerDialog;
import com.runningracehisotry.views.MyTimePickerDialog.OnTimeSetListener;
import com.runningracehisotry.views.TimePicker;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddRaceRequest;
import com.runningracehisotry.webservice.base.UpdateRaceRequest;
import com.runningracehisotry.webservice.base.UploadImageRequest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
//import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddRaceActivity extends BaseActivity implements OnTimeSetListener {

    //private ParseObject mShoe;
    private SimpleDateFormat mDf;
    private static HashMap<String, Object> mRace;
    private static Race mRaceUpdate;
    private EditText mRaceNameEdt, mRaceWebsiteEdt, mRaceCityEdt,
            mRaceStateEdt;
    private TextView mShoeTxt, mRaceDateTxt, mRaceTypeTxt, mRaceFinishTimeTxt;
    private ImageView mBidImg, mPersonImg, mMedalImg;

    private String mRaceMedalPath, mRaceBibPath, mRacePersonPath;
    private Uri mRaceMedalUpload, mRaceBibUpload, mRacePersonUpload;
    private CustomLoadingDialog mLoadingDialog;
    private int uploadedRaceImage;

    private LinearLayout lnGroup;
    private RadioGroup rdGroup;
    private RadioButton rdKm, rdMile;
    private EditText edtRaceMile;


    @Override
    protected int addContent() {
        // TODO Auto-generated method stub
        return R.layout.activity_add_race;
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        ImageView backgroundImg = (ImageView) findViewById(R.id.background_img);
        backgroundImg.setImageResource(R.drawable.img_add_race_bg);

        mDf = new SimpleDateFormat("dd-MM-yyyy");
        mBotLeftBtnTxt.setVisibility(View.VISIBLE);
        mBotRightBtnTxt.setVisibility(View.VISIBLE);
        mBottomBtnLayout.setBackgroundColor(getResources().getColor(
                R.color.text_button_bg_add_race));
        mShoeTxt = (TextView) findViewById(R.id.add_race_my_shoes_txt);
        mRaceDateTxt = (TextView) findViewById(R.id.add_race_date_txt);
        mRaceNameEdt = (EditText) findViewById(R.id.add_race_name_edt);
        mRaceCityEdt = (EditText) findViewById(R.id.add_race_city_edt);
        mRaceStateEdt = (EditText) findViewById(R.id.add_race_state_edt);
        mRaceTypeTxt = (TextView) findViewById(R.id.add_race_event_type_txt);
        mRaceWebsiteEdt = (EditText) findViewById(R.id.add_race_website_edt);
        mRaceFinishTimeTxt = (TextView) findViewById(R.id.add_race_finish_time_txt);

        // Image
        mBidImg = (ImageView) findViewById(R.id.add_race_photo_of_bib);
        mMedalImg = (ImageView) findViewById(R.id.add_race_photo_of_medal);
        mPersonImg = (ImageView) findViewById(R.id.add_race_photo_of_person);

        /*lnGroup = (LinearLayout) findViewById(R.id.races_add_other_layout);
        rdGroup = (RadioGroup) findViewById(R.id.races_radio_group_other);
        edtRaceMile = (EditText) findViewById(R.id.add_race_other_add_miles_edt);
        rdKm= (RadioButton)findViewById(R.id.race_km);
        rdMile= (RadioButton)findViewById(R.id.race_mile);
        edtRaceMile.setHint("Enter Length(km)");
        rdKm.setChecked(true);
        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.race_km) {
                    edtRaceMile.setHint("Enter Length(km)");
                } else if (checkedId == R.id.race_mile) {
                    edtRaceMile.setHint("Enter Length(mile)");
                }
            }
        });
        lnGroup.setVisibility(View.GONE);*/
        fillRaceUpdate();
		/*if (mRace != null) {

			int finishTime = (Integer) mRace.get(Constants.FINISHTIME);
			String finishTimeStr = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(finishTime),
					TimeUnit.MILLISECONDS.toMinutes(finishTime)
							% TimeUnit.HOURS.toMinutes(1),
					TimeUnit.MILLISECONDS.toSeconds(finishTime)
							% TimeUnit.MINUTES.toSeconds(1));
			int raceType = (Integer) mRace.get(Constants.EVENTTYPE);
			String rareTypeStr = getRaceTypeText(raceType);
			if (mRace.containsKey(Constants.SHOE.toUpperCase())) {

				mShoe = (ParseObject) mRace.get(Constants.SHOE.toUpperCase());
				mShoe.fetchIfNeededInBackground(new GetCallback<ParseObject>() {

					@Override
					public void done(ParseObject arg0, ParseException arg1) {
						// TODO Auto-generated method stub

						if (arg1 == null) {
							mShoeTxt.setText(String.format("%s (%s)",
									arg0.get(Constants.BRAND),
									arg0.get(Constants.MODEL)));
						} else {

							mShoe = null;
						}
					}
				});
			}

			if (mRace.containsKey(Constants.MEDAL)
					|| mRace.containsKey(Constants.BIB)
					|| mRace.containsKey(Constants.PERSON)) {

				new LoadRaceImageAsync().execute();
			}

			mRaceTypeTxt.setTag(raceType);
			mRaceFinishTimeTxt.setTag(finishTime / 1000);
			Date raceDate = (Date) mRace.get(Constants.RACEDATE);
			mRaceTypeTxt.setText(rareTypeStr);
			mRaceFinishTimeTxt.setText(finishTimeStr);
			mRaceDateTxt.setText(mDf.format(raceDate));
			mRaceCityEdt.setText((String) mRace.get(Constants.CITY));
			mRaceStateEdt.setText((String) mRace.get(Constants.STATE));
			mRaceNameEdt.setText((String) mRace.get(Constants.RACENAME));
			mRaceWebsiteEdt.setText((String) mRace.get(Constants.WEBSITE));
		}*/

        mShoeTxt.setOnClickListener(this);
        mBidImg.setOnClickListener(this);
        mMedalImg.setOnClickListener(this);
        mPersonImg.setOnClickListener(this);
        mRaceTypeTxt.setOnClickListener(this);
        mRaceDateTxt.setOnClickListener(this);
        mRaceFinishTimeTxt.setOnClickListener(this);
    }

    private void fillRaceUpdate() {
        if (mRaceUpdate != null) {
            int finishTime = 0;
            String finish = "00:00:00";
            try {
                String[] time = mRaceUpdate.getFinisherTime().split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);
                int seconds = Integer.parseInt(time[2]);
                int finishedSeconds = ((hour * 60 * 60) + (minute * 60) + seconds);
                mRaceFinishTimeTxt.setText(mRaceUpdate.getFinisherTime());
                mRaceFinishTimeTxt.setTag(finishedSeconds);
                //String finishTimeStr = String.format("%02d:%02d:%02d",
            } catch (Exception ex) {
                //ex.printStackTrace();
            }

            int raceType = (Integer) mRaceUpdate.getEvenType();
            String rareTypeStr = getRaceTypeText(raceType, mRaceUpdate);
            mRaceTypeTxt.setTag(raceType);
            mRaceTypeTxt.setText(rareTypeStr);


            String raceDate = "2015-04-30";
            try {
                String date = mRaceUpdate.getRaceDate().substring(0, 10);
                String[] dateSplit = date.split("-");
                raceDate = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];
                raceDate = Utilities.getDateTimeEachRace(date);
            } catch (Exception ex) {
                //ex.printStackTrace();
            }

            mRaceDateTxt.setText(raceDate);
            mRaceCityEdt.setText(mRaceUpdate.getCity());
            mRaceStateEdt.setText(mRaceUpdate.getState());
            mRaceNameEdt.setText(mRaceUpdate.getName());
            mRaceWebsiteEdt.setText(mRaceUpdate.getWebsite());

            Integer shoeId = mRaceUpdate.getShoeID();
            mShoeTxt.setTag(shoeId);
            Shoe shoeRace = mRaceUpdate.getShoe();
            if(shoeRace != null){
                mShoeTxt.setText(String.format("%s (%s)", shoeRace.getBrand(), shoeRace.getModel()));
            }
            else{
                mShoeTxt.setText("");
            }


            //set path for image
            mRaceMedalPath = mRaceUpdate.getMedalUrl();
            mRaceBibPath = mRaceUpdate.getBibUrl();
            mRacePersonPath = mRaceUpdate.getPersonUrl();
            if((mRaceUpdate.getMedalUrl() != null) && (!mRaceUpdate.getMedalUrl().isEmpty())){
                /*mOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(R.dimen.image_round_conner_new))
                        .showImageOnLoading(R.drawable.ic_photo_of_bib)
                        .showImageForEmptyUri(R.drawable.ic_photo_of_bib)
                        .showImageOnFail(R.drawable.ic_photo_of_bib).cacheInMemory(true)
                        .cacheOnDisc(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888).build();*/
                //mImageLoader.displayImage(ServiceApi.SERVICE_URL + mRaceUpdate.getMedalUrl(), mMedalImg, mOptions);
                //Bitmap bitmap = mImageLoader.loadImageSync(ServiceApi.SERVICE_URL + mRaceUpdate.getMedalUrl());

                mImageLoaderMedal.loadImage(ServiceApi.SERVICE_URL + mRaceUpdate.getMedalUrl(), new SimpleImageLoadingListener()
                {
                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {

                    }

                    @Override
                    public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage)
                    {
                        // Do whatever you want with Bitmap
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Bitmap bmp = Utilities.getRoundedCornerBitmap(loadedImage, 14);
                                    if(bmp != null) {
                                        LogUtil.d(Constants.LOG_TAG, "Loaded bitmap Medal url|bitmap: " + imageUri + "|" + bmp.toString());
                                        mMedalImg.setImageBitmap(bmp);
                                    }
                                    else{
                                        LogUtil.d(Constants.LOG_TAG, "Load bitmap Medal NULL url|bitmap: " + imageUri);
                                    }
                                }
                                catch (Exception ex){
                                }
                            }
                        });

                    }
                });


            }
            if((mRaceUpdate.getBibUrl() != null) && (!mRaceUpdate.getBibUrl().isEmpty())){
                /*mOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(R.dimen.image_round_conner_new))
                        .showImageOnLoading(R.drawable.ic_photo_of_medal)
                        .showImageForEmptyUri(R.drawable.ic_photo_of_medal)
                        .showImageOnFail(R.drawable.ic_photo_of_medal).cacheInMemory(true)
                        .cacheOnDisc(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888).build();*/
                //mImageLoader.displayImage(ServiceApi.SERVICE_URL + mRaceUpdate.getBibUrl(), mBidImg, mOptions);

                mImageLoader.loadImage(ServiceApi.SERVICE_URL + mRaceUpdate.getBibUrl(), new SimpleImageLoadingListener()
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
                                    mBidImg.setImageBitmap(bmp);
                                }
                                catch (Exception ex){
                                }
                            }
                        });

                    }
                });
            }
            if((mRaceUpdate.getPersonUrl() != null) && (!mRaceUpdate.getPersonUrl().isEmpty())){
                /*mOptions = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(R.dimen.image_round_conner_new))
                        .showImageOnLoading(R.drawable.ic_photo_of_person)
                        .showImageForEmptyUri(R.drawable.ic_photo_of_person)
                        .showImageOnFail(R.drawable.ic_photo_of_person).cacheInMemory(true)
                        .cacheOnDisc(true).considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888).build();*/
                //mImageLoader.displayImage(ServiceApi.SERVICE_URL + mRaceUpdate.getPersonUrl(), mPersonImg, mOptions);
                mImageLoaderPerson.loadImage(ServiceApi.SERVICE_URL + mRaceUpdate.getPersonUrl(), new SimpleImageLoadingListener()
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
                                    mPersonImg.setImageBitmap(bmp);
                                }
                                catch (Exception ex){
                                }
                            }
                        });

                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.REQUETS_CODE_ADD_SHOE) {

                int selected = data.getIntExtra(
                        Constants.INTENT_SELECT_SHOE_FOR_RACE, -1);
                String json = data.getStringExtra(Constants.INTENT_SELECT_SHOE_ID_FOR_RACE);
                Shoe shoeForRace = null;
                try {
                    Gson gson = new Gson();
                    shoeForRace = gson.fromJson(json, Shoe.class);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
                mShoeTxt.setText(String.format("%s (%s)",
                        shoeForRace.getBrand(), shoeForRace.getModel()));
                mShoeTxt.setTag(new Integer(shoeForRace.getId()));
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_BID) {

                Uri imageUri = data.getData();
                Utilities.startCropImage(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_BID,
                        imageUri);
                mRaceBibUpload = imageUri;
                LogUtil.d(mCurrentClassName, "Choose image: " + mRaceBibUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_MEDAL) {

                Uri imageUri = data.getData();
                Utilities.startCropImage(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_MEDAL,
                        imageUri);
                mRaceMedalUpload = imageUri;
                LogUtil.d(mCurrentClassName, "Choose image: " + mRaceMedalUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_PERSON) {

                Uri imageUri = data.getData();
                Utilities.startCropImage(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_PERSON,
                        imageUri);
                mRacePersonUpload = imageUri;
                LogUtil.d(mCurrentClassName, "Choose image: " + mRacePersonUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_BID) {

                Uri imageUri = Utilities.createImage();
                Utilities.startCropImage(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_BID,
                        imageUri);
                mRaceBibUpload = imageUri;
                LogUtil.d(mCurrentClassName, "Take image: " + mRaceBibUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_MEDAL) {

                Uri imageUri = Utilities.createImage();
                Utilities.startCropImage(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_MEDAL,
                        imageUri);
                mRaceMedalUpload = imageUri;
                LogUtil.d(mCurrentClassName, "Take image: " + mRaceMedalUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_PERSON) {

                Uri imageUri = Utilities.createImage();
                Utilities.startCropImage(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_PERSON,
                        imageUri);
                mRacePersonUpload = imageUri;
                LogUtil.d(mCurrentClassName, "Take image: " + mRacePersonUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_BID) {

                Bundle extras = data.getExtras();
                Bitmap imgBmp = extras.getParcelable("data");
                mBidImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
                        imgBmp,
                        getResources().getDimensionPixelSize(
                                R.dimen.image_round_conner)));
                mBidImg.setTag(imgBmp);
                mRaceBibUpload = getImageUri(this, imgBmp);
                LogUtil.d(mCurrentClassName, "Crop image: " + mRaceBibUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_MEDAL) {

                Bundle extras = data.getExtras();
                Bitmap imgBmp = extras.getParcelable("data");
                mMedalImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
                        imgBmp,
                        getResources().getDimensionPixelSize(
                                R.dimen.image_round_conner)));
                mMedalImg.setTag(imgBmp);
                mRaceMedalUpload = getImageUri(this, imgBmp);
                LogUtil.d(mCurrentClassName, "Crop image: " + mRaceMedalUpload);
            } else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_PERSON) {

                Bundle extras = data.getExtras();
                Bitmap imgBmp = extras.getParcelable("data");
                mPersonImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
                        imgBmp,
                        getResources().getDimensionPixelSize(
                                R.dimen.image_round_conner)));
                mPersonImg.setTag(imgBmp);
                mRacePersonUpload = getImageUri(this, imgBmp);
                LogUtil.d(mCurrentClassName, "Crop image: " + mRacePersonUpload);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public static void setRace(HashMap<String, Object> race) {

        mRace = race;
    }

    public static void setRaceUpdate(Race race) {

        mRaceUpdate = race;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute,
                          int seconds) {
        // TODO Auto-generated method stub

        String chooseTime = String.format("%02d:%02d:%02d", hourOfDay, minute, seconds);
        mRaceFinishTimeTxt.setText(chooseTime);
        int finishedSeconds = ((hourOfDay * 60 * 60) + (minute * 60) + seconds);
        LogUtil.d(Constants.LOG_TAG, "Choose time: " + chooseTime + "|" + finishedSeconds);
        mRaceFinishTimeTxt.setTag(finishedSeconds);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.add_race_date_txt:

                Calendar calendar = Calendar.getInstance();
                if (!mRaceDateTxt.getText().toString().equals("")) {

                    String dateStr = mRaceDateTxt.getText().toString();
                    try {

                        //Date raceDate = mDf.parse(dateStr);
                        Date raceDate = Utilities.getDateFromString(dateStr, "MMM dd, yyyy");
                                calendar.setTime(raceDate);
                    } catch (Exception e) {

                        LogUtil.e("add_race_date_txt", e.getMessage());
                    }
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddRaceActivity.this, null, year, month, day);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                datePickerDialog.cancel();
                                datePickerDialog.dismiss();
                            }
                        });
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.done),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                DatePicker datePicker = datePickerDialog
                                        .getDatePicker();
                                /*String raceDate = String.format("%s-%s-%s",
                                        datePicker.getDayOfMonth(),
                                        (datePicker.getMonth() + 1),
                                        datePicker.getYear());*/
                                String str =Utilities.getDateTimeAddRace(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                                String raceDate = Utilities.getDateTimeEachRace(str);

                                mRaceDateTxt.setText(raceDate);
                                datePickerDialog.dismiss();
                            }
                        });
                datePickerDialog.show();
                break;
            case R.id.add_race_finish_time_txt:

                String finishedTimeStr = mRaceFinishTimeTxt.getText().toString();
                int hour = 0;
                int min = 0;
                int sec = 0;
                if (!finishedTimeStr.equals("")) {

                    String[] finishedTimes = finishedTimeStr.split(":");
                    hour = Integer.valueOf(finishedTimes[0]);
                    min = Integer.valueOf(finishedTimes[1]);
                    sec = Integer.valueOf(finishedTimes[2]);
                }

                new MyTimePickerDialog(AddRaceActivity.this, this, hour, min, sec,
                        true).show();
                break;
            case R.id.add_race_my_shoes_txt:

                Intent selectShoeIntent = new Intent(AddRaceActivity.this,
                        MyShoesActivity.class);
                selectShoeIntent.putExtra(Constants.INTENT_SELECT_SHOE_FOR_RACE,
                        true);
                startActivityForResult(selectShoeIntent,
                        Constants.REQUETS_CODE_ADD_SHOE);
                break;
            case R.id.add_race_event_type_txt:

                showEventTypeChooser();
                break;
            case R.id.add_race_photo_of_bib:

                Utilities.showPickerImageDialog(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_BID,
                        Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_BID);
                break;

            case R.id.add_race_photo_of_person:

                Utilities.showPickerImageDialog(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_PERSON,
                        Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_PERSON);
                break;

            case R.id.add_race_photo_of_medal:

                Utilities.showPickerImageDialog(AddRaceActivity.this,
                        Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_MEDAL,
                        Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_MEDAL);
                break;
            case R.id.bottom_button_right_text:

                if (mRaceNameEdt.getText().toString().trim().equals("" + "" + "")
                        || mRaceDateTxt.getText().toString().equals("")
                        || mRaceFinishTimeTxt.getText().toString().equals("")
                        || mRaceTypeTxt.getText().toString().equals("")) {

                    Utilities.showAlertMessage(AddRaceActivity.this,
                            getString(R.string.dialog_add_shoe_fill_all_fields),
                            getString(R.string.dialog_add_race_tile));
                } else {
                    //call add Race
                    //new AddRaceAsync().execute();
                    if(mLoadingDialog == null) {
                        mLoadingDialog = CustomLoadingDialog.show(AddRaceActivity.this, "", "", false, false);
                    }
                    uploadedRaceImage = 0;
                    if(mRaceMedalUpload != null){
                        uploadedRaceImage = 1;
                        uploadMedalImage();
                    }
                    else if(mRaceBibUpload != null){
                        uploadedRaceImage = 2;
                        uploadBibImage();
                    }
                    else if(mRacePersonUpload != null){
                        uploadedRaceImage = 3;
                        uploadPersonImage();
                    }
                    else{
                        callAddUpdateRace();
                    }

                    //callAddUpdateRace();
                }
                break;
            /*case R.id.add_race_event_type_txt:
                showInputRaceMileDialog();
                break;*/
        }
    }


    /**
     * Show dialog allows user input email address to reset password
     * */
    private void showInputRaceMileDialog() {

        final Dialog dialog = new Dialog(AddRaceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialod_race_input_mile);
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
                    String miles = emailEdt.getText().toString();
                    try{
                        float raceMile = Float.parseFloat(miles);
                        //String str = String.format("0.02", miles);
                        mRaceTypeTxt.setTag(Constants.SELECT_RACE_OTHER);
                        String strRaceMiles= miles + " MI";
                        mRaceTypeTxt.setText(strRaceMiles);
                        dialog.dismiss();
                    }
                    catch (Exception ex){
                        dialog.dismiss();
                        Utilities
                                .showAlertMessage(
                                        AddRaceActivity.this,
                                        getString(R.string.race_input_mile_invalid),
                                        getString(R.string.race_input_mile_invalid));
                    }
                    /*if(Utilities.isValidEmail(email)){
                        callSendMailForgotPassword(email);
                    } else {

                        //dialog.dismiss();
                    }*/
                }
            }
        });

        dialog.show();
    }

    private void uploadBibImage() {
        UploadImageRequest request = new UploadImageRequest(mRaceBibUpload);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    private void uploadMedalImage() {

        UploadImageRequest request = new UploadImageRequest(mRaceMedalUpload);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    private void uploadPersonImage() {
        UploadImageRequest request = new UploadImageRequest(mRacePersonUpload);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    /**
     * Show event type chooser
     */
    private void showEventTypeChooser() {

        final Dialog dialog = new Dialog(AddRaceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_event_type);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        TextView type5k = (TextView) dialog
                .findViewById(R.id.dialog_choose_event_5k);
        TextView type10k = (TextView) dialog
                .findViewById(R.id.dialog_choose_event_10k);
        TextView type15k = (TextView) dialog
                .findViewById(R.id.dialog_choose_event_15k);
        TextView typeHalfMar = (TextView) dialog
                .findViewById(R.id.dialog_choose_event_half_mar);
        TextView typeFullMar = (TextView) dialog
                .findViewById(R.id.dialog_choose_event_full_mar);
        TextView typeOther = (TextView) dialog
                .findViewById(R.id.dialog_choose_event_other);
        ChooseEventTypeItemClick itemClick = new ChooseEventTypeItemClick(
                dialog);

        // Set tags
		/*type5k.setTag(0);
		type10k.setTag(1);
		type15k.setTag(2);
		typeHalfMar.setTag(3);
		typeFullMar.setTag(4);*/

        type5k.setTag(1);
        type10k.setTag(2);
        type15k.setTag(3);
        typeHalfMar.setTag(4);
        typeFullMar.setTag(5);
        typeOther.setTag(6);


        type5k.setOnClickListener(itemClick);
        type10k.setOnClickListener(itemClick);
        type15k.setOnClickListener(itemClick);
        typeHalfMar.setOnClickListener(itemClick);
        typeFullMar.setOnClickListener(itemClick);
        typeOther.setOnClickListener(itemClick);
        dialog.show();
    }

    private String getRaceTypeText(int raceType, Race race) {

        String rareTypeStr = "";
        switch (raceType) {
            case Constants.SELECT_RACE_5K:

                rareTypeStr = getString(R.string.add_race_type_5k);
                break;
            case Constants.SELECT_RACE_10K:

                rareTypeStr = getString(R.string.add_race_type_10k);
                break;
            case Constants.SELECT_RACE_15K:

                rareTypeStr = getString(R.string.add_race_type_15k);
                break;
            case Constants.SELECT_RACE_HALF_MAR:

                rareTypeStr = getString(R.string.add_race_type_half_mar);
                break;
            case Constants.SELECT_RACE_FULL_MAR:

                rareTypeStr = getString(R.string.add_race_type_full_mar);
                break;
            case Constants.SELECT_RACE_OTHER:
                rareTypeStr = "0.00 MI";
                if(race != null){
                    rareTypeStr = race.getRaceMiles() + " MI";
                }
                break;
        }
        return rareTypeStr;
    }

    private class ChooseEventTypeItemClick implements OnClickListener {

        private Dialog dialog;

        public ChooseEventTypeItemClick(Dialog dialog) {
            super();
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            int selectedItem = (Integer) v.getTag();
            String rareTypeStr = getRaceTypeText(selectedItem, mRaceUpdate);
            if(selectedItem == Constants.SELECT_RACE_OTHER){
                this.dialog.dismiss();
                this.dialog = null;
                //mRaceTypeTxt.setText("0.00 MI");
                mRaceTypeTxt.setTag(selectedItem);
                showInputRaceMileDialog();
                /*new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showInputRaceMileDialog();
                    }
                }, 500);*/
            }
            else{
                mRaceTypeTxt.setText(rareTypeStr);
                mRaceTypeTxt.setTag(selectedItem);
                dialog.dismiss();
            }

            /*if(selectedItem == Constants.SELECT_RACE_OTHER){
                lnGroup.setVisibility(View.VISIBLE);
            }
            else{
                lnGroup.setVisibility(View.GONE);
            }*/

        }
    }


    private void callAddUpdateRace() {
        String bibUrl = mRaceBibPath;
        String medalUrl = mRaceMedalPath;
        String personUrl = mRacePersonPath;

        String city = mRaceCityEdt.getText().toString();
        String eventType = String.valueOf((Integer) mRaceTypeTxt.getTag());
        String finisherDateTime = String.valueOf((Integer) mRaceFinishTimeTxt.getTag());
        String finisherDateTimeStr = mRaceFinishTimeTxt.getText().toString();
        LogUtil.d(Constants.LOG_TAG, "finisherDateTimeStr add: " + finisherDateTimeStr);
        String raceName = mRaceNameEdt.getText().toString();


        String raceDate = mRaceDateTxt.getText().toString();
        try {
            /*String[] time = raceDate.split("-");
            raceDate = time[2] + "-" + time[1] + "-" + time[0];
            LogUtil.d(Constants.LOG_TAG, "race date 1: " + raceDate);*/
            raceDate = Utilities.getDateTimeStringAddRace(raceDate);
            LogUtil.d(Constants.LOG_TAG, "race date 2: " + raceDate);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        String shoesId = null;
        if(mShoeTxt.getTag() != null){
            shoesId = String.valueOf((Integer) mShoeTxt.getTag());
        }
        String state = mRaceStateEdt.getText().toString();
        String website = mRaceWebsiteEdt.getText().toString();


        if (mRaceUpdate != null) {
            LogUtil.d(Constants.LOG_TAG, "Shoe info to updateadd: " + "bibURl: " + bibUrl + "|"
                    + "city: " + city + "|"+ " Event type: " + eventType
                    + "finisherDateTime: " + finisherDateTimeStr + "|"
                    + "medalUrl: " + medalUrl + "|"+ "raceName: " + raceName + "|"
                    + "personUrl: " + personUrl + "|"+ "raceDate: " + raceDate + "|"
                    + "shoesId: " + shoesId + "|" + "state: " + state + "|"
                    + "website: " + website + "|");
            //Toast.makeText(this,"shoesId update: " + shoesId, Toast.LENGTH_LONG).show();
            UpdateRaceRequest request = null;
            if((Integer) mRaceTypeTxt.getTag() == Constants.SELECT_RACE_OTHER){
                String raceMiles = "0.00";
                if(!mRaceTypeTxt.getText().toString().isEmpty()){
                    int len = mRaceTypeTxt.getText().toString().length();
                    String realMile = mRaceTypeTxt.getText().toString().substring(0, len - 3);
                    raceMiles = realMile.trim();
                }
                LogUtil.d(Constants.LOG_TAG, "Race mile update: "+ raceMiles);
                request = new UpdateRaceRequest(bibUrl, city, mRaceUpdate.getCreatedAt(), eventType,
                        finisherDateTime, String.valueOf(mRaceUpdate.getId()), medalUrl, raceName, personUrl,
                        raceDate, shoesId, state, mRaceUpdate.getUpdatedAt(),
                        String.valueOf(mRaceUpdate.getUserID()), website, raceMiles);
            }
            else{
                request = new UpdateRaceRequest(bibUrl, city, mRaceUpdate.getCreatedAt(), eventType,
                        finisherDateTime, String.valueOf(mRaceUpdate.getId()), medalUrl, raceName, personUrl,
                        raceDate, shoesId, state, mRaceUpdate.getUpdatedAt(),
                        String.valueOf(mRaceUpdate.getUserID()), website);
            }

            request.setListener(callBackEvent);
            new Thread(request).start();
        } else {
            LogUtil.d(Constants.LOG_TAG, "Shoe infor to add: " + "bibURl: " + bibUrl + "|"
                    + "city: " + city + "|"+ " Event type: " + eventType
                    + "finisherDateTime: " + finisherDateTimeStr + "|"
                    + "medalUrl: " + medalUrl + "|"+ "raceName: " + raceName + "|"
                    + "personUrl: " + personUrl + "|"+ "raceDate: " + raceDate + "|"
                    + "shoesId: " + shoesId + "|" + "state: " + state + "|"
                    + "website: " + website + "|");
            //Toast.makeText(this,"shoesId add: " + shoesId, Toast.LENGTH_LONG).show();
            AddRaceRequest request = null;
            if((Integer) mRaceTypeTxt.getTag() == Constants.SELECT_RACE_OTHER){
                String raceMiles = "0.00";
                if(!mRaceTypeTxt.getText().toString().isEmpty()){
                    int len = mRaceTypeTxt.getText().toString().length();
                    String realMile = mRaceTypeTxt.getText().toString().substring(0, len - 3);
                    raceMiles = realMile.trim();
                }
                LogUtil.d(Constants.LOG_TAG, "Race mile add: "+ raceMiles);
                request =  new AddRaceRequest(bibUrl, city, eventType, finisherDateTime, medalUrl,
                        raceName, personUrl, raceDate, shoesId, state, website, raceMiles);
            }
            else{
                request =  new AddRaceRequest(bibUrl, city, eventType, finisherDateTime, medalUrl,
                        raceName, personUrl, raceDate, shoesId, state, website);
            }
            request.setListener(callBackEvent);
            new Thread(request).start();

        }

    }

    private void processAddRace(String data) throws JSONException {
        JSONObject obj = new JSONObject(data.toString());
        String result = obj.getString("result");
        LogUtil.d(mCurrentClassName, "Response Add Race: " + result);
        if (result.equalsIgnoreCase("true")) {
            Intent resultIntent = new Intent("addRaceCallBackSucceed");
            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();
        } else {
            Utilities.showAlertMessage(
                    AddRaceActivity.this,
                    getString(R.string.race_add_failed),
                    "");
            LogUtil.d(mCurrentClassName, "Response Add Shoe Failed responde " + data);
            /*Intent resultIntent = new Intent("addRaceCallBackFailed");
            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();*/
        }
    }

    private void processUpdateRace(String data) throws JSONException {
        JSONObject obj = new JSONObject(data.toString());
        String result = obj.getString("result");
        LogUtil.d(mCurrentClassName, "Response Add Race: " + result);
        if (result.equalsIgnoreCase("true")) {
            Intent resultIntent = new Intent("updateRaceCallBackSucceed");
            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();
        } else {
            Utilities.showAlertMessage(
                    AddRaceActivity.this,
                    getString(R.string.race_update_failed),
                    "");
            LogUtil.d(mCurrentClassName, "Response Update Shoe Failed responde " + data);
            /*LogUtil.d(mCurrentClassName, "Response Add Shoe Failed ");
            Intent resultIntent = new Intent("updateRaceCallBackFailed");
            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();*/
        }
    }

    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog = CustomLoadingDialog.show(SignInActivity.this, "", "", false, false);
                }
            });*/
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, final Object data) {
            LogUtil.i(Constants.LOG_TAG, data.toString());
            if (methodName.equals(ServiceConstants.METHOD_ADD_RACE)) {

                // added race success
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processAddRace(data.toString());
                        } catch (Exception e) {
                            //Toast.makeText(AddRaceActivity.this,"shoesId add failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Utilities.showAlertMessage(AddRaceActivity.this,
                                    getString(R.string.race_add_failed), "");
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


            } else if (methodName.equals(ServiceConstants.METHOD_UPDATE_RACE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processUpdateRace(data.toString());
                        } catch (Exception e) {
                            //Toast.makeText(AddRaceActivity.this,"shoesId update failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Utilities.showAlertMessage(AddRaceActivity.this,
                                    getString(R.string.race_update_failed), "");
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
                            switch(uploadedRaceImage){
                                case 1:
                                    mRaceMedalPath = result;
                                    LogUtil.d(Constants.LOG_TAG, "upload medal: " + result);
                                    if(mRaceBibUpload != null) {
                                        uploadedRaceImage = 2;
                                        uploadBibImage();
                                    }
                                    else if(mRacePersonUpload != null) {
                                        uploadedRaceImage = 3;
                                        uploadPersonImage();
                                    }
                                    else{
                                        uploadedRaceImage = 4;
                                    }

                                    break;

                                case 2:
                                    mRaceBibPath = result;
                                    LogUtil.d(Constants.LOG_TAG, "upload bib: " + result);
                                    if(mRacePersonUpload != null) {
                                        uploadedRaceImage = 3;
                                        uploadPersonImage();
                                    }
                                    else{
                                        uploadedRaceImage = 4;
                                    }
                                    break;
                                case 3:
                                    mRacePersonPath = result;
                                    LogUtil.d(Constants.LOG_TAG, "upload person: " + result);
                                    uploadedRaceImage = 4;
                                    break;
                            }
                            if(uploadedRaceImage > 3){
                                callAddUpdateRace();
                            }
                        } catch (Exception e) {
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                            }
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





	/*private class LoadRaceImageAsync extends AsyncTask<Void, Void, Void> {

		private Dialog dialog;
		private Bitmap medalBmp, bibBmp, personBmp;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(AddRaceActivity.this, "", "",
					false, false);
		}

		@Override
		protected Void doInBackground(Void... paramVarArgs) {
			// TODO Auto-generated method stub
			if (mRace.containsKey(Constants.MEDAL)) {

				try {

					ParseObject object = (ParseObject) mRace
							.get(Constants.MEDAL);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					medalBmp = bmp;
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			if (mRace.containsKey(Constants.BIB)) {

				try {

					ParseObject object = (ParseObject) mRace.get(Constants.BIB);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					bibBmp = bmp;
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			if (mRace.containsKey(Constants.PERSON)) {

				try {

					ParseObject object = (ParseObject) mRace
							.get(Constants.PERSON);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					personBmp = bmp;
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (bibBmp != null) {

				mBidImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
						bibBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner)));
				mBidImg.setTag(bibBmp);
			}

			if (medalBmp != null) {

				mMedalImg.setImageBitmap((Utilities.getRoundedCornerBitmap(
						medalBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner))));
				mMedalImg.setTag(medalBmp);
			}

			if (personBmp != null) {

				mPersonImg.setImageBitmap((Utilities.getRoundedCornerBitmap(
						personBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner))));
				mPersonImg.setTag(personBmp);
			}

			dialog.dismiss();
		}
	}

	private class AddRaceAsync extends AsyncTask<Void, Void, Void> {

		Dialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(AddRaceActivity.this, "", "",
					false, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (mRace != null) {

				mHistory.remove(mRace);
			}

			HashMap<String, Object> raceInfo = new HashMap<String, Object>();
			raceInfo.put(Constants.WEBSITE, mRaceWebsiteEdt.getText()
					.toString());
			raceInfo.put(Constants.FINISHTIME,
					(Integer) mRaceFinishTimeTxt.getTag());
			raceInfo.put(Constants.CITY, mRaceCityEdt.getText().toString());
			raceInfo.put(Constants.STATE, mRaceStateEdt.getText().toString());
			raceInfo.put(Constants.RACENAME, mRaceNameEdt.getText().toString());
			raceInfo.put(Constants.EVENTTYPE, (Integer) mRaceTypeTxt.getTag());
			try {

				Date raceDate = mDf.parse(mRaceDateTxt.getText().toString());
				raceInfo.put(Constants.RACEDATE, raceDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				LogUtil.e("AddRaceAsync", e.getMessage());
			}

			// Bid image
			Bitmap bitmap = (Bitmap) mBidImg.getTag();
			if (bitmap != null) {

				ParseObject object = new ParseObject(Constants.IMAGE);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] bitmapdata = stream.toByteArray();
				ParseFile file = new ParseFile(bitmapdata);
				try {

					object.put(Constants.DATA.toUpperCase(), file);
					raceInfo.put(Constants.BIB, object);
					object.save();
				} catch (ParseException e) {

					LogUtil.e("Save image", e.getMessage());
				}
			}

			// Medal image
			bitmap = null;
			bitmap = (Bitmap) mMedalImg.getTag();
			if (bitmap != null) {

				ParseObject object = new ParseObject(Constants.IMAGE);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] bitmapdata = stream.toByteArray();
				ParseFile file = new ParseFile(bitmapdata);
				try {

					object.put(Constants.DATA.toUpperCase(), file);
					raceInfo.put(Constants.MEDAL, object);
					object.save();
				} catch (ParseException e) {

					LogUtil.e("Save image", e.getMessage());
				}
			}

			// Person image
			bitmap = null;
			bitmap = (Bitmap) mPersonImg.getTag();
			if (bitmap != null) {

				ParseObject object = new ParseObject(Constants.IMAGE);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] bitmapdata = stream.toByteArray();
				ParseFile file = new ParseFile(bitmapdata);
				try {

					object.put(Constants.DATA.toUpperCase(), file);
					raceInfo.put(Constants.PERSON, object);
					object.save();
				} catch (ParseException e) {

					LogUtil.e("Save image", e.getMessage());
				}
			}

			// Shoe
			if (mShoe != null) {

				float oriDis = (float) mShoe.getDouble(Constants.DISTANCE);
				int raceType = (Integer) mRaceTypeTxt.getTag();
				switch (raceType) {
				case Constants.SELECT_RACE_5K:

					oriDis += 3.1f;
					break;

				case Constants.SELECT_RACE_10K:

					oriDis += 6.2f;
					break;

				case Constants.SELECT_RACE_15K:

					oriDis += 9.3f;
					break;

				case Constants.SELECT_RACE_HALF_MAR:

					oriDis += 13.1;
					break;

				case Constants.SELECT_RACE_FULL_MAR:

					oriDis += 26.2;
					break;
				}

				mShoe.put(Constants.DISTANCE, oriDis);
				try {

					mShoe.save();
					raceInfo.put(Constants.SHOE.toUpperCase(), mShoe);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					LogUtil.e("Save image", e.getMessage());
				}
			}

			mHistory.add(raceInfo);
			mUser.put(Constants.DATA, mHistory);
			mUser.put(Constants.SHOES, mShoes);
			try {

				mUser.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				LogUtil.e("Save image", e.getMessage());
			}

			mRace = null;
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
