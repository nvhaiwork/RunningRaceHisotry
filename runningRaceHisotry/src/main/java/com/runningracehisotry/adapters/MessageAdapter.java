package com.runningracehisotry.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.runningracehisotry.R;
import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.models.Message;
import com.runningracehisotry.models.User;
import com.runningracehisotry.webservice.ServiceApi;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    private List<Pair<WritableMessage, Integer>> messages;
    private LayoutInflater layoutInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private User currentFriend;
    private Context context;

    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<WritableMessage, Integer>>();

        this.context = context;
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {

            mImageLoader.init(ImageLoaderConfiguration
                    .createDefault(activity));
        }

        this.currentFriend = currentFriend;
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user_img)
                .showImageForEmptyUri(R.drawable.ic_user_img)
                .showImageOnFail(R.drawable.ic_user_img).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();

    }

    public void addMessage(WritableMessage message, int direction) {
        messages.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {

            convertView = layoutInflater.inflate(
                    R.layout.layout_chat_item, viewGroup,
                    false);
            holder = new ViewHolder();
            holder.imageME = (ImageView) convertView
                    .findViewById(R.id.iv_avatar_me);
            holder.imageFriend = (ImageView) convertView
                    .findViewById(R.id.iv_avatar_friend);
            holder.text = (TextView) convertView
                    .findViewById(R.id.tv_message);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        int direction = getItemViewType(i);

        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            if (direction == DIRECTION_INCOMING) {
                holder.imageME.setVisibility(View.GONE);
                holder.imageFriend.setVisibility(View.VISIBLE);
                holder.text.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.text.setPadding(25, 0, 50, 0);
                mImageLoader.displayImage(ServiceApi.SERVICE_URL + currentFriend.getProfile_image(), holder.imageME, mOptions);
            } else if (direction == DIRECTION_OUTGOING) {
                holder.imageFriend.setVisibility(View.GONE);
                holder.imageME.setVisibility(View.VISIBLE);
                holder.text.setBackgroundColor(context.getResources().getColor(R.color.text_button_bg_5k));
                holder.text.setPadding(50, 0, 25, 0);
                mImageLoader.displayImage(ServiceApi.SERVICE_URL + RunningRaceApplication.getInstance().getCurrentUser().getProfile_image(), holder.imageME, mOptions);
            }
        }

        WritableMessage message = messages.get(i).first;

        TextView txtMessage = (TextView) convertView.findViewById(R.id.tv_message);
        txtMessage.setText(message.getTextBody());

        return convertView;
    }

    private class ViewHolder {

        ImageView imageME;
        ImageView imageFriend;
        TextView text;
    }
}

