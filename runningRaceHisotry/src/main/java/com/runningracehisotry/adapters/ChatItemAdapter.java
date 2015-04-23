package com.runningracehisotry.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.runningracehisotry.R;
import com.runningracehisotry.RunningRaceApplication;
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
        mMessages.add(message);
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
            converView.setTag(holder);
        } else {

            holder = (ViewHolder) converView.getTag();
        }

        if(message.getUserID().equals(RunningRaceApplication.getInstance().getCurrentUser().getId())) {
            holder.imageFriend.setVisibility(View.GONE);
            holder.imageME.setVisibility(View.VISIBLE);
            holder.text.setBackgroundColor(context.getResources().getColor(R.color.text_button_bg_5k));
            holder.text.setPadding(50, 0, 25, 0);
            mImageLoader.displayImage(ServiceApi.SERVICE_URL + RunningRaceApplication.getInstance().getCurrentUser().getProfile_image(), holder.imageME, mOptions);
        } else {
            holder.imageME.setVisibility(View.GONE);
            holder.imageFriend.setVisibility(View.VISIBLE);
            holder.text.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.text.setPadding(25, 0, 50, 0);
            mImageLoader.displayImage(ServiceApi.SERVICE_URL + currentFriend.getProfile_image(), holder.imageME, mOptions);
        }

        holder.text.setText(message.getContent());

        return converView;
    }

    private class ViewHolder {

        ImageView imageME;
        ImageView imageFriend;
        TextView text;
    }
}
