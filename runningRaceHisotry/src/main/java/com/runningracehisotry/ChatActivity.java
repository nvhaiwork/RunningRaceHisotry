package com.runningracehisotry;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.runningracehisotry.adapters.ChatItemAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Message;
import com.runningracehisotry.models.User;
import com.runningracehisotry.service.SinchService;
import com.runningracehisotry.views.CustomFontTextView;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;


import java.util.List;




public class ChatActivity extends BaseActivity implements ServiceConnection, MessageClientListener {


    private ListView lvMessages;
    private CustomFontTextView tvDes;
    private EditText etMessage;
    private Button btnSend;
    private ChatItemAdapter mChatItemAdaper;
    private User currentFriend;


    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private static final String TAG = SinchService.class.getSimpleName();


    @Override
    protected int addContent() {
        return R.layout.activity_chat;
    }


    @Override
    protected void initView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        getExtraFriend();


        super.initView();


        if(currentFriend != null) {
            etMessage = (EditText) findViewById(R.id.et_message);
            btnSend = (Button) findViewById(R.id.btn_send);
            tvDes = (CustomFontTextView) findViewById(R.id.tv_des);
            tvDes.setText("Chat with " + currentFriend.getFull_name());
            btnSend.setOnClickListener(this);


            lvMessages = (ListView) findViewById(R.id.lv_message);
            mChatItemAdaper = new ChatItemAdapter(this, mImageLoader, currentFriend);
            lvMessages.setAdapter(mChatItemAdaper);


            etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus && mChatItemAdaper.getCount() > 0) {
                        lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
                    }
                }
            });
        }


        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
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
        Log.d(TAG, "onIncomingMessage: " + message.getTextBody());
        Message messageObject = new Message(currentFriend.getId(), message.getTextBody());
        mChatItemAdaper.addMessage(messageObject);
        lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
    }


    @Override
    public void onMessageSent(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, String s) {
        Log.d(TAG, "onMessageSent: " + message.getTextBody());
        Message newMessage = new Message(RunningRaceApplication.getInstance().getCurrentUser().getId(), message.getTextBody());
        mChatItemAdaper.addMessage(newMessage);
        lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
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