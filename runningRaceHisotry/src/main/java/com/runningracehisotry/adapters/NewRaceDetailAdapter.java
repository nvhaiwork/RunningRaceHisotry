package com.runningracehisotry.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runningracehisotry.BaseActivity;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Race;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private Map<String, List<Race>> mHistories;



    public NewRaceDetailAdapter(Context context,
                             Map<String, List<Race>> histories,
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


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HeaderViewHolder holder = null;
        String headerStr = getGroupKey(groupPosition);
        if (convertView == null) {

            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(
                    R.layout.layout_race_detail_item_header, parent, false);
            holder.header = (TextView) convertView
                    .findViewById(R.id.races_detail_header);
            convertView.setTag(holder);
        } else {

            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.header.setText(headerStr);
        holder.header.setBackgroundColor(mRaceColor);
        holder.header.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (mCurrentView != null) {

                    ObjectAnimator animator;
                    animator = ObjectAnimator.ofFloat(mCurrentView,
                            "translationX", 0);
                    animator.setDuration(200);
                    animator.start();
                    mCurrentView = null;
                    actionDownX = -1;
                }

                return false;
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Race race = getChild(groupPosition, childPosition);
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_race_detail_item,
                    parent, false);
            holder.date = (TextView) convertView
                    .findViewById(R.id.races_detail_race_date);
            holder.name = (TextView) convertView
                    .findViewById(R.id.races_detail_race_name);
            holder.miles = (TextView) convertView
                    .findViewById(R.id.races_detail_race_mile);
            holder.time = (TextView) convertView
                    .findViewById(R.id.races_detail_race_time);
            holder.images = (ImageView) convertView
                    .findViewById(R.id.races_detail_race_imgs);
            holder.delete = (RelativeLayout) convertView
                    .findViewById(R.id.races_detail_delete);
            holder.shareBtn = (ImageView) convertView
                    .findViewById(R.id.races_detail_share);
            holder.itemLayout = (RelativeLayout) convertView
                    .findViewById(R.id.race_detail_layout);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        // Set image, text color
        holder.time.setTextColor(mRaceColor);
        holder.miles.setTextColor(mRaceColor);
        holder.name.setTextColor(mRaceColor);
        holder.date.setTextColor(mRaceColor);
        holder.images.setImageResource(mImagesId);
        holder.shareBtn.setImageResource(mShareBtn);
        holder.time.setCompoundDrawablesWithIntrinsicBounds(mTimeImageId, 0, 0,
                0);

        // Set data
        //long finishTime = (Integer) race.get(Constants.FINISHTIME) * 1000;
        /*long finishTime = (Integer) race.get(Constants.FINISHTIME) * 1000;
        String finishTimeStr = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(finishTime),
                TimeUnit.MILLISECONDS.toMinutes(finishTime)
                        % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(finishTime)
                        % TimeUnit.MINUTES.toSeconds(1));*/
        String finishTimeStr = race.getFinisherTime();
        int raceType = 0;
        try {

            //raceType = (Integer) (race.get(Constants.EVENTTYPE));
            raceType = race.getEvenType();
        } catch (Exception ex) {

            //raceType = Integer.valueOf((String) race.get(Constants.EVENTTYPE));
        }

        String raceDistance = "0.0";
        switch (raceType) {
            case Constants.SELECT_RACE_5K:

                raceDistance = Constants.RACE_5K_DISTANCE;
                break;
            case Constants.SELECT_RACE_10K:

                raceDistance = Constants.RACE_10K_DISTANCE;
                break;
            case Constants.SELECT_RACE_15K:

                raceDistance = Constants.RACE_15K_DISTANCE;
                break;
            case Constants.SELECT_RACE_HALF_MAR:

                raceDistance = Constants.RACE_HALF_MAR_DISTANCE;
                break;
            case Constants.SELECT_RACE_FULL_MAR:

                raceDistance = Constants.RACE_FULL_MAR_DISTANCE;
                break;
        }

        //Date raceDate = (Date) race.get(Constants.RACEDATE);
        holder.time.setText(finishTimeStr);
        holder.miles.setText(raceDistance + " MI");
        //holder.name.setText(race.get(Constants.RACENAME).toString());
        holder.name.setText(race.getName());
        //holder.date.setText(mDateFormat.format(raceDate));
        holder.date.setText(race.getRaceDate().substring(0,10));
        holder.images.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* if (race.containsKey(Constants.BIB)
                        || race.containsKey(Constants.MEDAL)
                        || race.containsKey(Constants.PERSON)) {

                    //new LoadRaceImageAsync().execute(race);
                    if (mCurrentView != null) {

                        ObjectAnimator animator;
                        animator = ObjectAnimator.ofFloat(mCurrentView,
                                "translationX", 0);
                        animator.setDuration(200);
                        animator.start();
                        mCurrentView = null;
                        actionDownX = -1;
                    }
                }*/
                if ((race.getBibUrl() != null) || (race.getMedalUrl() != null) ||(race.getPersonUrl() != null)){

                    Utilities.showAlertMessage(mContext, "Temporary not show image",
                            mContext.getString(R.string.dialog_race_tile));
                }
                else {

                    Utilities.showAlertMessage(mContext, mContext
                                    .getString(R.string.dialog_race_no_image_message),
                            mContext.getString(R.string.dialog_race_tile));
                }
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                mShareItemListener.onShareItem(race);
                if (mCurrentView != null) {

                    ObjectAnimator animator;
                    animator = ObjectAnimator.ofFloat(mCurrentView,
                            "translationX", 0);
                    animator.setDuration(200);
                    animator.start();
                    mCurrentView = null;
                    actionDownX = -1;
                }
            }
        });

        holder.shareBtn.setVisibility(View.INVISIBLE);
        if (mFriendPos == -1) {

            holder.shareBtn.setVisibility(View.VISIBLE);

            holder.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    final CustomAlertDialog dialog = new CustomAlertDialog(
                            mContext);
                    dialog.setCancelableFlag(false);
                    dialog.setTitle(mContext
                            .getString(R.string.dialog_race_tile));
                    dialog.setMessage(mContext
                            .getString(R.string.dialog_confirm_delete_race));
                    dialog.setNegativeButton(mContext.getString(R.string.no),
                            new CustomAlertDialog.OnNegativeButtonClick() {

                                @Override
                                public void onButtonClick(final View view) {
                                    // TODO Auto-generated method stub

                                    dialog.dismiss();
                                }
                            });
                    dialog.setPositiveButton(mContext.getString(R.string.yes),
                            new CustomAlertDialog.OnPositiveButtonClick() {

                                @Override
                                public void onButtonClick(View view) {
                                    // TODO Auto-generated method stub

                                    mRaceItemDelete.onRaceItemDelete(race);
                                    if (mCurrentView != null) {

                                        ObjectAnimator animator;
                                        animator = ObjectAnimator
                                                .ofFloat(mCurrentView,
                                                        "translationX", 0);
                                        animator.setDuration(200);
                                        animator.start();
                                        mCurrentView = null;
                                        actionDownX = -1;
                                    }

                                    dialog.dismiss();
                                }
                            });

                    dialog.show();
                }
            });

            MyTouchListener itemTouchListenner = new MyTouchListener(race);
            holder.itemLayout.setOnTouchListener(itemTouchListenner);
        }

        return convertView;
    }

    public void setData(Map<String, List<Race>> mRacesDetail) {
        this.mHistories = mRacesDetail;
    }

    private class HeaderViewHolder {

        TextView header;
    }

    private class ViewHolder {

        TextView date, miles, name, time;
        ImageView images, shareBtn;
        RelativeLayout itemLayout, delete;
    }
    class MyTouchListener implements View.OnTouchListener {

        private Race mRaceInfo;

        /**
         *
         */
        public MyTouchListener(Race raceInfo) {
            // TODO Auto-generated constructor stub
            this.mRaceInfo = raceInfo;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:

                    if (mCurrentView != null) {

                        ObjectAnimator animator;
                        animator = ObjectAnimator.ofFloat(mCurrentView,
                                "translationX", 0);
                        animator.setDuration(200);
                        animator.start();
                        mCurrentView = null;
                        actionDownX = -1;
                    } else {

                        actionDownX = (int) event.getX();
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    if (actionDownX != -1) {

                        actionUpX = (int) event.getX();
                        difference = actionDownX - actionUpX;
                        calculateDifference(v, mRaceInfo);
                    }
                    break;
            }

            return true;
        }
    }
    private void calculateDifference(final View holder,
                                    final Race raceInfo) {

        ((BaseActivity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ObjectAnimator animator;

                if (mCurrentView != null) {

                    animator = ObjectAnimator.ofFloat(mCurrentView,
                            "translationX", 0);
                    animator.setDuration(200);
                    animator.start();
                    mCurrentView = null;
                } else {
                    float px = mContext.getResources().getDimension(
                            R.dimen.race_item_delete);
                    if (difference == 0) {

                        mRaceItemClick.onRaceItemClick(raceInfo);
                    } else if (difference > 75) {

                        animator = ObjectAnimator.ofFloat(holder,
                                "translationX", 0 - px);
                        animator.setDuration(200);
                        animator.start();
                        mCurrentView = (RelativeLayout) holder;
                    } else if (difference < -75) {

                        animator = ObjectAnimator.ofFloat(holder,
                                "translationX", 0);
                        animator.setDuration(200);
                        animator.start();
                    }
                }

                actionDownX = 0;
                actionUpX = 0;
                difference = 0;
            }
        });
    }


    public interface OnShareItemClickListener {

        public void onShareItem(Race raceInfo);
    }

    public void setOnShareItemListener(
            OnShareItemClickListener shareItemListener) {

        this.mShareItemListener = shareItemListener;
    }

    public interface OnRaceItemClickListener {

        public void onRaceItemClick(Race raceInfo);
    }

    public void setRaceItemClick(OnRaceItemClickListener raceItemClick) {

        this.mRaceItemClick = raceItemClick;
    }

    public interface OnRaceItemDelete {

        public void onRaceItemDelete(Race raceInfo);
    }

    public void setOnRaceItemDelete(OnRaceItemDelete raceItemDelete) {

        this.mRaceItemDelete = raceItemDelete;
    }
    @Override
    public int getGroupCount() {
        return mHistories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String key = (String) (new ArrayList(mHistories.keySet()))
                .get(groupPosition);
        return mHistories.get(key).size();
    }

    @Override
    public List<Race> getGroup(int groupPosition) {
        String key = (String) (new ArrayList(mHistories.keySet()))
                .get(groupPosition);
        return mHistories.get(key);
    }

    @Override
    public Race getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).get(childPosition);
    }

    public String getGroupKey(int groupPosition) {

        return (String) (new ArrayList(mHistories.keySet())).get(groupPosition);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
