package com.runningracehisotry;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runningracehisotry.adapters.FriendChatAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Friend;
import com.runningracehisotry.models.Group;
import com.runningracehisotry.models.HistoryConversation;
import com.runningracehisotry.models.Runner;
import com.runningracehisotry.service.MessageService;
import com.runningracehisotry.service.SinchService;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetAllGroupUserRequest;
import com.runningracehisotry.webservice.base.GetGroupMemberRequest;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatFriendActivity extends BaseActivity implements SinchService.StartFailedListener {

    private ExpandableListView mFriendListview;
    private FriendChatAdapter mFriendAdapter;
    private int totalFriends, returnedFriends;
    private List<Group> lstGroup = new ArrayList<Group>();
    private List<String> lstFriendNew = new ArrayList<String>();
    private Map<Integer, List<Friend>> friendMap;
    private Map<String, List<com.runningracehisotry.models.Message>> messageMap = new HashMap<String, List<com.runningracehisotry.models.Message>>();

    private CustomLoadingDialog mLoadingDialog;

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private static final String TAG = "ChatFriendActivity";

    private int selectedPosition = -1;
    private int selectedGroupPosition = -1;

    private SearchView searchView;

    private boolean isProcessing;


    protected  void updateNotificationChat(String strFriend){
        Log.d(Constants.LOG_TAG,"New Chat Broadcast SUB updatew GUI ");
        //update lstFriendNew and friendMap
        HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
        long insertDb = dao.addFriendNewMessage(strFriend);
        Log.d(Constants.LOG_TAG,"New Chat Broadcast SUB updatew GUI INSERT NEW: " + insertDb);
        lstFriendNew = dao.getListNewMessage();
        List<Friend> tempFriend = new ArrayList<Friend>();
        List<Group> groups = new ArrayList<Group>();
        Map<Integer, List<Friend>> tempMap = new HashMap<Integer, List<Friend>>();
        if(friendMap != null && friendMap.size()>0) {
            /*LogUtil.d(Constants.LOG_TAG, "listNew: " + !newText.trim().isEmpty());
            if(newText != null && !newText.trim().isEmpty()) {*/
                //LogUtil.d(Constants.LOG_TAG, "listNew siz");
                for(Integer id: friendMap.keySet()){
                    List<Friend> friends = friendMap.get(id);
                    for (Friend friend : friends) {
                        if(lstFriendNew != null && lstFriendNew.size()>0){
                            LogUtil.d(Constants.LOG_TAG, "Has new chat, check and set");
                            //if(friend.getFriend().getName().equalsIgnoreCase("")) {
                            for(String friendNew : lstFriendNew){
                                if(friendNew.equalsIgnoreCase(friend.getFriend().getName())){
                                    LogUtil.d(Constants.LOG_TAG, "Set new for Friend ID|Name:" + friend.getFriendId()
                                    + friendNew);
                                    friend.setNewMessage(1);
                                }
                            }
                        }

                        tempFriend.add(friend);
                        if (tempMap.containsKey(friend.getGroupId())) {
                            tempMap.get(friend.getGroupId()).add(friend);
                        } else {
                            List<Friend> tempListFriend = new ArrayList<Friend>();
                            tempListFriend.add(friend);
                            tempMap.put(friend.getGroupId(), tempListFriend);
                        }

                    }

                }

                for (Integer groupid : tempMap.keySet()) {
                    for (Group g : lstGroup) {
                        if(g.getGroupId() == groupid) {
                            groups.add(g);
                        }
                    }
                }

//                tempMap.put(Integer.valueOf(-1), tempFriend);
                synchronized (tempMap) {
                    mFriendAdapter.setData(groups, tempMap);
                    expandAllGroup();
                }
            //}
        }
        String newText = searchView.getQuery().toString();
        LogUtil.d(Constants.LOG_TAG, "Refresh List with query string: " + newText);
        if(newText.length() > 0) {
            LogUtil.d(Constants.LOG_TAG, "Refresh List with query string: " + newText);
            filterRunner(newText);
        } else {
            LogUtil.d(Constants.LOG_TAG, "Refresh List with no query string");
            mFriendAdapter.setData(lstGroup, friendMap);
            expandAllGroup();
        }
    }
    private BroadcastReceiver listenerChatSub = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() != null){
                if(intent.getAction().equalsIgnoreCase(BaseActivity.actionNewForChatActivity)){
                    Log.d(Constants.LOG_TAG,"New Chat Broadcast SUB received");
                    CustomSharedPreferences.setPreferences(Constants.PREF_NEW_NOTIFICATION_CHAT, 1);
                    String friend = intent.getStringExtra(Constants.INCOMMING_BROADCAST_FRIEND_NAME_EXTRA);
                    if(friend != null && !friend.isEmpty()){
                        updateNotificationChat(friend);


                    }

                    //super.onReceivedNewMessage();
                    /*if(mMenu.isMenuShowing()){
                        Log.d(Constants.LOG_TAG,"New Chat Broadcast when showing menu: refresh it and show again");
                        refreshLeftMenu(true);
                        mMenu.toggle();
                    }*/
                }
            }
        }
    };

    private SearchView.OnQueryTextListener listenerSearch =  new SearchView.OnQueryTextListener()
    {
        @Override
        public boolean onQueryTextSubmit(String query) {
            LogUtil.d(Constants.LOG_TAG,"String query: " + query +" close keyboard");
            searchView.setIconified(true);
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // TODO Auto-generated method stub
//            filterRunner(newText);
            //filter and show
            if(newText.length() > 0) {
                filterRunner(newText);
            } else {
                mFriendAdapter.setData(lstGroup, friendMap);
                expandAllGroup();
            }
            return false;
        }
    };

    private synchronized void filterRunner(String newText) {
        List<Group> groups = new ArrayList<Group>();
//        Group group = new Group();
//        group.setGroupId(-1);
//        groups.add(group);
        List<Friend> tempFriend = new ArrayList<Friend>();
        Map<Integer, List<Friend>> tempMap = new HashMap<Integer, List<Friend>>();
        if(friendMap != null && friendMap.size()>0) {
            LogUtil.d(Constants.LOG_TAG, "listNew: " + !newText.trim().isEmpty());
            if(newText != null && !newText.trim().isEmpty()) {
                LogUtil.d(Constants.LOG_TAG, "listNew siz");
                for(Integer id: friendMap.keySet()){
                    List<Friend> friends = friendMap.get(id);
                    for (Friend friend : friends) {
                        if(friend.getFriend().getFull_name().toLowerCase().contains(newText.toLowerCase())) {
                            tempFriend.add(friend);
                            if(tempMap.containsKey(friend.getGroupId())) {
                                tempMap.get(friend.getGroupId()).add(friend);
                            } else {
                                List<Friend> tempListFriend = new ArrayList<Friend>();
                                tempListFriend.add(friend);
                                tempMap.put(friend.getGroupId(), tempListFriend);
                            }
                        }
                    }

                }

                for (Integer groupid : tempMap.keySet()) {
                    for (Group g : lstGroup) {
                        if(g.getGroupId() == groupid) {
                            groups.add(g);
                        }
                    }
                }

//                tempMap.put(Integer.valueOf(-1), tempFriend);
                synchronized (tempMap) {
                    mFriendAdapter.setData(groups, tempMap);
                    expandAllGroup();
                }
            }
        }
    }

    @Override
    protected int addContent() {
        return R.layout.activity_chat_friend;
    }

    @Override
    protected void initView() {
        bindService(new Intent(getApplicationContext(), SinchService.class), connection,
                BIND_AUTO_CREATE);

        super.initView();

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mFriendAdapter.setData(lstGroup, friendMap);
                expandAllGroup();
                return true;
            }
        });

