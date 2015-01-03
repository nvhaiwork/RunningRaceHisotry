package com.runningracehisotry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runningracehisotry.adapters.RaceDetailAdapter;
import com.runningracehisotry.adapters.RaceDetailAdapter.OnRaceItemClickListenner;
import com.runningracehisotry.adapters.RaceDetailAdapter.OnRaceItemDelete;
import com.runningracehisotry.adapters.RaceDetailAdapter.OnShareItemClickListenner;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class RacesDetailActivity extends BaseActivity implements
		OnCheckedChangeListener, OnRaceItemClickListenner, OnRaceItemDelete,
		OnShareItemClickListenner {

	private TextView mEmptyText;
	private RadioGroup mSortGroup;
	private ExpandableListView mRaceList;
	private int mSelectedRace, mFriendRace;
	private RaceDetailAdapter mRacesAdapter;
	private Map<String, List<HashMap<String, Object>>> mRacesDetail;

	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_races_detail;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		mSelectedRace = getIntent()
				.getIntExtra(Constants.INTENT_SELECT_RACE, 0);
		mFriendRace = getIntent().getIntExtra(
				Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
		int raceColor = 0;
		int sortItemBg = 0;
		int titleImage = 0;
		int listImages = 0;
		int shareButton = 0;
		int sortGroupBg = 0;
		int listTimeImg = 0;
		ColorStateList sortItemColor = null;
		switch (mSelectedRace) {
		case Constants.SELECT_RACE_5K:

			sortItemColor = getResources().getColorStateList(
					R.color.races_detail_sort_text_5k);
			shareButton = R.drawable.ic_race_share_5k;
			listTimeImg = R.drawable.ic_race_detail_time_5k;
			listImages = R.drawable.ic_race_detail_images_5k;
			titleImage = R.drawable.ic_races_detail_title_5k;
			sortItemBg = R.drawable.races_detail_sort_item_5k_bg;
			sortGroupBg = R.drawable.races_detail_sort_group_5k_bg;
			raceColor = getResources().getColor(R.color.text_button_bg_5k);
			break;
		case Constants.SELECT_RACE_10K:

			sortItemColor = getResources().getColorStateList(
					R.color.races_detail_sort_text_10k);
			shareButton = R.drawable.ic_race_share_10k;
			listTimeImg = R.drawable.ic_race_detail_time_10k;
			listImages = R.drawable.ic_race_detail_images_10k;
			titleImage = R.drawable.ic_races_detail_title_10k;
			sortItemBg = R.drawable.races_detail_sort_item_10k_bg;
			sortGroupBg = R.drawable.races_detail_sort_group_10k_bg;
			raceColor = getResources().getColor(R.color.text_button_bg_10k);
			break;
		case Constants.SELECT_RACE_15K:

			sortItemColor = getResources().getColorStateList(
					R.color.races_detail_sort_text_15k);
			shareButton = R.drawable.ic_race_share_15k;
			listTimeImg = R.drawable.ic_race_detail_time_15k;
			listImages = R.drawable.ic_race_detail_images_15k;
			titleImage = R.drawable.ic_races_detail_title_15k;
			sortItemBg = R.drawable.races_detail_sort_item_15k_bg;
			sortGroupBg = R.drawable.races_detail_sort_group_15k_bg;
			raceColor = getResources().getColor(R.color.text_button_bg_15k);
			break;
		case Constants.SELECT_RACE_HALF_MAR:

			raceColor = getResources()
					.getColor(R.color.text_button_bg_half_mar);
			sortItemColor = getResources().getColorStateList(
					R.color.races_detail_sort_text_half_mar);
			shareButton = R.drawable.ic_race_share_half_mar;
			listTimeImg = R.drawable.ic_race_detail_time_half_mar;
			listImages = R.drawable.ic_race_detail_images_half_mar;
			titleImage = R.drawable.ic_races_detail_title_half_mar;
			sortItemBg = R.drawable.races_detail_sort_item_half_mar_bg;
			sortGroupBg = R.drawable.races_detail_sort_group_half_mar_bg;
			break;
		case Constants.SELECT_RACE_FULL_MAR:

			raceColor = getResources()
					.getColor(R.color.text_button_bg_full_mar);
			sortItemColor = getResources().getColorStateList(
					R.color.races_detail_sort_text_full_mar);
			shareButton = R.drawable.ic_race_share_full_mar;
			listTimeImg = R.drawable.ic_race_detail_time_full_mar;
			listImages = R.drawable.ic_race_detail_images_full_mar;
			titleImage = R.drawable.ic_races_detail_title_full_mar;
			sortItemBg = R.drawable.races_detail_sort_item_full_mar_bg;
			sortGroupBg = R.drawable.races_detail_sort_group_full_mar_bg;
			break;
		}

		mEmptyText = (TextView) findViewById(R.id.races_detail_no_item);
		mSortGroup = (RadioGroup) findViewById(R.id.races_detail_sort_group);
		mRaceList = (ExpandableListView) findViewById(R.id.races_detail_list);
		ImageView titleImg = (ImageView) findViewById(R.id.races_detail_title);
		TextView sortText = (TextView) findViewById(R.id.races_detail_sort_text);
		RadioButton sortItemDate = (RadioButton) findViewById(R.id.races_detail_sort_date);
		RadioButton sortItemTime = (RadioButton) findViewById(R.id.races_detail_sort_time);

		sortText.setTextColor(raceColor);
		sortItemDate.setTextColor(sortItemColor);
		sortItemTime.setTextColor(sortItemColor);
		titleImg.setBackgroundResource(titleImage);
		mSortGroup.setBackgroundResource(sortGroupBg);
		sortItemDate.setBackgroundResource(sortItemBg);
		sortItemTime.setBackgroundResource(sortItemBg);

		mBotLeftBtnTxt.setVisibility(View.VISIBLE);
		mBotRightBtnImg.setVisibility(View.VISIBLE);
		mSortGroup.setOnCheckedChangeListener(this);
		mBottomBtnLayout.setBackgroundColor(raceColor);

		// Disable group's click
		mRaceList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		displayData(mSelectedRace, raceColor, listImages, listTimeImg,
				shareButton);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.REQUETS_CODE_ADD_RACE) {

			displayData(mSelectedRace, 0, 0, 0);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		sortData(mRacesDetail);

		if (mRacesAdapter != null) {

			mRacesAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRaceItemClick(HashMap<String, Object> raceInfo) {
		// TODO Auto-generated method stub

		AddRaceActivity.setRace(raceInfo);
		Intent addRaceIntent = new Intent(RacesDetailActivity.this,
				AddRaceActivity.class);
		startActivityForResult(addRaceIntent, Constants.REQUETS_CODE_ADD_RACE);
	}

	@Override
	public void onRaceItemDelete(HashMap<String, Object> raceInfo) {
		// TODO Auto-generated method stub

		mHistory.remove(raceInfo);
		mUser.put(Constants.DATA, mHistory);
		mUser.saveInBackground();
		displayData(mSelectedRace, 0, 0, 0);
	}

	@Override
	public void onShareItem(HashMap<String, Object> raceInfo) {
		// TODO Auto-generated method stub

		showShareDialog(raceInfo);
	}

	/**
	 * Display user data
	 * 
	 * @param resources
	 *            Color of text, images of images button, image of time text
	 * */
	private void displayData(int selectedRace, int... resources) {

		List<HashMap<String, Object>> userHistories = null;
		if (mFriendRace != -1) {

			ParseUser friend = mFriends.get(mFriendRace);
			userHistories = friend.getList(Constants.DATA);
		} else {

			userHistories = mHistory;
		}

		if (userHistories != null) {
			mRacesDetail = new HashMap<String, List<HashMap<String, Object>>>();
			for (HashMap<String, Object> history : userHistories) {

				int raceType = 0;
				try {

					raceType = (Integer) (history.get(Constants.EVENTTYPE));
				} catch (Exception ex) {

					raceType = Integer.valueOf((String) history
							.get(Constants.EVENTTYPE));
				}

				if (raceType == selectedRace) {

					Date monthDate = (Date) history.get(Constants.RACEDATE);
					SimpleDateFormat dateformat = new SimpleDateFormat(
							"yyyy-MM");
					String monthStr = dateformat.format(monthDate);
					List<HashMap<String, Object>> histories = mRacesDetail
							.get(monthStr);
					if (histories == null) {

						histories = new ArrayList<HashMap<String, Object>>();
						histories.add(history);
						mRacesDetail.put(monthStr, histories);
					} else {

						histories.add(history);
					}
				}
			}

			mRacesDetail = sortData(mRacesDetail);
			if (mRacesDetail == null || mRacesDetail.size() == 0) {

				mEmptyText.setVisibility(View.VISIBLE);
			} else {

				mEmptyText.setVisibility(View.INVISIBLE);
				if (mRacesAdapter == null) {

					mRacesAdapter = new RaceDetailAdapter(
							RacesDetailActivity.this, mRacesDetail,
							mFriendRace, resources);
					mRaceList.setAdapter(mRacesAdapter);
					mRacesAdapter.setRaceItemClick(this);
					mRacesAdapter.setOnRaceItemDelete(this);
					mRacesAdapter.setOnShareItemListenner(this);
				} else {

					mRacesAdapter.setData(mRacesDetail);
					mRacesAdapter.notifyDataSetChanged();
				}

				// Expand all groups
				for (int i = 0; i < mRacesAdapter.getGroupCount(); i++) {

					mRaceList.expandGroup(i);
				}
			}
		} else {

			mEmptyText.setVisibility(View.VISIBLE);
		}
	}

	private Map<String, List<HashMap<String, Object>>> sortData(
			Map<String, List<HashMap<String, Object>>> map) {

		List<String> keys = new ArrayList<String>(map.keySet());
		Map<String, List<HashMap<String, Object>>> returnMap = new LinkedHashMap<String, List<HashMap<String, Object>>>();
		if (keys != null) {

			Collections.sort(keys, new Comparator<String>() {

				@Override
				public int compare(String lhs, String rhs) {
					// TODO Auto-generated method stub
					return rhs.compareTo(lhs);
				}
			});

			for (String key : keys) {

				returnMap.put(key, map.get(key));
			}
		}

		returnMap = sortMapByValues(returnMap);
		return returnMap;
	}

	private Map<String, List<HashMap<String, Object>>> sortMapByValues(
			Map<String, List<HashMap<String, Object>>> raceMap) {

		List<String> keys = new ArrayList<String>(raceMap.keySet());
		for (String key : keys) {

			List<HashMap<String, Object>> raceList = raceMap.get(key);
			Collections.sort(raceList,
					new Comparator<HashMap<String, Object>>() {

						@Override
						public int compare(HashMap<String, Object> lhs,
								HashMap<String, Object> rhs) {
							// TODO Auto-generated method stub

							int selectSort = mSortGroup
									.getCheckedRadioButtonId();
							if (selectSort == R.id.races_detail_sort_date) {

								Date lDate = (Date) lhs.get(Constants.RACEDATE);
								Date rDate = (Date) rhs.get(Constants.RACEDATE);
								return rDate.compareTo(lDate);
							} else {

								int lFinishTime = (Integer) lhs
										.get(Constants.FINISHTIME);
								int rFinishTime = (Integer) rhs
										.get(Constants.FINISHTIME);
								return lFinishTime - rFinishTime;
							}
						}
					});
		}

		return raceMap;
	}

	@SuppressWarnings("unchecked")
	private void showShareDialog(final HashMap<String, Object> race) {

		final Dialog dialog = new Dialog(RacesDetailActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_choose_share);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		TextView twitterBtn = (TextView) dialog
				.findViewById(R.id.dialog_share_twitter);
		TextView facebookBtn = (TextView) dialog
				.findViewById(R.id.dialog_share_facebook);

		twitterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ShareImagesAsync share = new ShareImagesAsync();
				share.setShareType(R.id.dialog_share_twitter);
				share.execute(race);
				dialog.dismiss();
			}
		});

		facebookBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ShareImagesAsync share = new ShareImagesAsync();
				share.setShareType(R.id.dialog_share_facebook);
				share.execute(race);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private class ShareImagesAsync extends
			AsyncTask<HashMap<String, Object>, Void, String> {

		private int shareType;
		private Dialog dialog;
		private ArrayList<Uri> imageUris;
		private List<String> imageBitmaps;

		public void setShareType(int shareType) {
			this.shareType = shareType;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(RacesDetailActivity.this, "", "",
					false, false);
		}

		@Override
		protected String doInBackground(HashMap<String, Object>... paramVarArgs) {
			// TODO Auto-generated method stub
			HashMap<String, Object> raceInfo = paramVarArgs[0];
			imageUris = new ArrayList<Uri>();
			imageBitmaps = new ArrayList<String>();
			String shareText = "";
			String distance = "";
			String raceDate = "";
			String shoeName = "";
			String userName = "";
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			Date date = (Date) raceInfo.get(Constants.RACEDATE);
			raceDate = df.format(date);
			switch (mSelectedRace) {
			case Constants.SELECT_RACE_5K:

				distance = Constants.RACE_5K;
				break;
			case Constants.SELECT_RACE_10K:

				distance = Constants.RACE_10K;
				break;
			case Constants.SELECT_RACE_15K:

				distance = Constants.RACE_15K;
				break;
			case Constants.SELECT_RACE_HALF_MAR:

				distance = Constants.RACE_HALF_MAR;
				break;
			case Constants.SELECT_RACE_FULL_MAR:

				distance = Constants.RACE_FULL_MAR;
				break;
			}

			if (raceInfo.containsKey(Constants.SHOE.toUpperCase())) {

				ParseObject shoe = (ParseObject) raceInfo.get(Constants.SHOE
						.toUpperCase());
				try {

					shoe.fetch();
					shoeName = shoe.getString(Constants.BRAND) + "("
							+ shoe.getString(Constants.MODEL) + ")";
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					LogUtil.e("ShareImagesAsync", e.getMessage());
				}
			}
			userName = mUser.getUsername();
			int total = (Integer) raceInfo.get(Constants.FINISHTIME);
			int hour = total / 3600;
			int min = (total % 3600) / 60;
			int sec = (total % 60);
			if (hour > 0) {

				if (!shoeName.equals("")) {

					shareText = String
							.format("%s Ran a %s race in %d hour %d minutes and %d seconds. Race Name is the %s, the race date is %s. The shoes he ran it in is %s",
									userName, distance, hour, min, sec,
									(String) raceInfo.get(Constants.RACENAME),
									raceDate, shoeName);
				} else {

					shareText = String
							.format("%s Ran a %s race in %d hour %d minutes and %d seconds. Race Name is the %s, the race date is %s.",
									userName, distance, hour, min, sec,
									(String) raceInfo.get(Constants.RACENAME),
									raceDate);
				}

			} else {

				if (!shoeName.equals("")) {

					shareText = String
							.format("%s Ran a %s race in %d minutes and %d seconds. Race Name is the %s, the race date is %s. The shoes he ran it in is %s",
									userName, distance, min, sec,
									(String) raceInfo.get(Constants.RACENAME),
									raceDate, shoeName);
				} else {

					shareText = String
							.format("%s Ran a %s race in %d minutes and %d seconds. Race Name is the %s, the race date is %s.",
									userName, distance, min, sec,
									(String) raceInfo.get(Constants.RACENAME),
									raceDate);
				}
			}

			if (raceInfo.containsKey(Constants.PERSON)) {

				try {

					ParseObject object = (ParseObject) raceInfo
							.get(Constants.PERSON);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					imageBitmaps.add(image.getUrl());
					imageUris.add(0, Utilities.saveBitmap(bmp, "person.jpg"));
					if (shareType == R.id.dialog_share_twitter) {

						return shareText;
					}
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			if (raceInfo.containsKey(Constants.BIB)) {

				try {

					ParseObject object = (ParseObject) raceInfo
							.get(Constants.BIB);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					imageBitmaps.add(image.getUrl());
					imageUris.add(0, Utilities.saveBitmap(bmp, "bib.jpg"));
					if (shareType == R.id.dialog_share_twitter) {

						return shareText;
					}
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			if (raceInfo.containsKey(Constants.MEDAL)) {

				try {

					ParseObject object = (ParseObject) raceInfo
							.get(Constants.MEDAL);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					imageBitmaps.add(image.getUrl());
					imageUris.add(0, Utilities.saveBitmap(bmp, "medal.jpg"));
					if (shareType == R.id.dialog_share_twitter) {

						return shareText;
					}
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			return shareText;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (shareType == R.id.dialog_share_facebook) {

				OpenGraphObject myobject = OpenGraphObject.Factory
						.createForPost("fitness.course");
				myobject.setProperty("title", result);
				myobject.setProperty("description", result);
				myobject.setProperty("caption", result);
				OpenGraphAction action = GraphObject.Factory
						.create(OpenGraphAction.class);
				action.setProperty("course", myobject);
				action.setImageUrls(imageBitmaps);
				FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(
						RacesDetailActivity.this, action, "fitness.runs",
						"course").build();
				uiHelper.trackPendingDialogCall(shareDialog.present());
				// Utilities.doShare(RacesDetailActivity.this,
				// "com.facebook.katana", result, imageUris);
			} else {

				Utilities.doShare(RacesDetailActivity.this,
						"com.twitter.android", result, imageUris);
			}

			dialog.dismiss();
		}
	}

}
