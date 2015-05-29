package com.runningracehisotry;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.runningracehisotry.adapters.ChatItemAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.HistoryConversation;
import com.runningracehisotry.models.Message;
import com.runningracehisotry.models.User;
import com.runningracehisotry.service.SinchService;
import com.runningracehisotry.views.CustomFontTextView;
import com.runningracehisotry.webservice.ServiceApi;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;


import java.io.File;
import java.util.List;




public class ChatActivity extends BaseActivity implements ServiceConnection, MessageClientListener {


    private ListView lvMessages;
    private CustomFontTextView tvDes;
    private EditText etMessage;
    private ImageView btnSend;
    private ChatItemAdapter mChatItemAdaper;
    private User currentFriend;

    private InputMethodManager imm;
    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private static final String TAG = SinchService.class.getSimpleName();
    private String loggedUserId;

    public static final String  DATABASE_FILE_PATH      = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String  DATABASE_PARENT_FOLDER = "com.runningracehisotry";
    public static final String  DATABASE_NAME      = "Running";
    @Override
    protected int addContent() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
        /*getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);*/

    }

    /**
     * Internal class
     * @author quynt3
     */
    private class MyFocusChangeListener implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Log.d(Constants.LOG_TAG, "edittext has focus");
                Log.d(Constants.LOG_TAG, "HIDE CATEGORY onFocusChange");
