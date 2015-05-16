package com.runningracehisotry;

import java.beans.IndexedPropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//import com.facebook.Session;
//import com.facebook.model.GraphObject;
//import com.facebook.model.OpenGraphAction;
//import com.facebook.model.OpenGraphObject;
//import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
//import com.runningracehisotry.adapters.NewRaceDetailAdapter;
import com.runningracehisotry.adapters.OnLikeItemClickListener;
import com.runningracehisotry.adapters.NewRaceDetailAdapter.OnRaceItemClickListener;
import com.runningracehisotry.adapters.NewRaceDetailAdapter.OnRaceItemDelete;
import com.runningracehisotry.adapters.NewRaceDetailAdapter.OnShareItemClickListener;
import com.runningracehisotry.adapters.RaceDetailSortAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Like;
import com.runningracehisotry.models.Race;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.DateValueComparator;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.RaceDateComparator;
import com.runningracehisotry.utilities.RaceLengthComparator;
import com.runningracehisotry.utilities.RaceTimeComparator;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddLikeRequest;
import com.runningracehisotry.webservice.base.BaseGetRequest;
import com.runningracehisotry.webservice.base.DeleteRaceRequest;
import com.runningracehisotry.webservice.base.GetAllRaceRequest;
import com.runningracehisotry.webservice.base.GetLikeOfRaceRequest;
import com.runningracehisotry.webservice.base.GetRaceByTypeRequest;
import com.runningracehisotry.webservice.base.UnLikeRequest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
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
//import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/*public class RacesDetailActivity extends BaseActivity implements
        OnCheckedChangeListener, OnRaceItemClickListener, OnRaceItemDelete,
        OnShareItemClickListener, OnLikeItemClickListener {*/
public class RacesDetailActivity extends BaseActivity implements
        OnCheckedChangeListener, RaceDetailSortAdapter.OnRaceItemClickListener, RaceDetailSortAdapter.OnRaceItemDelete,
        RaceDetailSortAdapter.OnShareItemClickListener, OnLikeItemClickListener {

    private TextView mEmptyText;
    private RadioGroup mSortGroup;
    private ExpandableListView mRaceList;
    private int mSelectedRace, mFriendRace;
    //private RaceDetailAdapter mRacesAdapter;
    //private NewRaceDetailAdapter mRacesAdapter;
    private RaceDetailSortAdapter mRacesSortAdapter;
    //private Map<String, List<HashMap<String, Object>>> mRacesDetail;
    //define store var
    private List<Race> listRace = new ArrayList<Race>();
    LinkedHashMap<String, List<Race>> listRaceDetail = new LinkedHashMap<String, List<Race>>();
    //private Map<String, String> listKeyDateRace = new HashMap<String, String>();
    //private Map<String, List<Race>> listRaceDetail = new HashMap<String, List<Race>>();
    int raceColor = 0;
    int listImages = 0;
    int shareButton = 0, likeButton;
    int listTimeImg = 0;
    private int delRaceId;
    private int typeLike;
    private int likeRaceId;
    private CustomLoadingDialog mLoadingDialog;
    private ProgressDialog pDialog;


    private void printSortedMap(LinkedHashMap<String, List<Race>> map){
        for(String key: map.keySet()){
            List<Race> list = map.get(key);
            LogUtil.e(Constants.LOG_TAG, "AFter PARSE, key:" + key + " has " +list.size() + " race date 0: " + list.get(0).getRaceDate());
            for(Race race : list){
                LogUtil.e(Constants.LOG_TAG, "AFter PARSE, race: " + race.getRaceDate());
            }
        }
    }

    private void sortByDate(){
        Collections.sort(listRace, new RaceDateComparator());
    }
    private void sortByTime(){
        List<Race> list = new ArrayList<Race>(listRace);
        Collections.sort(list, new RaceTimeComparator());
        listRace = list;
    }
    private void sortByLength(){
        List<Race> list = new ArrayList<Race>(listRace);
        Collections.sort(list, new RaceLengthComparator());
        listRace = list;
    }

    private LinkedHashMap<String, List<Race>> parseSortByDate(){
        LinkedHashMap<String, List<Race>> mapDate = new LinkedHashMap<String, List<Race>>();
        int i, j, t, len1 = listRace.size();
        for(i =0;i <len1; i++){
            t=i;
            String date = listRace.get(i).getRaceDate().substring(0,7);
            LogUtil.e(Constants.LOG_TAG, "PARSE KEY: " + date);
            List<Race> list = new ArrayList<Race>();
            list.add(list.size(), listRace.get(i));
            LogUtil.e(Constants.LOG_TAG, "ADD RACE: " + listRace.get(i).getRaceDate().substring(0, 10));
            for(j =i+1;j <len1; j++){
                LogUtil.e(Constants.LOG_TAG, "ADD MORE RACE???: " + listRace.get(j).getRaceDate().substring(0,7));
                if(listRace.get(j).getRaceDate().substring(0,7).equalsIgnoreCase(listRace.get(i).getRaceDate().substring(0,7))){
                    LogUtil.e(Constants.LOG_TAG, "ADD MORE RACE: " + listRace.get(j).getRaceDate().substring(0,10));
                    list.add(list.size(), listRace.get(j));
                    t = j;
                }
            }
            //Collections.sort(list, new RaceDateComparator());
            mapDate.put(date, list);
            i = t;
        }
        return mapDate;
    }


    private LinkedHashMap<String, List<Race>> parseSortByTimeOrLength(){
        LinkedHashMap<String, List<Race>> mapTime = new LinkedHashMap<String, List<Race>>();
        int i, len1 = listRace.size();
        for(i =0;i <len1; i++){
            String date = listRace.get(i).getRaceDate().substring(0,10) + "_" +i;
            List<Race> list = new ArrayList<Race>();
            list.clear();
            list.add(list.size(), listRace.get(i));
            LogUtil.e(Constants.LOG_TAG, "ADD TIME/LENGTH: " + date +"|" + listRace.get(i).getRaceDate());
            LogUtil.e(Constants.LOG_TAG, "ADD TIME/LENGTH: " + list.size());
            mapTime.put(date, list);
            LogUtil.e(Constants.LOG_TAG, "ADD TIME/LENGTH: " + mapTime.get(date).get(0).getRaceDate());
        }
        return mapTime;
    }



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
        if(mSelectedRace ==  Constants.SELECT_RACE_OTHER){

        }
        int sortItemBg = 0;
        int titleImage = 0;
        //int listImages = 0;
        //int shareButton = 0;
        int sortGroupBg = 0;
        //int listTimeImg = 0;
        ColorStateList sortItemColor = null;
        RadioButton sortItemLength = (RadioButton) findViewById(R.id.races_detail_sort_length);
        View viewTitle = (View) findViewById(R.id.races_detail_title_text);
        View view1 = (View) findViewById(R.id.races_detail_sort_1);
        View view2 = (View) findViewById(R.id.races_detail_sort_2);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        sortItemLength.setVisibility(View.GONE);
        switch (mSelectedRace) {
            case Constants.SELECT_RACE_5K:

                sortItemColor = getResources().getColorStateList(
                        R.color.races_detail_sort_text_5k);
                shareButton = R.drawable.ic_race_share_5k;
                likeButton = R.drawable.unlike_5k;
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
                likeButton = R.drawable.unlike_10k;
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
                likeButton = R.drawable.unlike_15k;
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
                likeButton = R.drawable.unlike_half;
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
                likeButton = R.drawable.unlike_full;
                listTimeImg = R.drawable.ic_race_detail_time_full_mar;
                listImages = R.drawable.ic_race_detail_images_full_mar;
                titleImage = R.drawable.ic_races_detail_title_full_mar;
                sortItemBg = R.drawable.races_detail_sort_item_full_mar_bg;
                sortGroupBg = R.drawable.races_detail_sort_group_full_mar_bg;
                break;

            case Constants.SELECT_RACE_OTHER:

                sortItemColor = getResources().getColorStateList(
                        R.color.races_detail_sort_text_other);
                shareButton = R.drawable.ic_race_share_other;
                likeButton = R.drawable.unlike_other;
                listTimeImg = R.drawable.ic_race_detail_time_other;
                listImages = R.drawable.ic_race_detail_images_other;
                titleImage = R.drawable.ic_races_detail_title_other;
                sortItemBg = R.drawable.races_detail_sort_item_other_bg;
                sortGroupBg = R.drawable.races_detail_sort_group_other_bg;
                raceColor = getResources().getColor(R.color.text_button_bg_other);
                //RadioButton sortItemLength = (RadioButton) findViewById(R.id.races_detail_sort_length);
                viewTitle.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                sortItemLength.setVisibility(View.VISIBLE);
                sortItemLength.setTextColor(sortItemColor);
                sortItemLength.setBackgroundResource(sortItemBg);
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
        LogUtil.d(Constants.LOG_TAG, "Race Call by type|friend Id: " + mSelectedRace +"|"+ mFriendRace);
        getRaceByType(mSelectedRace, mFriendRace);
    }

    private void getRaceByType(int mSelectedRace, int mFriendRace) {
        if(mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.show(RacesDetailActivity.this, "", "", false, false);
        }

        /*if(mSelectedRace == Constants.SELECT_RACE_OTHER){
            this.mSelectedRace = Constants.SELECT_RACE_5K;
            LogUtil.d(Constants.LOG_TAG, "Race Call fake 6 -> 1");
        }*/
        BaseGetRequest request = null;
        if(mFriendRace == -1){
            if(mSelectedRace == Constants.SELECT_RACE_OTHER) {
                request = new GetAllRaceRequest();
            }
            else {
                request = new GetRaceByTypeRequest("date", this.mSelectedRace);
            }
        }
        else{
            if(mSelectedRace == Constants.SELECT_RACE_OTHER) {
                request = new GetAllRaceRequest();
            }
            else {
                request = new GetRaceByTypeRequest("date", this.mSelectedRace, mFriendRace);
            }
        }
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    private void processGetRaceByType(String data) throws Exception{
        Type listType = new TypeToken<List<Race>>(){}.getType();
        Gson gson = new Gson();
        List<Race> lst = gson.fromJson(data.toString(), listType);

        //TODO magic code, remove later
        if(mSelectedRace == Constants.SELECT_RACE_OTHER) {
            List<Race> lst2 = new ArrayList<Race>();
            for(Race race : lst) {
                if(race.getEvenType() == 6) {
                    lst2.add(race);
                }
            }
            lst = lst2;
        }

        listRace = new ArrayList<Race>(lst);
        int a =0,b = listRace.size();
        for(a = 0; a< b; a++){
            LogUtil.e(Constants.LOG_TAG, "BEFORE SORT Race " + a + " has date: " + listRace.get(a).getRaceDate().substring(0,10));
        }
        sortByDate();
        for(a = 0; a< b; a++){
            LogUtil.e(Constants.LOG_TAG, "AFTER SORT Race " + a + " has date: " + listRace.get(a).getRaceDate().substring(0,10));
        }
        listRaceDetail.clear();
        listRaceDetail.keySet().clear();
        listRaceDetail = parseSortByDate();



        fillDataSort(mSelectedRace, raceColor, listImages, listTimeImg, shareButton, likeButton);

        if(mSelectedRace == Constants.SELECT_RACE_OTHER) {
            for(String keyDetail : listRaceDetail.keySet()){
                List<Race> temp = new ArrayList<Race>(listRaceDetail.get(keyDetail));

                for(Race rTemp: temp) {
                    //LogUtil.d(Constants.LOG_TAG, "Call API get Like for race ID: " + rTemp.getId());
                    GetLikeOfRaceRequest request = new GetLikeOfRaceRequest(rTemp.getId());
                    request.setListener(callBackEvent);
                    new Thread(request).start();
                }
            }
        }

    }

    private void fillDataSort(int mSelectedRace, int...resources) {
        if (listRace == null || listRace.size() == 0) {
            if (mRacesSortAdapter != null) {
                mRacesSortAdapter.notifyDataSetChanged();
            }
            mEmptyText.setVisibility(View.VISIBLE);
        } else {

            mEmptyText.setVisibility(View.INVISIBLE);
            //if (mRacesAdapter == null) {
            String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
            if(!userId.isEmpty()) {
//                int id = Integer.parseInt(userId);
                //  mRacesSortAdapter = new RaceDetailSortAdapter
                mRacesSortAdapter = new RaceDetailSortAdapter(mSelectedRace, userId, RacesDetailActivity.this, listRaceDetail,
                        mFriendRace, resources);
                mRaceList.setAdapter(mRacesSortAdapter);
                mRacesSortAdapter.setRaceItemClick(this);
                mRacesSortAdapter.setOnRaceItemDelete(this);
                mRacesSortAdapter.setOnShareItemListener(this);
                mRacesSortAdapter.setOnLikeItemListener(this);
            /*} else {

                mRacesAdapter.setData(listRaceDetail);
                mRacesAdapter.notifyDataSetChanged();
            }*/

                // Expand all groups
                for (int i = 0; i < mRacesSortAdapter.getGroupCount(); i++) {

                    mRaceList.expandGroup(i);
                }
            }
        }
    }

    private void processAddLike(String json) throws JSONException{

        Type listType = new TypeToken<List<Like>>(){}.getType();
        Gson gson = new Gson();
        List<Like> listLike = gson.fromJson(json, listType);
        if(listLike != null && listLike.size()>0){
            //listRaceDetail = addLikeForRace(listRaceDetail, listLike);
            addLikeForRace(listLike);
            if (mRacesSortAdapter != null) {
                mRacesSortAdapter.notifyDataSetChanged();
            }
        }
    }

    private void addLikeForRace(List<Like> lstLike) {


        int i, len;
        for(String keyDetail : listRaceDetail.keySet()){
            len = listRaceDetail.get(keyDetail).size();
            for(i=0; i<len; i++){
                if(listRaceDetail.get(keyDetail).get(i).getId() == lstLike.get(0).getShoeID()){
                    LogUtil.d(Constants.LOG_TAG, "Add like for race ID: " + lstLike.get(0).getShoeID());
                    listRaceDetail.get(keyDetail).get(i).setLikes(lstLike);
                    break;
                }
            }
            //listRaceDetail.get(keyDetail));

        }
        //return returnMap;
    }

    /*private void fillData(int selectedRace, int... resources) {
        List<HashMap<String, Object>> userHistories = null;
		*//*if (mFriendRace != -1) {

			ParseUser friend = mFriends.get(mFriendRace);
			userHistories = friend.getList(Constants.DATA);
		} else {

			userHistories = mHistory;
		}*//*

        if (listRaceDetail == null || listRaceDetail.size() == 0) {
            if (mRacesAdapter != null) {
                mRacesAdapter.notifyDataSetChanged();
            }
            mEmptyText.setVisibility(View.VISIBLE);
        } else {

            mEmptyText.setVisibility(View.INVISIBLE);
            //if (mRacesAdapter == null) {
            String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
            if(!userId.isEmpty()) {
//                int id = Integer.parseInt(userId);

                mRacesAdapter = new NewRaceDetailAdapter(mSelectedRace, userId, RacesDetailActivity.this, listRaceDetail,
                        mFriendRace, resources);
                mRaceList.setAdapter(mRacesAdapter);
                mRacesAdapter.setRaceItemClick(this);
                mRacesAdapter.setOnRaceItemDelete(this);
                mRacesAdapter.setOnShareItemListener(this);
                mRacesAdapter.setOnLikeItemListener(this);
            *//*} else {

                mRacesAdapter.setData(listRaceDetail);
                mRacesAdapter.notifyDataSetChanged();
            }*//*

                // Expand all groups
                for (int i = 0; i < mRacesAdapter.getGroupCount(); i++) {

                    mRaceList.expandGroup(i);
                }
            }
        }

    }*/


    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {

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
                            RacesDetailActivity.this,getResources().getString(R.string.race_get_failed),"");
                } finally {
                    try{
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                            mLoadingDialog = null;
                        }
                    }
                    catch(Exception ex){
                    }
                }
            }

            else if (methodName.equals(ServiceConstants.METHOD_GET_LIKE_OF_RACE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processAddLike(data.toString());
                        }
                        catch (Exception e) {
                            LogUtil.d(Constants.LOG_TAG, "error when parse like");
                            e.printStackTrace();


                        }
                        finally {
                            try{
                            }
                            catch(Exception ex){
                            }
                        }
                    }
                });

            }
            else if (methodName.equals(ServiceConstants.METHOD_DELETE_RACE_BY_ID)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processCallApiDeleteRace(data.toString());
                            LogUtil.d(Constants.LOG_TAG, "List race: " + data.toString());
                        } catch (Exception e) {
                            e.printStackTrace();

                        } finally {
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                    mLoadingDialog = null;
                                }
                            }
                            catch(Exception ex){
                            }
                        }
                    }
                });

            }
            else if (methodName.equals(ServiceConstants.METHOD_ADD_LIKE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAfterLike(data);
                    }
                });
            }
            else if (methodName.equals(ServiceConstants.METHOD_REMOVE_LIKE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAfterUnLike(data);
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

    private void processAfterLike(Object data) {
        try {
            LogUtil.d(Constants.LOG_TAG, "METHOD_ADD_LIKE: " + data.toString());
            JSONObject jsonObjectReceive = new JSONObject(data.toString());
            boolean result = jsonObjectReceive.getBoolean("result");
            // Login success
            if (result) {
                String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
                if(!userId.isEmpty()) {
//                    int id = Integer.parseInt(userId);
                    if (userId != null && userId.length() > 0) {
                        LogUtil.d(Constants.LOG_TAG, "METHOD_ADD_LIKE user ID " + userId);
                        Like like = new Like(1000, userId, this.likeRaceId);
                        if(listRaceDetail.keySet().size() > 0){
                            Set<String> keys = listRaceDetail.keySet();
                            for(String key : keys){
                                List<Race> listRace = listRaceDetail.get(key);
                                for(Race race : listRace){
                                    if(race.getId() == this.likeRaceId){
                                        race.getLikes().add(like);
                                        LogUtil.d(Constants.LOG_TAG, "METHOD_ADD_LIKE DONE");
                                        break;
                                    }
                                }
                            }
                        }
                        //listRaceDetail = sortDataNew(listRaceDetail);
                        if (mRacesSortAdapter != null) {
                            mRacesSortAdapter.notifyDataSetChanged();
                        }
                    }
                    else{
                        LogUtil.d(Constants.LOG_TAG, "METHOD_ADD_LIKE user ID empty SNS?");
                    }
                }
                // add like and refresh
            }
            else{
                //do nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
            }
            catch(Exception ex){
            }
        }
    }

    private void processAfterUnLike(Object data) {
        try {
            LogUtil.d(Constants.LOG_TAG, "METHOD_UN_LIKE: " + data.toString());
            JSONObject jsonObjectReceive = new JSONObject(data.toString());
            boolean result = jsonObjectReceive.getBoolean("result");
            // Login success
            if (result) {
                String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
                if(!userId.isEmpty()) {
//                    int id = Integer.parseInt(userId);
                    if (userId != null && userId.length() > 0) {
                        //Like like = new Like(1000, userId);
                        LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE user ID: " + userId);
                        if(listRaceDetail.keySet().size() > 0){
                            Set<String> keys = listRaceDetail.keySet();
                            for(String key : keys){
                                List<Race> listRace = listRaceDetail.get(key);
                                List<Race> listRaceTemp = new ArrayList<Race>(listRace);
                                int i, len = listRaceTemp.size();
                                for(i = 0; i< len; i++){
                                    if(listRace.get(i).getId() == this.likeRaceId){
                                        List<Like> likes = listRace.get(i).getLikes();
                                        if(likes !=null && likes.size()>0){
                                            int lenLike = likes.size();
                                            for(int j =0; j < lenLike; j++){
                                                LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE DONE IN: "
                                                        + likes.get(j).getUserID());
                                                if(String.valueOf(likes.get(j).getUserID()).equalsIgnoreCase(userId)){
                                                    LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE DONE IN IN");
                                                    listRace.get(i).getLikes().remove(j);
                                                    break;
                                                }
                                            }
                                        }
                                        LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE DONE");
                                        break;
                                    }
                                }
                            }
                        }
                        //listRaceDetail = sortDataNew(listRaceDetail);
                        int selectSort = mSortGroup
                                .getCheckedRadioButtonId();
                        listRaceDetail.clear();
                        listRaceDetail.keySet().clear();
                        if (selectSort == R.id.races_detail_sort_date) {
                            sortByDate();
                            listRaceDetail = parseSortByDate();
                        }
                        else if (selectSort == R.id.races_detail_sort_time) {
                            sortByTime();
                            listRaceDetail = parseSortByTimeOrLength();
                        }
                        else{
                            sortByLength();
                            listRaceDetail = parseSortByTimeOrLength();
                        }
                        LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE DONE REMAIN: " + listRaceDetail.size());
                        if (mRacesSortAdapter != null) {
                            mRacesSortAdapter.getData().clear();
                            mRacesSortAdapter.setData(listRaceDetail);
                            mRacesSortAdapter.notifyDataSetChanged();
                        }

                        /*if (mRacesSortAdapter != null) {
                            mRacesSortAdapter.notifyDataSetChanged();
                        }*/
                    }
                    else{
                        LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE user ID empty SNS?");
                    }
                }
                // add like and refresh
            }
            else{
                //do nothing
            }
        } catch (Exception e) {
            LogUtil.d(Constants.LOG_TAG, "METHOD_REMOVE_LIKE EXCEPTION");
            e.printStackTrace();
        } finally {
            try{
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
            }
            catch(Exception ex){
            }
        }
    }

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

        TreeMap<String, List<Race>> listRaceDetailTemp = new TreeMap<String, List<Race>>();
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
        mRacesSortAdapter.setData(listRaceDetail);
        mRacesSortAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
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

        } else {
            super.onActivityResult(requestCode, resultCode, data);
//            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub

        //listRaceDetail = sortDataNew(listRaceDetail);
        int selectSort = mSortGroup
                .getCheckedRadioButtonId();
        listRaceDetail.clear();
        listRaceDetail.keySet().clear();
        if (selectSort == R.id.races_detail_sort_date) {
            sortByDate();
            int a, b = listRace.size();
            for(a = 0; a< b; a++){
                LogUtil.e(Constants.LOG_TAG, "AFTER SORT Race DAte" + a + " has date: " + listRace.get(a).getRaceDate().substring(0,10)
                        + "|"+listRace.get(a).getFinisherTime());
            }

            listRaceDetail = parseSortByDate();
            LogUtil.d(Constants.LOG_TAG, "==================================================");
            LogUtil.d(Constants.LOG_TAG, "PRINT AFTER SORT DATE");
            printSortedMap(listRaceDetail);
            LogUtil.d(Constants.LOG_TAG, "==================================================");
        }
        else if (selectSort == R.id.races_detail_sort_time) {
            sortByTime();
            int a, b = listRace.size();
            for(a = 0; a< b; a++){
                LogUtil.e(Constants.LOG_TAG, "AFTER SORT Race Time" + a + " has date: " + listRace.get(a).getRaceDate().substring(0,10)
                + "|"+listRace.get(a).getFinisherTime());
            }
            listRaceDetail = parseSortByTimeOrLength();
            LogUtil.d(Constants.LOG_TAG, "==================================================");
            LogUtil.d(Constants.LOG_TAG, "PRINT AFTER SORT TIME");
            printSortedMap(listRaceDetail);
            LogUtil.d(Constants.LOG_TAG, "==================================================");
        }
        else{
            sortByLength();
            int a, b = listRace.size();
            for(a = 0; a< b; a++){
                LogUtil.e(Constants.LOG_TAG, "AFTER SORT Race Length" + a + " has date: " + listRace.get(a).getRaceDate().substring(0,10)
                + "|"+listRace.get(a).getRaceMiles());
            }
            listRaceDetail = parseSortByTimeOrLength();
            LogUtil.d(Constants.LOG_TAG, "==================================================");
            LogUtil.d(Constants.LOG_TAG, "PRINT AFTER SORT LENGTH");
            printSortedMap(listRaceDetail);
            LogUtil.d(Constants.LOG_TAG, "==================================================");
        }
        if (mRacesSortAdapter != null) {
            LogUtil.d(Constants.LOG_TAG, "sort notifyDataSetChanged setData " + listRaceDetail.size());
            mRacesSortAdapter.getData().clear();
            //mRaceList.invalidateViews();
            //mRacesSortAdapter.notifyDataSetChanged();
            mRacesSortAdapter.setData(listRaceDetail);
            mRacesSortAdapter.notifyDataSetChanged();
            //mRaceList.invalidateViews();

        }
        else{
            LogUtil.d(Constants.LOG_TAG, "sort notifyDataSetChanged NULL");
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
        if(mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.show(RacesDetailActivity.this, "", "", false, false);
        }
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

        if (listRaceDetail != null) {
            mEmptyText.setVisibility(View.INVISIBLE);

        } else {

            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    /*private Map<String, List<Race>> sortDataNew(
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

        //returnMap = sortMapNewByValues(returnMap);
        return returnMap;
    }*/

    /*private TreeMap<String, List<Race>> sortMapNewByValues(
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
                                LogUtil.d(Constants.LOG_TAG, "date 1|date 2: " + dateStr1
                                         +"|" + dateStr2);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                Date lDate, rDate;
                                try {
                                    lDate = format.parse(dateStr1);
                                    rDate = format.parse(dateStr2);

                                    LogUtil.d(Constants.LOG_TAG, "parse to compare Sort date: "
                                            + (rDate.compareTo(lDate)));
                                    return rDate.compareTo(lDate);
                                } catch (java.text.ParseException e) {
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare sort date error: " + e.getMessage());
                                }
                                return 0;
                            }
                            else if (selectSort == R.id.races_detail_sort_time) {
                                try{
                                    String[] dateStr1 = lhs.getFinisherTime().split(":");
                                    String[] dateStr2 = rhs.getFinisherTime().split(":");
                                    int lFinishTime = Integer.parseInt(dateStr1[0])*60*60
                                            + Integer.parseInt(dateStr1[1])*60
                                            + Integer.parseInt(dateStr1[2]);
                                    int rFinishTime = Integer.parseInt(dateStr2[0])*60*60
                                            + Integer.parseInt(dateStr2[1])*60
                                            + Integer.parseInt(dateStr2[2]);
                                    LogUtil.d(Constants.LOG_TAG, "date 1|date 2: " + lFinishTime
                                            +"|" + rFinishTime);
                                    LogUtil.d(Constants.LOG_TAG, "parse to compare Sort time: "
                                            + (lFinishTime - rFinishTime));
                                    return (lFinishTime - rFinishTime);
                                }
                                catch (Exception e){
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare Sort time error: " + e.getMessage());
                                }
                                return 0;
                            }
                            else{
                                try{
                                    String mileStr1 = lhs.getRaceMiles();
                                    String mileStr2 = rhs.getRaceMiles();
                                    float lFinishTime = Float.parseFloat(mileStr1);
                                    float rFinishTime = Float.parseFloat(mileStr2);
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare Sort length: "
                                            + (lFinishTime - rFinishTime));

                                    if((lFinishTime - rFinishTime) > 0){
                                        return 1;
                                    }
                                    else if((lFinishTime - rFinishTime) == 0){
                                        return 0;
                                    }
                                    else{
                                        return -1;
                                    }

                                }
                                catch (Exception e){
                                    LogUtil.e(Constants.LOG_TAG, "parse to compare Sort length error: " + e.getMessage());
                                }
                                return 0;
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

    @Override
    public void onLikeItem(Race raceInfo) {
        this.likeRaceId = raceInfo.getId();
        LogUtil.d(Constants.LOG_TAG,"Like click" + raceInfo.getId());
        String userId = CustomSharedPreferences.getPreferences(Constants.PREF_USER_ID, "");
        if(!userId.isEmpty()){
//            int id = Integer.parseInt(userId);
            if(userId != null && userId.length() > 0){
                if(mLoadingDialog == null) {
                    mLoadingDialog = CustomLoadingDialog.show(RacesDetailActivity.this, "", "", false, false);
                    LogUtil.d(Constants.LOG_TAG,"Loading");
                }
                if(isLiked(raceInfo, userId)){
                    //unlike then refresh
                    LogUtil.d(Constants.LOG_TAG,"Liked -> unlike");
                    callLikeRace(raceInfo.getId(),false);
                }
                else{
                    //call add like
                    LogUtil.d(Constants.LOG_TAG,"UnLike -> Like");
                    callLikeRace(raceInfo.getId(),true);
                }
            }
        }
    }

    private void callLikeRace(int raceId, boolean like) {
        if(like){
            AddLikeRequest request = new AddLikeRequest(String.valueOf(raceId));
            request.setListener(callBackEvent);
            new Thread(request).start();
        }
        else{
            UnLikeRequest request = new UnLikeRequest(String.valueOf(raceId));
            request.setListener(callBackEvent);
            new Thread(request).start();
        }
    }

    public boolean isLiked(Race race, String userId){
        boolean result = false;
        List<Like> listLike = race.getLikes();
        LogUtil.d(Constants.LOG_TAG,"User ID Login: " + userId);
        if(listLike != null && listLike.size() >0){
            for(Like like : listLike){
                LogUtil.d(Constants.LOG_TAG,"Like user ID: " + like.getUserID());
                if(like.getUserID().equalsIgnoreCase(userId)){
                    result =  true;
                    break;
                }
                else if(like.getUserID().equalsIgnoreCase(userId)){

                }
            }

        }
        return result;
    }

    public static Bitmap getBitmapFromUrl(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private class ShareImagesAsync extends
            AsyncTask<Race, Void, String> {

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
        protected String doInBackground(Race... paramVarArgs) {
            // TODO Auto-generated method stub
            Race raceInfo = paramVarArgs[0];
            imageUris = new ArrayList<Uri>();
            imageBitmaps = new ArrayList<String>();
            String shareText = "";
            String distance = "";
            String raceDate = "";
            String shoeName = "";
            String userName = "";
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String strSplit = raceInfo.getRaceDate().substring(0, 10);
            Date dateTemp = null;
            try{
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                dateTemp = df1.parse(strSplit);
                Date date =  dateTemp;
                raceDate = df.format(date);
            }
            catch(Exception ex){

            }

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
            String shoeInfo = "";
            if(raceInfo.getShoe() != null){
                Shoe sh= raceInfo.getShoe();
                shoeInfo = (sh.getBrand() +"(" +sh.getModel()+ ")");
            }
			/*if (raceInfo.containsKey(Constants.SHOE.toUpperCase())) {

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
			}*/
            //userName = mUser.getUsername();
            userName = CustomSharedPreferences.getPreferences(Constants.PREF_USERNAME, "");
            String str = raceInfo.getFinisherTime();
            int total =0, hour=0, min= 0, sec =0;
            try{
                String[] str1 = str.split(":");
                hour = Integer.parseInt(String.valueOf(str1[0]));
                min = Integer.parseInt(String.valueOf(str1[1]));
                sec = Integer.parseInt(String.valueOf(str1[2]));
            }
            catch (Exception ex){

            }
			/*int total = (Integer) raceInfo.get(Constants.FINISHTIME);
			int hour = total / 3600;
			int min = (total % 3600) / 60;
			int sec = (total % 60);*/
            if (hour > 0) {

                if (!shoeName.equals("")) {

                    shareText = String
                            .format("%s Ran a %s race in %d hour %d minutes and %d seconds. Race Name is the %s, the race date is %s. The shoes he ran it in is %s",
                                    userName, distance, hour, min, sec,
                                    raceInfo.getName(),raceDate, shoeName);
                } else {

                    shareText = String
                            .format("%s Ran a %s race in %d hour %d minutes and %d seconds. Race Name is the %s, the race date is %s.",
                                    userName, distance, hour, min, sec,
                                    raceInfo.getName(),raceDate);
                }

            } else {

                if (!shoeName.equals("")) {

                    shareText = String
                            .format("%s Ran a %s race in %d minutes and %d seconds. Race Name is the %s, the race date is %s. The shoes he ran it in is %s",
                                    userName, distance, min, sec,
                                    raceInfo.getName(),
                                    raceDate, shoeName);
                } else {

                    shareText = String
                            .format("%s Ran a %s race in %d minutes and %d seconds. Race Name is the %s, the race date is %s.",
                                    userName, distance, min, sec,
                                    raceInfo.getName(),
                                    raceDate);
                }
            }

			/*if (raceInfo.containsKey(Constants.PERSON)) {

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
			}*/

            if((raceInfo.getPersonUrl() != null) && (!raceInfo.getPersonUrl().isEmpty())){
                try {
                    String path = raceInfo.getPersonUrl();
                    Bitmap bmp = getBitmapFromUrl(ServiceApi.SERVICE_URL + path);
                    imageBitmaps.add(path);
                    imageUris.add(0, Utilities.saveBitmap(bmp, "person.jpg"));
                    if (shareType == R.id.dialog_share_twitter) {
                        return shareText;
                    }
                } catch (Exception e) {
                    LogUtil.d("LoadRaceImageAsync", e.getMessage());
                }
            }
			/*if (raceInfo.containsKey(Constants.BIB)) {

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
			}*/
            if((raceInfo.getBibUrl() != null) && (!raceInfo.getBibUrl().isEmpty())){
                try {
                    String path = raceInfo.getBibUrl();
                    Bitmap bmp = getBitmapFromUrl(ServiceApi.SERVICE_URL + path);
                    imageBitmaps.add(path);
                    imageUris.add(0, Utilities.saveBitmap(bmp, "bib.jpg"));
                    if (shareType == R.id.dialog_share_twitter) {
                        return shareText;
                    }
                } catch (Exception e) {
                    LogUtil.d("LoadRaceImageAsync", e.getMessage());
                }
            }

			/*if (raceInfo.containsKey(Constants.MEDAL)) {

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
			}*/
            if((raceInfo.getMedalUrl() != null) && (!raceInfo.getMedalUrl().isEmpty())){
                try {
                    String path = raceInfo.getMedalUrl();
                    Bitmap bmp = getBitmapFromUrl(ServiceApi.SERVICE_URL + path);
                    imageBitmaps.add(path);
                    imageUris.add(0, Utilities.saveBitmap(bmp, "medal.jpg"));
                    if (shareType == R.id.dialog_share_twitter) {
                        return shareText;
                    }
                } catch (Exception e) {
                    LogUtil.d("LoadRaceImageAsync", e.getMessage());
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

                try{
//                    OpenGraphObject myobject = OpenGraphObject.Factory
//                            .createForPost("fitness.course");
//                    myobject.setProperty("title", result);
//                    myobject.setProperty("description", result);
//                    myobject.setProperty("caption", result);
//                    OpenGraphAction action = GraphObject.Factory
//                            .create(OpenGraphAction.class);
//                    action.setProperty("course", myobject);
//                    action.setImageUrls(imageBitmaps);
//                    FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(
//                            RacesDetailActivity.this, action, "fitness.runs",
//                            "course").build();
//                    uiHelper.trackPendingDialogCall(shareDialog.present());
                    Utilities.doShareSocial(RacesDetailActivity.this, "com.facebook.katana", result, imageUris);
                }
                catch(Exception ex){
                    Utilities.showAlertMessage(
                            RacesDetailActivity.this,getString(R.string.share_fb_failed),"");
                }

            } else {
                try{
                    Utilities.doShareSocial(RacesDetailActivity.this, "com.twitter.android", result, imageUris);
                }
                catch(Exception ex){
                    Utilities.showAlertMessage(
                            RacesDetailActivity.this,getString(R.string.share_tw_failed),"");
                }
            }

            dialog.dismiss();
            dialog = null;
        }
    }

    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(RacesDetailActivity.this);
            pDialog.setMessage("Posting to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            try {
                boolean isLoggedIn = CustomSharedPreferences.getPreferences(LoginChoiceScreen.PREF_KEY_TWITTER_LOGIN, false);
                if(isLoggedIn) {
                    // Access Token
                    String access_token = CustomSharedPreferences.getPreferences(LoginChoiceScreen.PREF_KEY_OAUTH_TOKEN, "");
                    // Access Token Secret
                    String access_token_secret = CustomSharedPreferences.getPreferences(LoginChoiceScreen.PREF_KEY_OAUTH_SECRET, "");

                    AccessToken accessToken = new AccessToken(access_token, access_token_secret);


                    ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder = new ConfigurationBuilder();
                    builder.setDebugEnabled(true)
                            .setOAuthConsumerKey(RacesDetailActivity.this.getString(R.string.twitter_consumer_key))
                            .setOAuthConsumerSecret(RacesDetailActivity.this.getString(R.string.twitter_consumer_secret))
                            .setOAuthAccessToken(access_token)
                            .setOAuthAccessTokenSecret(access_token_secret);
                    Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                    // Update status
                    StatusUpdate statusUpdate = new StatusUpdate(status);

                    twitter4j.Status response = twitter.updateStatus(statusUpdate);
                    Log.d("Status", response.getText());
                } else {

                }

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();

            //Toast.makeText(RacesDetailActivity.this, "Posted to Twitter!", Toast.LENGTH_SHORT).show();
        }

    }

}