//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchView.onActionViewExpanded();
//            }
//        });
        searchView.setOnQueryTextListener(listenerSearch);
        searchView.setQueryRefinementEnabled(true);

        mFriendListview = (ExpandableListView) findViewById(R.id.friends_list);
//        mFriendListview.setOnItemClickListener(this);
        mFriendListview.setGroupIndicator(null);

        mFriendListview.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                mFriendListview.expandGroup(groupPosition);
            }
        });

        mFriendListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(!isProcessing) {
                    isProcessing = true;
                    selectedPosition = childPosition;
                    selectedGroupPosition = groupPosition;

//                if (!mSinchServiceInterface.isStarted()) {
//                    Log.d(TAG, "mSinchServiceInterface.isStarted(): false");
//                    String userName = mFriendAdapter.getChild(selectedGroupPosition, selectedPosition).getFriend().getName();
//                    mSinchServiceInterface.startClient(RunningRaceApplication.getInstance().getCurrentUser().getName());
//                } else {
//                    Log.d(TAG, "mSinchServiceInterface.isStarted(): true");
//                    goToChat();
//                }

//                String userName = mFriendAdapter.getChild(selectedGroupPosition, selectedPosition).getFriend().getName();
//                mSinchServiceInterface.startClient(RunningRaceApplication.getInstance().getCurrentUser().getName());

                    goToChat();
                }

                return false;
            }
        });

        friendMap = new HashMap<Integer, List<Friend>>();
        HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
        lstFriendNew = dao.getListNewMessage();
        if(lstFriendNew != null && lstFriendNew.size()>0){
            LogUtil.d(Constants.LOG_TAG, "Has new chat");
        }
        else{
            LogUtil.d(Constants.LOG_TAG, "No new chat");
            //add fake to test
            //lstFriendNew.add("manhdn2");
        }
        getGroupOfUser();

        if(listenerChatSub != null) {
            registerReceiver(listenerChatSub, new IntentFilter("com.runningracehisotry.chat.incomming.chat.activity"));
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        super.onItemClick(adapterView, view, position, id);

//        if (adapterView.getId() == R.id.friends_list) {
//
//            selectedPosition = position;
//
//            if (!mSinchServiceInterface.isStarted()) {
//                mSinchServiceInterface.startClient();
//            } else {
//                goToChat();
//            }
//        }
    }

    private void goToChat() {
        //delete New for this friend
        HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
        String friend = mFriendAdapter.getChild(selectedGroupPosition, selectedPosition).getFriend().getName();
        long result = dao.deleteFriendNewMessage(friend);
        LogUtil.d(Constants.LOG_TAG, "Delete NEw chat for " + friend + " is OK(true)??" + (result >0));
        Intent selectRaceIntent = new Intent(ChatFriendActivity.this,
                ChatActivity.class);

        selectRaceIntent.putExtra(
                Constants.INTENT_SELECT_CHAT_FRIEND, serializeObject(mFriendAdapter.getChild(selectedGroupPosition, selectedPosition).getFriend()));
        startActivity(selectRaceIntent);

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        backToHome();
    }

    private void getGroupOfUser() {
        if(mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.show(this, "", "", false, false);
        }

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
                mFriendAdapter = new FriendChatAdapter(this, lstGroup, mImageLoader);
                mFriendListview.setAdapter(mFriendAdapter);

                totalFriends = lstGroup.size() - 1;
                LogUtil.d(Constants.LOG_TAG, "processGetGroupOfUser total: " + (totalFriends + 1));
                for(Group group : lstGroup){
                    getFriendOfUser(group.getGroupId());
                }
            } else {
                if(mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        }
        catch(Exception ex){
            //ex.printStackTrace();
            if(mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
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
            // add new icon for list when initial
            List<Friend> listFriend = gson.fromJson(json, listType);
            List<Friend> listFriendAdd = new ArrayList<Friend>(listFriend);
            if(lstFriendNew != null && lstFriendNew.size()>0){
                for(int i = 0; i< listFriend.size(); i++){
                    for(String friendNew : lstFriendNew){
                        if(friendNew.equalsIgnoreCase(listFriend.get(i).getFriend().getName())){
                            listFriendAdd.get(i).setNewMessage(1);
                        }
                    }
                }
            }
            else{
                LogUtil.d(Constants.LOG_TAG, "NO Friend NEW");
            }

            mFriendAdapter.addItem(listFriendAdd);
            expandAllGroup();

            friendMap.put(listFriend.get(0).getGroupId(), listFriend);

//            if(friendMap.containsKey(fr.getGroupId())) {
//                friendMap.get(fr.getGroupId()).add(fr);
//            } else {
//                List<Friend> list = new ArrayList<Friend>();
//                list.add(fr);
//                friendMap.put(fr.getGroupId(), list);
//
//            }

//            lstFriend.add(fr);

            if(returnedFriends < totalFriends){
                returnedFriends += listFriend.size();
                LogUtil.d(Constants.LOG_TAG, "return < add " + returnedFriends +"|"+ totalFriends);
            }
            else{
//                mFriendAdapter = new FriendChatAdapter(this, friendMap, lstGroup, mImageLoader);
//                mFriendListview.setAdapter(mFriendAdapter);
//                for(int i=0; i < mFriendAdapter.getGroupCount(); i++) {
//                    mFriendListview.expandGroup(i);
//                }
//
//                mFriendAdapter.notifyDataSetChanged();
                LogUtil.d(Constants.LOG_TAG, "return >= show "  + returnedFriends +"|"+ totalFriends);

                if(mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }

        }
        catch(Exception ex){
            //ex.printStackTrace();
            if(mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        }
    }

    private void expandAllGroup() {
        for(int i=0; i < mFriendAdapter.getGroupCount(); i++) {
            mFriendListview.expandGroup(i);
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
                    if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
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
    public void onStartFailed(SinchError error) {
        Log.d("a", error.toString());
    }

    @Override
    public void onStarted() {
//        mSinchServiceInterface.addMessageClientListener(new MessageClientListener() {
//            @Override
//            public void onIncomingMessage(MessageClient messageClient, Message message) {
//                Log.d(TAG, "onIncomingMessage: " + message.getTextBody());
//            }
//
//            @Override
//            public void onMessageSent(MessageClient messageClient, Message message, String s) {
//                Log.d(TAG, "onMessageSent: " + message.getTextBody());
//            }
//
//            @Override
//            public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
//
//            }
//
//            @Override
//            public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
//                Log.d(TAG, "onMessageDelivered: " + messageDeliveryInfo.getMessageId());
//            }
//
//            @Override
//            public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
//
//            }
//        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (SinchService.class.getName().equals(componentName.getClassName())) {
                mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
                mSinchServiceInterface.setStartListener(ChatFriendActivity.this);
//                mSinchServiceInterface.startClient(RunningRaceApplication.getInstance().getCurrentUser().getName());
                registerInBackground();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("a", name.toShortString());
        }
    };

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                String regid = "";
                regid = CustomSharedPreferences.getPreferences(Constants.PREF_GCM_DEVICE_ID, "");
                if(regid.length() == 0) {
                    try {
                        String androidID = "984219596580";

                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(RunningRaceApplication.getInstance());
                        regid = gcm.register(androidID);
                        msg = "Device registered, registration ID=" + regid;


                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String regid) {
                if(regid.length() > 0) {
                    CustomSharedPreferences.setPreferences(Constants.PREF_GCM_DEVICE_ID, regid);
                }

                mSinchServiceInterface.startClient(RunningRaceApplication.getInstance().getCurrentUser().getName());
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if(listenerChatSub != null) {
            unregisterReceiver(listenerChatSub);
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isProcessing = false;
    }
}
