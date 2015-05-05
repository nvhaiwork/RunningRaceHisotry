package com.runningracehisotry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runningracehisotry.adapters.FriendAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Friend;
import com.runningracehisotry.models.Group;
import com.runningracehisotry.utilities.LogUtil;

import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetAllGroupUserRequest;
import com.runningracehisotry.webservice.base.GetGroupMemberRequest;
import android.content.Intent;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;

import org.json.JSONObject;

public class FriendsActivity extends BaseActivity {

    private ListView mFriendListview;
    private FriendAdapter mFriendAdapter;
    private int totalFriends, returnedFriends;
    private List<Group> lstGroup = new ArrayList<Group>();
    private List<Friend> lstFriend = new ArrayList<Friend>();
    private CustomLoadingDialog mLoadingDialog;


    @Override
    protected int addContent() {
        // TODO Auto-generated method stub
        return R.layout.activity_friend;
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();

        mFriendListview = (ListView) findViewById(R.id.lv_friends);
        mFriendListview.setOnItemClickListener(this);

        mFriendAdapter = new FriendAdapter(this, mImageLoader);
        mFriendListview.setAdapter(mFriendAdapter);

        // Initiation data
        // new LoadFriendsAsyncTask().execute();
        if(mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.show(FriendsActivity.this, "", "", false, false);
        }

        getGroupOfUser();
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        // TODO Auto-generated method stub
        super.onItemClick(adapterView, view, position, id);

        if (adapterView.getId() == R.id.lv_friends) {

            Intent selectRaceIntent = new Intent(FriendsActivity.this,
                    SelectRaceActivity.class);
            selectRaceIntent.putExtra(
                    Constants.INTENT_SELECT_RACE_FROM_FRIENDS, position);
            startActivity(selectRaceIntent);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        backToHome();
    }

    private void getGroupOfUser() {
        GetAllGroupUserRequest request = new GetAllGroupUserRequest();
        request.setListener(callBackEvent);
        new Thread(request).start();
        LogUtil.d(Constants.LOG_TAG, "getGroupOfUser call");
    }
    private void processGetGroupOfUser(String json) {

        try{
            LogUtil.d(Constants.LOG_TAG, "processGetGroupOfUser return: " +json);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Group>>(){}.getType();
            lstGroup = gson.fromJson(json, listType);
            if(lstGroup.size() > 0){
                totalFriends = lstGroup.size() - 1;
                LogUtil.d(Constants.LOG_TAG, "processGetGroupOfUser total: " + (totalFriends + 1));
                for(Group group : lstGroup){
                    getFriendOfUser(group.getGroupId());
                }
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
        }
        catch(Exception ex){
            //ex.printStackTrace();
        }

    }

    private void getFriendOfUser(int groupId) {
        GetGroupMemberRequest request = new GetGroupMemberRequest(groupId);
        request.setListener(callBackEvent);
        new Thread(request).start();
        LogUtil.d(Constants.LOG_TAG, "getFriendOfUser call: " + groupId);
    }

    private void processGetFriendGroupOfUser(String json) {
        try{
            LogUtil.d(Constants.LOG_TAG, "processGetFriendGroupOfUser return: " + json);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Friend>>(){}.getType();
            JSONArray arr= new JSONArray(json);
            JSONObject obj = arr.getJSONObject(0);
            LogUtil.d(Constants.LOG_TAG, "processGetFriendGroupOfUser return after: " + obj.toString());
            Friend fr  = gson.fromJson(obj.toString(), Friend.class);
            LogUtil.d(Constants.LOG_TAG, "Friend return: " + fr.getFriend().getFull_name()
                    + "|" + fr.getFriend().getProfile_image());
            LogUtil.d(Constants.LOG_TAG, "return|total: " + returnedFriends +"|"+ totalFriends);

            lstFriend.add(fr);

            List<Friend> listFriend = gson.fromJson(json, listType);

            mFriendAdapter.addItem(listFriend);

            if(returnedFriends<totalFriends){
                returnedFriends += listFriend.size();

                LogUtil.d(Constants.LOG_TAG, "return < add " + returnedFriends +"|"+ totalFriends);
            }
            else{

                LogUtil.d(Constants.LOG_TAG, "return >= show "  + returnedFriends +"|"+ totalFriends);
                try{
                    if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                }
                catch(Exception ex){
                }
            }

            /*if(lstFriend != null && lstFriend.size() > 0){
                //show data
                mFriendAdapter = new FriendAdapter(this, lstFriend, mImageLoader);
                mFriendListview.setAdapter(mFriendAdapter);
                mFriendAdapter.notifyDataSetChanged();
                for(Friend fr : lstFriend){
                    LogUtil.d(Constants.LOG_TAG, "Friend return: " + fr.getFriend().getFull_name()
                            + "|" + fr.getFriend().getProfile_image());
                }
            }*/
        }
        catch(Exception ex){
            try{
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
            catch(Exception exc){
            }
            //ex.printStackTrace();
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
        public void Wsdl2CodeFinished(String methodName, Object data) {
            LogUtil.i(Constants.LOG_TAG, data.toString());
            if (methodName.equals(ServiceConstants.METHOD_GET_ALL_GROUP)) {
                try {
                    // Login success
                    processGetGroupOfUser(data.toString());
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
            else if (methodName.equals(ServiceConstants.METHOD_GET_GROUP_OF_USER)) {
                try {
                    // Login success
                    processGetFriendGroupOfUser(data.toString());
                } catch (Exception e) {

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
	/*private class LoadFriendsAsyncTask extends AsyncTask<Void, Void, Void> {

		Dialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(FriendsActivity.this, "", "",
					false, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			List<ParseUser> errorFriend = new ArrayList<ParseUser>();
			for (ParseUser friend : mFriends) {

				try {

					friend.fetchIfNeeded();
				} catch (Exception e) {

					errorFriend.add(friend);
					LogUtil.e("Load friend async", e.getMessage());
				}
			}

			mFriends.removeAll(errorFriend);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (mFriendAdapter == null) {

				mFriendAdapter = new RunnersAdapter(FriendsActivity.this,
						mFriends, mImageLoader);
				mFriendListview.setAdapter(mFriendAdapter);
			} else {

				mFriendAdapter.notifyDataSetChanged();
			}
			dialog.dismiss();
		}
	}*/
}
