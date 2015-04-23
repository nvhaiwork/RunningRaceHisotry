package com.runningracehisotry;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.runningracehisotry.adapters.ChatItemAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Message;
import com.runningracehisotry.models.User;
import com.runningracehisotry.views.CustomFontTextView;


public class ChatActivity extends BaseActivity {

    private ListView lvMessages;
    private CustomFontTextView tvDes;
    private EditText etMessage;
    private Button btnSend;
    private ChatItemAdapter mChatItemAdaper;
    private User currentFriend;

    @Override
    protected int addContent() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
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
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            if(message != null || message.length() > 0) {
                sendMessage(message);
            }
        }
        super.onClick(v);
    }

    private void sendMessage(String message) {
        Message newMessage = new Message(RunningRaceApplication.getInstance().getCurrentUser().getId(), message);
        mChatItemAdaper.addMessage(newMessage);

        etMessage.setText("");
        lvMessages.setSelection(mChatItemAdaper.getCount() - 1);
    }
}