//                imm.showSoftInput(etMessage, 0);
                if(mChatItemAdaper.getCount() > 0) {
                    lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
                }

            }
            else {
                Log.e(Constants.LOG_TAG, "edittext has not focus");

//                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();
        this.loggedUserId = RunningRaceApplication.getInstance().getCurrentUser().getName();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
//        imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        getExtraFriend();

        if(currentFriend != null) {
            etMessage = (EditText) findViewById(R.id.et_message);
            btnSend = (ImageView) findViewById(R.id.btn_send);
            tvDes = (CustomFontTextView) findViewById(R.id.tv_des);
            tvDes.setText("Chat with " + currentFriend.getFull_name());
            btnSend.setOnClickListener(this);


            lvMessages = (ListView) findViewById(R.id.lv_message);
            mChatItemAdaper = new ChatItemAdapter(this, mImageLoader, currentFriend);
            lvMessages.setAdapter(mChatItemAdaper);


            /*etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus && mChatItemAdaper.getCount() > 0) {
                        lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
                    }
                }
            });*/
            Log.e(Constants.LOG_TAG, "IMAGE PROFILE FRIEND URL: " + ServiceApi.SERVICE_URL + currentFriend.getProfile_image());
            etMessage.setOnFocusChangeListener(new MyFocusChangeListener());
        }


        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);

        File myFilesDir = null;
        try{
            myFilesDir = new File(DATABASE_FILE_PATH + File.separator
                    + DATABASE_PARENT_FOLDER);
            myFilesDir.mkdirs();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        getHistoryChat();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void getHistoryChat() {
        HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
        String meId = RunningRaceApplication.getInstance().getCurrentUser().getName();
        List<Message> listMes = dao.getMessageFromUserId(meId, meId);;
        if(currentFriend != null) {
            listMes = dao.getMessageFromUserId(meId, currentFriend.getName());;
        }
        if(listMes != null && listMes.size()>0){
            mChatItemAdaper.setMessages(listMes);
            mChatItemAdaper.notifyDataSetChanged();
            lvMessages.setSelection(listMes.size()-1);
            for(Message m: listMes){
                //Log.d(TAG, "History message: " + m.getMessageId() + "||" + m.getContent());
                if(currentFriend.getName().equalsIgnoreCase(m.getOwnerId())){
                    Log.e(Constants.LOG_TAG, "IMAGE PROFILE FRIEND: " + ServiceApi.SERVICE_URL + currentFriend.getProfile_image());
                }
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void getExtraFriend() {
        String extraFrienString = getIntent().getExtras().getString(Constants.INTENT_SELECT_CHAT_FRIEND, "");
        if(extraFrienString.length() > 0) {
            Gson gson = new Gson();
            currentFriend = gson.fromJson(extraFrienString, User.class);
        }
    }


    @Override
    public void onClick(View v) {


        if(v.getId() == R.id.btn_send) {
            String message = etMessage.getText().toString().trim();
            if(message != null && message.length() > 0) {
                sendMessage(message);
            }
        }
        super.onClick(v);
    }


    private void sendMessage(String message) {
        mSinchServiceInterface.sendMessage(currentFriend.getName(), message);
        etMessage.setText("");


    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected");
        mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
        mSinchServiceInterface.addMessageClientListener(this);
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        mSinchServiceInterface.stopClient();
        mSinchServiceInterface = null;

    }


    @Override
    public void onIncomingMessage(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message) {
        Log.d(TAG, "onIncomingMessage: " + message.getTextBody() + "\nsender: " + message.getSenderId() + "\nreceiver: " + message.getRecipientIds().contains(currentFriend.getName()));
        String msgId = message.getMessageId();
        List<String> list = message.getRecipientIds();
        String userIdDb = RunningRaceApplication.getInstance().getCurrentUser().getName();
        /*if(list != null && list.size()>0){
            for(String str : list){
                if(str.equalsIgnoreCase(loggedUserId)){
                    userIdDb = str;
                    break;
                }
            }
        }*/
        String friendIdDb = message.getSenderId();
        String content = message.getTextBody();
        long sentTime = message.getTimestamp().getTime();
        String ownerId = currentFriend.getName();
        Log.d(TAG, "onIncomingMessage ID|SENder|friend(ME)|content|time|owner: "
                + msgId + "|" + userIdDb + "|" + friendIdDb + "|" + content + "|" + sentTime + "|"+ ownerId);
        if(userIdDb != null) {
            //Message messageObject = new Message(currentFriend.getId(), message.getTextBody());
            Message messageObject = new Message(msgId, userIdDb, friendIdDb, content, sentTime, ownerId);
            if(message.getSenderId().equals(currentFriend.getName())) {
                mChatItemAdaper.addMessage(messageObject);
            }

            lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
            //store DB when received
            HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
            long result = dao.addMessage(messageObject);
            Log.d(TAG, "onIncomingMessage add DB result|| message ID: " + result + "||" + message.getMessageId());
        }



    }


    @Override
    public void onMessageSent(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, String s) {
        Log.d(TAG, "onMessageSent content: " + message.getTextBody()+ "\nsender: " + message.getSenderId() + "\nreceiver: " + message.getRecipientIds().contains(currentFriend.getName()));
        String msgId = message.getMessageId();
        String userIdDb = loggedUserId;
        String friendIdDb = message.getRecipientIds().get(0);
        String content = message.getTextBody();
        long sentTime = message.getTimestamp().getTime();
        String ownerId = loggedUserId;
        Log.d(TAG, "onMessageSent ID|SENder|friend|content|time|owner: "
        + msgId + "|"+ userIdDb + "|"+ friendIdDb + "|"+ content + "|"+ sentTime + "|"+ ownerId);
        //Message newMessage = new Message(loggedUserId, message.getTextBody());
        Message messageObject = new Message(msgId, userIdDb, friendIdDb, content, sentTime, ownerId);
        if(message.getRecipientIds().contains(currentFriend.getName())) {
            mChatItemAdaper.addMessage(messageObject);
            lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
        }

        //store DB when received
        HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
        long result = dao.addMessage(messageObject);
        Log.d(TAG, "onMessageSent add DB result|| message ID: " + result +"||" + message.getMessageId());
    }


    @Override
    public void onMessageFailed(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, MessageFailureInfo messageFailureInfo) {
        Log.d(TAG, "onMessageFailed: " + message.getTextBody());
    }


    @Override
    public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
        Log.d(TAG, "onMessageDelivered: " + messageDeliveryInfo.getMessageId());

    }


    @Override
    public void onShouldSendPushData(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, List<PushPair> pushPairs) {
        Log.d(TAG, "onShouldSendPushData: " + message.getTextBody());

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        getApplicationContext().unbindService(this);
//    }

    @Override
    public void finish() {
        super.finish();
//        getApplicationContext().unbindService(this);
    }
}