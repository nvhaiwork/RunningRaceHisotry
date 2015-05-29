package com.runningracehisotry.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.runningracehisotry.R;
import com.runningracehisotry.models.Friend;
import com.runningracehisotry.models.Group;
import com.runningracehisotry.webservice.ServiceApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ngocm on 05/03/15.
 */
public class FriendChatAdapter extends BaseExpandableListAdapter {
    private Context context;
    private Map<Integer, List<Friend>> friendMap = new HashMap<Integer, List<Friend>>();
    private List<Group> groups;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public FriendChatAdapter(Context context, List<Group> groups, ImageLoader imageLoader) {
        this.context = context;
        this.groups = groups;

        this.mImageLoader = imageLoader;
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user_img)
                .showImageForEmptyUri(R.drawable.ic_user_img)
                .showImageOnFail(R.drawable.ic_user_img).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888).build();
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(friendMap.get(groups.get(groupPosition).getGroupId()) == null)
            return 0;
        else
            return friendMap.get(groups.get(groupPosition).getGroupId()).size();
    }

    @Override
    public Group getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Friend getChild(int groupPosition, int childPosition) {
        if(friendMap.get(groups.get(groupPosition).getGroupId()) == null || friendMap.get(groups.get(groupPosition).getGroupId()).size() < childPosition + 1)
            return null;
        else
            return friendMap.get(groups.get(groupPosition).getGroupId()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition *100 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = groups.get(groupPosition).getGroupName();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_group_item, null);
        }

        TextView header = (TextView) convertView
                .findViewById(R.id.tv_group_item);

        header.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Friend friend = getChild(groupPosition, childPosition);
        final String childText = friend.getFriend().getFull_name();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_user_item, null);
        }

        TextView tvName = (TextView) convertView
                .findViewById(R.id.user_item_text);

        ImageView ivAvatar = (ImageView) convertView
                .findViewById(com.runningracehisotry.R.id.user_item_img);
        ImageView ivNew = (ImageView) convertView
                .findViewById(com.runningracehisotry.R.id.user_item_new);

        View viewNotify = convertView.findViewById(R.id.view_notify);

        //TODO set visibility for red point
//        viewNotify.setVisibility();

        tvName.setText(childText);
        mImageLoader.displayImage(ServiceApi.SERVICE_URL + friend.getFriend().getProfile_image(), ivAvatar, mOptions);
        if(friend.getNewMessage() != 0){
            ivNew.setVisibility(View.VISIBLE);
        }
        else{
            ivNew.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addItem(List<Friend> friends) {
        friendMap.put(friends.get(0).getGroupId(), friends);

        notifyDataSetChanged();
    }

    public void setData(List<Group> groups, Map<Integer, List<Friend>> friendMap) {
        this.groups = groups;
        this.friendMap = friendMap;
        notifyDataSetChanged();
    }
}
