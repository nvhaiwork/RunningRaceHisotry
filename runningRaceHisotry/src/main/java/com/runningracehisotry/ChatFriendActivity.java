package com.runningracehisotry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseUser;
import com.runningracehisotry.adapters.FriendAdapter;
import com.runningracehisotry.adapters.RunnersAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Friend;
import com.runningracehisotry.models.Group;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.models.User;
import com.runningracehisotry.service.SinchService;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetAllGroupUserRequest;
import com.runningracehisotry.webservice.base.GetGroupMemberRequest;
import com.runningracehisotry.webservice.base.GetUserProfileRequest;
import com.sinch.android.rtc.SinchError;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatFriendActivity extends BaseActivity implements ServiceConnection, SinchService.StartFailedListener {

    private ListView mFriendListview;
    private FriendAdapter mFriendAdapter;
    private int totalFriends, returnedFriends;
    private List<Group> lstGroup = new ArrayList<Group>();
    private List<Friend> lstFriend = new ArrayList<Friend>();

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private int selectedPosition = -1;

    @Override
    protected int addContent() {
        return R.layout.activity_friends;
    }

    @Override
    protected void initView() {
        bindService(new Intent(this, SinchService.class), connection,
                BIND_AUTO_CREATE);

        super.initView();

        mFriendListview = (ListView) findViewById(R.id.friends_list);
        mFriendListview.setOnItemClickListener(this);

        getGroupOfUser();
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        super.onItemClick(adapterView, view, position, id);

        if (adapterView.getId() == R.id.friends_list) {

            selectedPosition = position;

            if (!mSinchServiceInterface.isStarted()) {
                mSinchServiceInterface.startClient();
            } else {
                goToChat();
            }
        }
    }

    private void goToChat() {
        Intent selectRaceIntent = new Intent(ChatFriendActivity.this,
                ChatActivity.class);

        selectRaceIntent.putExtra(
                Constants.INTENT_SELECT_CHAT_FRIEND, serializeObject(lstFriend.get(selectedPosition).getFriend()));
        startActivity(selectRaceIntent);
    }

    @Override
    public void onBackPressed() {
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
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private void getFriendOfUser(String groupId) {
        GetGroupMemberRequest request = new GetGroupMemberRequest(groupId);
        request.setListener(callBackEvent);
        new Thread(request).start();
        LogUtil.d(Constants.LOG_TAG, "getFriendOfUser call: " + groupId);
    }

    private void processGetFriendGroupOfUser(String json) {
        try{
            LogUtil.d(Constants.LOG_TAG, "processGetFriendGroupOfUser return: " + json);
            Gson gson = new Gson();
            //Type listType = new TypeToken<Friend>(){}.getType();
            JSONArray arr= new JSONArray(json);
            JSONObject obj = arr.getJSONObject(0);
            LogUtil.d(Constants.LOG_TAG, "processGetFriendGroupOfUser return after: " + obj.toString());
            Friend fr  = gson.fromJson(obj.toString(), Friend.class);

            LogUtil.d(Constants.LOG_TAG, "Friend return: " + fr.getFriend().getFull_name()
                    + "|" + fr.getFriend().getProfile_image());
            LogUtil.d(Constants.LOG_TAG, "return|total: " + returnedFriends +"|"+ totalFriends);

            lstFriend.add(fr);

            if(returnedFriends < totalFriends){
                returnedFriends++;
                LogUtil.d(Constants.LOG_TAG, "return < add " + returnedFriends +"|"+ totalFriends);
            }
            else{
                mFriendAdapter = new FriendAdapter(this, lstFriend, mImageLoader);
                mFriendListview.setAdapter(mFriendAdapter);
                mFriendAdapter.notifyDataSetChanged();
                LogUtil.d(Constants.LOG_TAG, "return >= show "  + returnedFriends +"|"+ totalFriends);
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, Object data) {
            LogUtil.i(Constants.LOG_TAG, data.toString());
            if (methodName.equals(ServiceConstants.METHOD_GET_ALL_GROUP)) {
                try {
                    // Login success
                    processGetGroupOfUser(data.toString());
                } catch (Exception e) {

                } finally {
                }
            }
            else if (methodName.equals(ServiceConstants.METHOD_GET_GROUP_OF_USER)) {
                try {
                    // Login success
                    processGetFriendGroupOfUser(data.toString());
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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            mSinchServiceInterface.setStartListener(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("a", name.toShortString());

    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {
        goToChat();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (SinchService.class.getName().equals(componentName.getClassName())) {
                mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
                mSinchServiceInterface.setStartListener(ChatFriendActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("a", name.toShortString());
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
