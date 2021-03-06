package com.runningracehisotry.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.runningracehisotry.ChatFriendActivity;
import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.HistoryConversation;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;
import com.sinch.android.rtc.*;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class SinchService extends Service {

    private static final String APP_KEY = "8c362025-b04a-45c6-bf4c-317936006f47";
    private static final String APP_SECRET = "nQt8aQukK0asaOKPjAXvNQ==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";

    private static final String TAG = SinchService.class.getSimpleName();

    private final SinchServiceInterface mServiceInterface = new SinchServiceInterface();

    private SinchClient mSinchClient = null;
    private StartFailedListener mListener;
    private String userName;

    public class SinchServiceInterface extends Binder {

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String user) {
            start(user);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public void sendMessage(String recipientUserId, String textBody) {
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            SinchService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            SinchService.this.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        if(!isStarted()) {
//            mServiceInterface.startClient();
//        }
        return mServiceInterface;
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    public void sendMessage(String recipientUserId, String textBody) {
        LogUtil.d(TAG, "call sendMessage: " + textBody);
        if (isStarted()) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            mSinchClient.getMessageClient().send(message);
            LogUtil.d(TAG, "sendMessage: " + textBody);
        }
    }

    public void addMessageClientListener(final MessageClientListener listener) {
        if (mSinchClient != null) {
//            mSinchClient.getMessageClient().addMessageClientListener(listener);
            mSinchClient.getMessageClient().addMessageClientListener(new MessageClientListener() {
                @Override
                public void onIncomingMessage(MessageClient messageClient, Message message) {
                    listener.onIncomingMessage(messageClient, message);

                    sendImcommingBroadcast(message.getSenderId());
//                    addMessageToDB(message);
                }

                @Override
                public void onMessageSent(MessageClient messageClient, Message message, String s) {
                    listener.onMessageSent(messageClient, message, s);
                }

                @Override
                public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
                    listener.onMessageFailed(messageClient, message, messageFailureInfo);
                }

                @Override
                public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
                    listener.onMessageDelivered(messageClient, messageDeliveryInfo);
                }

                @Override
                public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
                    listener.onShouldSendPushData(messageClient, message, list);
                }
            });
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().removeMessageClientListener(listener);
        }
    }

    private void start(String userName) {
        LogUtil.d(TAG, "call start: ");
        if (mSinchClient == null || !userName.equalsIgnoreCase(this.userName)) {
            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();

            mSinchClient.setSupportMessaging(true);
            mSinchClient.startListeningOnActiveConnection();
            mSinchClient.setSupportActiveConnectionInBackground(true);
            mSinchClient.checkManifest();
//            mSinchClient.getMessageClient().addMessageClientListener(new MessageClientListener() {
//                @Override
//                public void onIncomingMessage(MessageClient messageClient, Message message) {
//                    Log.d(TAG, "onIncomingMessage: " + message.getTextBody());
//                }
//
//                @Override
//                public void onMessageSent(MessageClient messageClient, Message message, String s) {
//                    Log.d(TAG, "onMessageSent: " + message.getTextBody());
//                }
//
//                @Override
//                public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
//
//                }
//
//                @Override
//                public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
//                    Log.d(TAG, "onMessageDelivered: " + messageDeliveryInfo.getMessageId());
//                }
//
//                @Override
//                public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
//
//                }
//            });
            String regid = CustomSharedPreferences.getPreferences(Constants.PREF_GCM_DEVICE_ID, "");
            if(regid.length() > 0) {
                mSinchClient.setSupportPushNotifications(true);
                mSinchClient.registerPushNotificationData(regid.getBytes(Charset.forName("UTF-8")));
            }

            mSinchClient.addSinchClientListener(new MySinchClientListener());
            mSinchClient.start();
            LogUtil.d(TAG, "call start: ");
        }
    }


    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }

    private void sendImcommingBroadcast(String senderID) {
        LogUtil.d(TAG, "sendBroadcast : " + senderID);
        Intent incommingIntent = new Intent(Constants.INCOMMING_BROADCAST_ACTION);
        incommingIntent.putExtra(Constants.INCOMMING_BROADCAST_FRIEND_NAME_EXTRA, senderID);
        sendBroadcast(incommingIntent);

    }

    private void addMessageToDB(Message message) {
        String loggedUserId = RunningRaceApplication.getInstance().getCurrentUser().getName();

        String msgId = message.getMessageId();
        List<String> list = message.getRecipientIds();
        String userIdDb = loggedUserId;
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
        String ownerId = message.getSenderId();
        LogUtil.d(TAG, "onIncomingMessage ID|SENder|friend(ME)|content|time|owner: "
                + msgId + "|" + userIdDb + "|" + friendIdDb + "|" + content + "|" + sentTime + "|" + ownerId);
        if (userIdDb != null) {
            //Message messageObject = new Message(currentFriend.getId(), message.getTextBody());
            com.runningracehisotry.models.Message messageObject = new com.runningracehisotry.models.Message(msgId, userIdDb, friendIdDb, content, sentTime, ownerId);
            //store DB when received
            HistoryConversation dao = HistoryConversation.getInstance(this, RunningRaceApplication.getInstance().getCurrentUser().getName());
            long result = dao.addMessage(messageObject);
            LogUtil.d(TAG, "onIncomingMessage add DB result|| message ID: " + result + "||" + message.getMessageId());
        }
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            LogUtil.d(TAG, "SinchClient fail:" + error.getMessage());
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            LogUtil.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
                mSinchClient.getMessageClient().addMessageClientListener(new MessageClientListener() {
                    @Override
                    public void onIncomingMessage(MessageClient messageClient, Message message) {

                        sendImcommingBroadcast(message.getSenderId());
                        //TODO add to db hear
                    }

                    @Override
                    public void onMessageSent(MessageClient messageClient, Message message, String s) {

                    }

                    @Override
                    public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {

                    }

                    @Override
                    public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

                    }

                    @Override
                    public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

                    }
                });
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            LogUtil.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                ClientRegistration clientRegistration) {
        }
    }
}
