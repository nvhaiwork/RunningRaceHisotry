package com.runningracehisotry;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.runningracehisotry.adapters.NewRaceDetailAdapter;
import com.runningracehisotry.adapters.OnLikeItemClickListener;
import com.runningracehisotry.adapters.RaceDetailAdapter;
import com.runningracehisotry.adapters.NewRaceDetailAdapter.OnRaceItemClickListener;
import com.runningracehisotry.adapters.NewRaceDetailAdapter.OnRaceItemDelete;
import com.runningracehisotry.adapters.NewRaceDetailAdapter.OnShareItemClickListener;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Race;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.DeleteRaceRequest;
import com.runningracehisotry.webservice.base.GetRaceByTypeRequest;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RacesDetailActivity extends BaseActivity implements
		OnCheckedChangeListener, OnRaceItemClickListener, OnRaceItemDelete,
		OnShareItemClickListener, OnLikeItemClickListener {

	private TextView mEmptyText;
	private RadioGroup mSortGroup;
	private ExpandableListView mRaceList;
	private int mSelectedRace, mFriendRace;
	//private RaceDetailAdapter mRacesAdapter;
    private NewRaceDetailAdapter mRacesAdapter;
	private Map<String, List<HashMap<String, Object>>> mRacesDetail;
//define store var
    private List<Race> listRace = new ArrayList<Race>();
    private Map<String, String> listKeyDateRace = new HashMap<String, String>();
    private Map<String, List<Race>> listRaceDetail = new HashMap<String, List<Race>>();
    int raceColor = 0;
    int listImages = 0;
    int shareButton = 0, likeButton;
    int listTimeImg = 0;
    private int delRaceId;
    private int updateRaceId;
    private int likeRaceId;


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

		int sortItemBg = 0;
		int titleImage = 0;
		//int listImages = 0;
		//int shareButton = 0;
		int sortGroupBg = 0;
		//int listTimeImg = 0;
		ColorStateList sortItemColor = null;
		switch (mSelectedRace) {
		case Constants.SELECT_RACE_5K:

			sortItemColor = getResources().getColorStateList(
					R.color.races_detail_sort_text_5k);
			shareButton = R.drawable.ic_race_share_5k;
            likeButton = R.drawable.unlike;
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
            likeButton = R.drawable.unlike;
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
            likeButton = R.drawable.unlike;
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
            likeButton = R.drawable.unlike;
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
            likeButton = R.drawable.unlike;
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
		//displayData(mSelectedRace, raceColor, listImages, listTimeImg, shareButton);
        //get race by type
        if(mFriendRace == -1){
            /*String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "0");
            mFriendRace = Integer.parseInt(userId);*/
        }
        LogUtil.d(Constants.LOG_TAG, "Race Call by type: " + mSelectedRace +"|"+ mFriendRace);
        getRaceByType(mSelectedRace, mFriendRace);
	}

    private void getRaceByType(int mSelectedRace, int mFriendRace) {
        GetRaceByTypeRequest request = new GetRaceByTypeRequest("date", mSelectedRace);
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    private void processGetRaceByType(String data) throws Exception{
        Type listType = new TypeToken<List<Race>>(){}.getType();
        Gson gson = new Gson();
        List<Race> lst = gson.fromJson(data.toString(), listType);
        /*for(Race race: lst){
            if(race.getLikes() != null){
                LogUtil.d(Constants.LOG_TAG, "Race Like: "+ race.getLikes().size());
            }
            LogUtil.d(Constants.LOG_TAG, "Race infor brand Shoe: "
                    + race.getShoe().getId() +"|"
                    + race.getShoe().getBrand() +"|"
                    +race.getShoe().getModel());
        }*/
        listKeyDateRace.clear();
        listKeyDateRace.keySet().clear();
        for(Race race: lst){
            String raceDate = race.getRaceDate().substring(0,7);
            LogUtil.d(Constants.LOG_TAG, "Race date: " + raceDate);
            try{
                listKeyDateRace.put(raceDate, "raceDate");
            }
            catch (Exception ex){
            }
        }
        for(String key : listKeyDateRace.keySet()){
            LogUtil.d(Constants.LOG_TAG, "Race key: " + key);
        }
        listRaceDetail.clear();
        listRaceDetail.keySet().clear();
        for(String key : listKeyDateRace.keySet()){
            listRace.clear();
            for(Race race: lst){
                String raceDate = race.getRaceDate().substring(0,7);
                LogUtil.d(Constants.LOG_TAG, "Add race date: " + raceDate);
                if(raceDate.equalsIgnoreCase(key)){
                    LogUtil.d(Constants.LOG_TAG, "Add Race for key: " + key);
                    listRace.add(race);
                }
            }
            LogUtil.d(Constants.LOG_TAG, "Races for key: " + key +" size: " + listRace.size());
            listRaceDetail.put(key, new ArrayList<Race>(listRace));
        }
        for(String keyDetail : listRaceDetail.keySet()){
            List<Race> temp = new ArrayList<Race>(listRaceDetail.get(keyDetail));
            LogUtil.d(Constants.LOG_TAG, String.format("Date %s has total race: %s", keyDetail, temp.size()));
        }

        fillData(mSelectedRace,raceColor, listImages, listTimeImg, shareButton, likeButton );


    }

    private void fillData(int selectedRace, int... resources) {
        List<HashMap<String, Object>> userHistories = null;
		/*if (mFriendRace != -1) {

			ParseUser friend = mFriends.get(mFriendRace);
			userHistories = friend.getList(Constants.DATA);
		} else {

			userHistories = mHistory;
		}*/

        if (listRaceDetail == null || listRaceDetail.size() == 0) {
            if (mRacesAdapter != null) {
                mRacesAdapter.notifyDataSetChanged();
            }
            mEmptyText.setVisibility(View.VISIBLE);
        } else {

            mEmptyText.setVisibility(View.INVISIBLE);
            //if (mRacesAdapter == null) {

                mRacesAdapter = new NewRaceDetailAdapter(
                        RacesDetailActivity.this, listRaceDetail,
                        mFriendRace, resources);
                mRaceList.setAdapter(mRacesAdapter);
                mRacesAdapter.setRaceItemClick(this);
                mRacesAdapter.setOnRaceItemDelete(this);
                mRacesAdapter.setOnShareItemListener(this);
                mRacesAdapter.setOnLikeItemListener(this);
            /*} else {

                mRacesAdapter.setData(listRaceDetail);
                mRacesAdapter.notifyDataSetChanged();
            }*/

            // Expand all groups
            for (int i = 0; i < mRacesAdapter.getGroupCount(); i++) {

                mRaceList.expandGroup(i);
            }
        }/*
        if (userHistories != null) {
			*//*mRacesDetail = new HashMap<String, List<HashMap<String, Object>>>();
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
			}*//*
        } else {

            mEmptyText.setVisibility(View.VISIBLE);
        }*/
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
            LogUtil.i("Running", data.toString());
            if (methodName.equals(ServiceConstants.METHOD_GET_RACES_BY_TYPE)) {
                try {
                    LogUtil.d(Constants.LOG_TAG, "List race: " + data.toString());
                    processGetRaceByType(data.toString());
                    //JSONArray jsonObjectReceive = new JSONArray(data.toString());
                    //boolean result = jsonObjectReceive.getBoolean("result");
                    // Login success

                } catch (Exception e) {
                    e.printStackTrace();
                    Utilities.showAlertMessage(
                            RacesDetailActivity.this,
                            "get race list failed",
                            "");
                } finally {
                    /*if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }*/
                }
            }
            else if (methodName.equals(ServiceConstants.METHOD_DELETE_RACE_BY_ID)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processCallApiDeleteRace(data.toString());
                        try {
                            LogUtil.d(Constants.LOG_TAG, "List race: " + data.toString());

                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                    /*if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }*/
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

    private void processCallApiDeleteRace(String data) {
        try {
            LogUtil.d(Constants.LOG_TAG, "Delete race: " + data.toString());
            JSONObject jsonObjectReceive = new JSONObject(data.toString());
            boolean result = jsonObjectReceive.getBoolean("result");
            if (result) {
                //remove race in list then refresh
                processAfterDeleteRace();
            }
            else{
                Utilities.showAlertMessage(
                        RacesDetailActivity.this,
                        "Delete race failed",
                        "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utilities.showAlertMessage(
                    RacesDetailActivity.this,
                    "Delete race failed due to server",
                    "");

        } finally {

        }

    }

    private void processAfterDeleteRace() {

        Map<String, List<Race>> listRaceDetailTemp = new HashMap<String, List<Race>>();
        listRaceDetailTemp.keySet().clear();
        Set<String> keys = listRaceDetail.keySet();
        for(String key : keys){
            List<Race> listRaceTemp = new ArrayList<Race>();
            listRaceTemp.clear();
            List<Race> temp = listRaceDetail.get(key);
            listRaceTemp = new ArrayList<Race>(temp);
            if(listRaceTemp != null && listRaceTemp.size()>0){
                int i = 0;
                int len = temp.size();
                for(i = 0; i<len; i++){
                //for(Race race: listRaceTemp){
                    //LogUtil.d(Constants.LOG_TAG, "Remove race i= :" + i);
                    if(temp.get(i).getId() == delRaceId){
                        Race result = listRaceTemp.remove(i);
                        if(result != null){
                            LogUtil.d(Constants.LOG_TAG, "Remove race in list done at key: " + key
                            + " Size: " + listRaceTemp.size());
                            //break;
                        }
                    }
                }
                if(listRaceTemp.size() > 0){
                    LogUtil.d(Constants.LOG_TAG, "Month " + key +" size: " + listRaceTemp.size());
                    listRaceDetailTemp.put(key, new ArrayList<Race>(listRaceTemp));
                }
                else{
                    LogUtil.d(Constants.LOG_TAG, "Month : " + key + " has no race");
                }

            }
        }
        listRaceDetail.clear();
        listRaceDetail.keySet().clear();
        if(listRaceDetailTemp.size() > 0){
            LogUtil.d(Constants.LOG_TAG, "Still has race");
            for(String keyOld: listRaceDetailTemp.keySet()){
                LogUtil.d(Constants.LOG_TAG, "Still has race" + keyOld + "|" +listRaceDetailTemp.get(keyOld).size());
                listRaceDetail.put(keyOld, listRaceDetailTemp.get(keyOld));
            }

            //listRaceDetail.putAll(listRaceDetailTemp);
        }
        else{
            LogUtil.d(Constants.LOG_TAG, "Still nothing!");
            mEmptyText.setVisibility(View.VISIBLE);
        }
        for(String key1 : keys){
            //List<Race> list = new ArrayList<Race>(listRaceDetail.get(key1));
            LogUtil.d(Constants.LOG_TAG, "Key: " + key1 +" size: " + listRaceDetail.get(key1).size());
        }
        LogUtil.d(Constants.LOG_TAG, "refresh list race!");
        mRacesAdapter.setData(listRaceDetail);
        mRacesAdapter.notifyDataSetChanged();
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.REQUETS_CODE_ADD_RACE) {

            //find and update then notify
			//displayData(mSelectedRace, 0, 0, 0);
            if(data != null && (data.getAction() != null) && data.getAction().equalsIgnoreCase("updateRaceCallBackSucceed")){
                getRaceByType(mSelectedRace, mFriendRace);
                LogUtil.d(Constants.LOG_TAG, "back from update RAce ok");
            }
            else{
                LogUtil.d(Constants.LOG_TAG, "back from update RAce failed!");
            }

		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

        listRaceDetail = sortDataNew(listRaceDetail);
        if (mRacesAdapter != null) {
            mRacesAdapter.notifyDataSetChanged();
        }
        LogUtil.d(Constants.LOG_TAG, "sort then refresh");
        /*if(checkedId == R.id.races_detail_sort_time){

        }
        else if(checkedId == R.id.races_detail_sort_time){
            LogUtil.d(Constants.LOG_TAG, "do nothing, not sort");
        }*/

	}

	@Override
	public void onRaceItemClick(Race raceInfo) {
		// TODO Auto-generated method stub

		AddRaceActivity.setRaceUpdate(raceInfo);
		Intent addRaceIntent = new Intent(RacesDetailActivity.this,
				AddRaceActivity.class);
		startActivityForResult(addRaceIntent, Constants.REQUETS_CODE_ADD_RACE);
	}

	@Override
	public void onRaceItemDelete(Race raceInfo) {
		// TODO Auto-generated method stub
		/*mHistory.remove(raceInfo);
		mUser.put(Constants.DATA, mHistory);
		mUser.saveInBackground();
		displayData(mSelectedRace, 0, 0, 0);*/
        //SHow race for delete
        this.delRaceId = raceInfo.getId();
        callDeleteRace();
	}

    private void callDeleteRace() {
        DeleteRaceRequest request = new DeleteRaceRequest(String.valueOf(this.delRaceId));
        request.setListener(callBackEvent);
        new Thread(request).start();
        LogUtil.d(Constants.LOG_TAG, "Delete race di: " + this.delRaceId);
    }

    @Override
	public void onShareItem(Race raceInfo) {
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
		/*if (mFriendRace != -1) {

			ParseUser friend = mFriends.get(mFriendRace);
			userHistories = friend.getList(Constants.DATA);
		} else {

			userHistories = mHistory;
		}*/

		if (listRaceDetail != null) {
            mEmptyText.setVisibility(View.INVISIBLE);
			/*mRacesDetail = new HashMap<String, List<HashMap<String, Object>>>();
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
			}*/
		} else {

			mEmptyText.setVisibility(View.VISIBLE);
		}
	}

    private Map<String, List<Race>> sortDataNew(
            Map<String, List<Race>> map) {

        List<String> keys = new ArrayList<String>(map.keySet());
        Map<String, List<Race>> returnMap = new LinkedHashMap<String, List<Race>>();
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

        returnMap = sortMapNewByValues(returnMap);
        return returnMap;
    }

    private Map<String, List<Race>> sortMapNewByValues(
            Map<String, List<Race>> raceMap) {

        List<String> keys = new ArrayList<String>(raceMap.keySet());
        for (String key : keys) {

            List<Race> raceList = raceMap.get(key);
            Collections.sort(raceList,
                    new Comparator<Race>() {

                        @Override
                        public int compare(Race lhs,
                                           Race rhs) {
                            // TODO Auto-generated method stub

                            int selectSort = mSortGroup
                                    .getCheckedRadioButtonId();
                            if (selectSort == R.id.races_detail_sort_date) {
                                String dateStr1 = lhs.getRaceDate().substring(0,10);
                                String dateStr2 = rhs.getRaceDate().substring(0,10);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                Date lDate, rDate;
                                try {
                                    lDate = format.parse(dateStr1);
                                    rDate = format.parse(dateStr2);

                                    LogUtil.e(Constants.LOG_TAG, "parse to compare Sort date: "
                                            + (rDate.compareTo(lDate)));
                                    return rDate.compareTo(lDate);
                                } catch (java.text.ParseException e) {
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare sort date error: " + e.getMessage());
                                }
                                return 0;
                            } else {
                                try{
                                    String[] dateStr1 = lhs.getFinisherTime().split(":");
                                    String[] dateStr2 = rhs.getFinisherTime().split(":");
                                    int lFinishTime = Integer.parseInt(dateStr1[0])*60*60
                                        + Integer.parseInt(dateStr1[1])*60
                                            + Integer.parseInt(dateStr1[2]);
                                    int rFinishTime = Integer.parseInt(dateStr2[0])*60*60
                                            + Integer.parseInt(dateStr2[1])*60
                                            + Integer.parseInt(dateStr2[2]);
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare Sort time: "
                                            + (lFinishTime - rFinishTime));
                                    return (lFinishTime - rFinishTime);
                                }
                                catch (Exception e){
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare Sort time error: " + e.getMessage());
                                }

                               /* int lFinishTime = (Integer) lhs
                                        .get(Constants.FINISHTIME);
                                int rFinishTime = (Integer) rhs
                                        .get(Constants.FINISHTIME);
                                return lFinishTime - rFinishTime;*/
                                return 0;
                            }
                        }
                    });
        }

        return raceMap;
    }
	/*private Map<String, List<HashMap<String, Object>>> sortData(
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
	}*/

	@SuppressWarnings("unchecked")
	private void showShareDialog(final Race race) {

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

				/*ShareImagesAsync share = new ShareImagesAsync();
				share.setShareType(R.id.dialog_share_twitter);
				share.execute(race);*/
				dialog.dismiss();
			}
		});

		facebookBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				/*ShareImagesAsync share = new ShareImagesAsync();
				share.setShareType(R.id.dialog_share_facebook);
				share.execute(race);*/
				dialog.dismiss();
			}
		});

		dialog.show();
	}

    @Override
    public void onLikeItem(Race raceInfo) {
        LogUtil.d(Constants.LOG_TAG,"Like click");
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
