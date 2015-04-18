package com.runningracehisotry.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by QuyNguyen on 4/19/2015.
 */
public class NewRaceDetailAdapter  extends BaseExpandableListAdapter {


    private int mFriendPos;
    private Context mContext;
    private int mShareBtn = 0;
    private int actionUpX = 0;
    private View mCurrentView;
    private int difference = 0;
    private int actionDownX = 0;
    private LayoutInflater mInflater;
    private SimpleDateFormat mDateFormat;
    private OnRaceItemDelete mRaceItemDelete;
    private OnRaceItemClickListener mRaceItemClick;
    private OnShareItemClickListener mShareItemListener;
    private int mRaceColor, mImagesId, mTimeImageId;
    private Map<String, List<HashMap<String, Object>>> mHistories;



    public NewRaceDetailAdapter(Context context,
                             Map<String, List<HashMap<String, Object>>> histories,
                             int friendPos, int... resources) {
        super();

        this.mContext = context;
        this.mFriendPos = friendPos;
        this.mHistories = histories;
        this.mShareBtn = resources[3];
        this.mImagesId = resources[1];
        this.mRaceColor = resources[0];
        this.mTimeImageId = resources[2];
        this.mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface OnShareItemClickListener {

        public void onShareItem(HashMap<String, Object> raceInfo);
    }

    public void setOnShareItemListener(
            OnShareItemClickListener shareItemListener) {

        this.mShareItemListener = shareItemListener;
    }

    public interface OnRaceItemClickListener {

        public void onRaceItemClick(HashMap<String, Object> raceInfo);
    }

    public void setRaceItemClick(OnRaceItemClickListener raceItemClick) {

        this.mRaceItemClick = raceItemClick;
    }

    public interface OnRaceItemDelete {

        public void onRaceItemDelete(HashMap<String, Object> raceInfo);
    }

    public void setOnRaceItemDelete(OnRaceItemDelete raceItemDelete) {

        this.mRaceItemDelete = raceItemDelete;
    }
    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
