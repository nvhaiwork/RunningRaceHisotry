package com.runningracehisotry;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.MyTimePickerDialog;
import com.runningracehisotry.views.MyTimePickerDialog.OnTimeSetListener;
import com.runningracehisotry.views.TimePicker;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddRaceRequest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class AddRaceActivity extends BaseActivity implements OnTimeSetListener {

	//private ParseObject mShoe;
	private SimpleDateFormat mDf;
	private static HashMap<String, Object> mRace;
	private EditText mRaceNameEdt, mRaceWebsiteEdt, mRaceCityEdt,
			mRaceStateEdt;
	private TextView mShoeTxt, mRaceDateTxt, mRaceTypeTxt, mRaceFinishTimeTxt;
	private ImageView mBidImg, mPersonImg, mMedalImg;

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
                try{
                    Gson gson = new Gson();
                    shoeForRace = gson.fromJson(json, Shoe.class);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
                mShoeTxt.setText(String.format("%s (%s)",
                        shoeForRace.getBrand(), shoeForRace.getModel()));
                mShoeTxt.setTag(new Integer(shoeForRace.getId()));
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_BID) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(AddRaceActivity.this,
						Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_BID,
						imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_MEDAL) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(AddRaceActivity.this,
						Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_MEDAL,
						imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_CHO0SE_IMAGE_PERSON) {

				Uri imageUri = data.getData();
				Utilities.startCropImage(AddRaceActivity.this,
						Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_PERSON,
						imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_BID) {

				Uri imageUri = Utilities.createImage();
				Utilities.startCropImage(AddRaceActivity.this,
						Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_BID,
						imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_MEDAL) {

				Uri imageUri = Utilities.createImage();
				Utilities.startCropImage(AddRaceActivity.this,
						Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_MEDAL,
						imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_TAKE_IMAGE_PERSON) {

				Uri imageUri = Utilities.createImage();
				Utilities.startCropImage(AddRaceActivity.this,
						Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_PERSON,
						imageUri);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_BID) {

				Bundle extras = data.getExtras();
				Bitmap imgBmp = extras.getParcelable("data");
				mBidImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
						imgBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner)));
				mBidImg.setTag(imgBmp);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_MEDAL) {

				Bundle extras = data.getExtras();
				Bitmap imgBmp = extras.getParcelable("data");
				mMedalImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
						imgBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner)));
				mMedalImg.setTag(imgBmp);
			} else if (requestCode == Constants.REQUETS_CODE_ADD_RACE_IMAGE_CROP_PERSON) {

				Bundle extras = data.getExtras();
				Bitmap imgBmp = extras.getParcelable("data");
				mPersonImg.setImageBitmap(Utilities.getRoundedCornerBitmap(
						imgBmp,
						getResources().getDimensionPixelSize(
								R.dimen.image_round_conner)));
				mPersonImg.setTag(imgBmp);
			}
		}
	}

	public static void setRace(HashMap<String, Object> race) {

		mRace = race;
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute,
			int seconds) {
		// TODO Auto-generated method stub

		mRaceFinishTimeTxt.setText(String.format("%02d:%02d:%02d", hourOfDay,
				minute, seconds));
		int finishedSeconds = ((hourOfDay * 60 * 60) + (minute * 60) + seconds);
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

					Date raceDate = mDf.parse(dateStr);
					calendar.setTime(raceDate);
				} catch (java.text.ParseException e) {

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
							String raceDate = String.format("%s-%s-%s",
									datePicker.getDayOfMonth(),
									(datePicker.getMonth() + 1),
									datePicker.getYear());
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
                callAddRace();
			}
			break;
		}
	}



    /**
	 * Show event type chooser
	 * */
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
		ChooseEventTypeItemClick itemClick = new ChooseEventTypeItemClick(
				dialog);

		// Set tags
		type5k.setTag(0);
		type10k.setTag(1);
		type15k.setTag(2);
		typeHalfMar.setTag(3);
		typeFullMar.setTag(4);

		type5k.setOnClickListener(itemClick);
		type10k.setOnClickListener(itemClick);
		type15k.setOnClickListener(itemClick);
		typeHalfMar.setOnClickListener(itemClick);
		typeFullMar.setOnClickListener(itemClick);
		dialog.show();
	}

	private String getRaceTypeText(int raceType) {

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
			String rareTypeStr = getRaceTypeText(selectedItem);
			mRaceTypeTxt.setText(rareTypeStr);
			mRaceTypeTxt.setTag(selectedItem);
			dialog.dismiss();
		}
	}


    private void callAddRace() {
        String bibUrl ="/bib.png";
        String city = mRaceCityEdt.getText().toString();
        String eventType = String.valueOf((Integer) mRaceTypeTxt.getTag());
        String finisherDateTime = String.valueOf((Integer) mRaceFinishTimeTxt.getTag());
        String finisherDateTimeStr = mRaceFinishTimeTxt.getText().toString();
        String medalUrl = "/medal.png";
        String raceName = mRaceNameEdt.getText().toString();
        String personUrl = "/person.png";
        long time = 0;
        String raceDate = mRaceDateTxt.getText().toString();
        try {
            time = mDf.parse(mRaceDateTxt.getText().toString()).getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String shoesId = String.valueOf((Integer) mShoeTxt.getTag());
        String state = mRaceStateEdt.getText().toString();
        String website = mRaceWebsiteEdt.getText().toString();

        LogUtil.d(Constants.LOG_TAG, "Shoe infor to add: "
                + "bibURl: " + bibUrl + "|"
                + "city: " + city + "|"
                + "finisherDateTime: " + finisherDateTime + "|"
                + "medalUrl: " + medalUrl + "|"
                + "raceName: " + raceName + "|"
                + "personUrl: " + personUrl + "|"
                + "raceDate: " + raceDate + "|"
                + "shoesId: " + shoesId + "|"
                + "state: " + state + "|"
                + "website: " + website + "|"
                + "time: " + time
                );
        AddRaceRequest request = new AddRaceRequest(bibUrl,city,eventType,finisherDateTime,medalUrl,raceName,personUrl,raceDate,shoesId,state,website);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }
    private void processAddRace(String data) throws JSONException{
        JSONObject obj = new JSONObject(data.toString());
        String result = obj.getString("result");
        LogUtil.d(mCurrentClassName, "Response Add Race: " + result);
        if(result.equalsIgnoreCase("true")){
            Intent resultIntent = new Intent("addRaceCallBack");
            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();
        }
        else{
            LogUtil.d(mCurrentClassName, "Response Add Shoe Failed ");
            Intent resultIntent = new Intent("addRaceCallBack");
            setResult(RESULT_OK, resultIntent);
            //dialog.dismiss();
            finish();
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
        public void Wsdl2CodeFinished(String methodName,final Object data) {
            LogUtil.i(Constants.LOG_TAG, data.toString());
            if (methodName.equals(ServiceConstants.METHOD_ADD_RACE)) {

                    // added race success
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          try {
                                              processAddRace(data.toString());
                                          } catch (Exception e) {

                                          } finally {
                                          }
                                      }
                                  });


            }
            else if (methodName.equals(ServiceConstants.METHOD_UPDATE_RACE)) {
                try {
                    // Login success
                    //processGetFriendGroupOfUser(data.toString());
                } catch (Exception e) {

                } finally {
                }

            }


        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

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
