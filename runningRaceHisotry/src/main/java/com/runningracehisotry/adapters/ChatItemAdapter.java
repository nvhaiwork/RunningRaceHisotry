package com.runningracehisotry.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.runningracehisotry.R;
import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Message;
import com.runningracehisotry.models.User;
import com.runningracehisotry.webservice.ServiceApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynt3 on 2015/04/18.
 */
public class ChatItemAdapter extends BaseAdapter {

    private List<Message> mMessages;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private User currentFriend;
    private Context context;

    public ChatItemAdapter(Context context, ImageLoader imageLoader, User currentFriend) {
        super();
        this.context = context;
        this.mMessages = new ArrayList<Message>();
        this.mImageLoader = imageLoader;
        this.currentFriend = currentFriend;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user_img)
                .showImageForEmptyUri(R.drawable.ic_user_img)
                .showImageOnFail(R.drawable.ic_user_img).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (mMessages != null) {

            return mMessages.size();
        }

        return 0;
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addMessage(Message message) {
        if(mMessages != null) {
            mMessages.add(mMessages.size(), message);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View converView, ViewGroup parrent) {

        Message message = (Message) getItem(position);
        ViewHolder holder = null;
        if (converView == null) {

            converView = mInflater.inflate(
                    R.layout.layout_chat_item, parrent,
                    false);
            holder = new ViewHolder();
            holder.imageME = (ImageView) converView
                    .findViewById(R.id.iv_avatar_me);
            holder.imageFriend = (ImageView) converView
                    .findViewById(R.id.iv_avatar_friend);
            holder.text = (TextView) converView
                    .findViewById(R.id.tv_message);

            holder.lnMe = (LinearLayout) converView
                    .findViewById(R.id.user_item_img_layout_me);
            holder.lnFriend = (LinearLayout) converView
                    .findViewById(R.id.user_item_img_layout_friend);
            converView.setTag(holder);
        } else {

            holder = (ViewHolder) converView.getTag();
        }
        //Log.d(Constants.LOG_TAG, "IMAGE LOGIN: " + RunningRaceApplication.getInstance().getCurrentUser().getId());
        //Log.d(Constants.LOG_TAG, "IMAGE cxhcek: " + message.getUserId().equalsIgnoreCase(RunningRaceApplication.getInstance().getCurrentUser().getId()));
        //Log.d(Constants.LOG_TAG, "IMAGE cxhcek: " + (message.getOwnerId().equalsIgnoreCase(message.getUserId())));
        if(message.getOwnerId().equalsIgnoreCase(message.getUserId())) {
            //login user message
            holder.lnFriend.setVisibility(View.GONE);
            holder.imageFriend.setVisibility(View.GONE);
            holder.lnMe.setVisibility(View.VISIBLE);
            holder.imageME.setVisibility(View.VISIBLE);
            holder.text.setBackgroundColor(context.getResources().getColor(R.color.text_button_bg_5k));
            holder.text.setPadding(20, 20, 20, 20);
            mImageLoader.displayImage(ServiceApi.SERVICE_URL + RunningRaceApplication.getInstance().getCurrentUser().getProfile_image(), holder.imageME, mOptions);
            //Log.d(Constants.LOG_TAG, "IMAGE MYSELF: " + ServiceApi.SERVICE_URL + RunningRaceApplication.getInstance().getCurrentUser().getProfile_image());
        } else {
            holder.lnMe.setVisibility(View.GONE);
            holder.imageME.setVisibility(View.GONE);
            holder.lnFriend.setVisibility(View.VISIBLE);
            holder.imageFriend.setVisibility(View.VISIBLE);
            holder.text.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.text.setPadding(20, 20, 20, 20);
            mImageLoader.displayImage(ServiceApi.SERVICE_URL + currentFriend.getProfile_image(), holder.imageFriend, mOptions);
            //Log.d(Constants.LOG_TAG, "IMAGE FRIEND CHAT: " + ServiceApi.SERVICE_URL + currentFriend.getProfile_image());
        }

        holder.text.setText(message.getContent());

        return converView;
    }

    public List<Message> getMessages() {
        return mMessages;
    }

    public void setMessages(List<Message> mMessages) {
        this.mMessages = mMessages;
    }

    private class ViewHolder {
        LinearLayout lnMe, lnFriend;
        ImageView imageME;
        ImageView imageFriend;
        TextView text;
    }
}
