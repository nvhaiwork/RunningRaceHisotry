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
import com.runningracehisotry.models.Friend;
import com.runningracehisotry.models.Runner;

import java.util.List;

/**
 * Created by quynt3 on 2015/04/18.
 */
public class FriendAdapter  extends BaseAdapter {

    private List<Friend> mFriends;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public FriendAdapter(Context context, List<Friend> runners,
                            ImageLoader imageLoader) {
        super();
        this.mFriends = runners;
        this.mImageLoader = imageLoader;
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
        if (mFriends != null) {

            return mFriends.size();
        }

        return 0;
    }

    @Override
    public Friend getItem(int position) {
        // TODO Auto-generated method stub
        return mFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parrent) {
        // TODO Auto-generated method stub

        Friend friendGroup = (Friend) getItem(position);
        ViewHolder holder = null;
        if (converView == null) {

            converView = mInflater.inflate(
                    com.runningracehisotry.R.layout.layout_user_item, parrent,
                    false);
            holder = new ViewHolder();
            holder.image = (ImageView) converView
                    .findViewById(com.runningracehisotry.R.id.user_item_img);
            holder.text = (TextView) converView
                    .findViewById(com.runningracehisotry.R.id.user_item_text);
            converView.setTag(holder);
        } else {

            holder = (ViewHolder) converView.getTag();
        }

        holder.text.setText(friendGroup.getFriend().getFull_name());

        // Image
        /*if (user.containsKey(Constants.PICTURE)) {

            ParseObject image = user.getParseObject(Constants.PICTURE);
            Utilities.displayParseImage(image, holder.image, 0);
        } else {

            String imgUrl = Utilities.getUserProfileImage(user);
            mImageLoader.displayImage(imgUrl, holder.image, mOptions);
        }*/

        return converView;
    }

    private class ViewHolder {

        ImageView image;
        TextView text;
    }
}
