package com.runningracehisotry.adapters;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runningracehisotry.BaseActivity;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Like;
import com.runningracehisotry.models.Race;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.RaceImagesDialog;
import com.runningracehisotry.webservice.ServiceApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by QuyNguyen on 5/16/2015.
 */
public class RaceDetailSortAdapter   extends BaseExpandableListAdapter {


    private int mFriendPos;
    private Context mContext;
    private int mShareBtn = 0, mLikeButton = 0;
    private int actionUpX = 0;
    private View mCurrentView;
    private int difference = 0;
    private int actionDownX = 0;
    private LayoutInflater mInflater;
    private SimpleDateFormat mDateFormat;
    private OnRaceItemDelete mRaceItemDelete;
    private OnRaceItemClickListener mRaceItemClick;
    private OnShareItemClickListener mShareItemListener;
    private OnLikeItemClickListener mLikeItemListener;
    private int mRaceColor, mImagesId, mTimeImageId;
    private LinkedHashMap<String, List<Race>> mHistories;
    private String loggedUserId;
    private int typeLike;


    public RaceDetailSortAdapter(int typeLike, String userId, Context context,
                                 LinkedHashMap<String, List<Race>> histories,
                                int friendPos, int... resources) {
        super();
        this.typeLike = typeLike;
        this.loggedUserId = userId;
        this.mContext = context;
        this.mFriendPos = friendPos;
        this.mHistories = histories;
        this.mLikeButton = resources[4];
        this.mShareBtn = resources[3];
        this.mImagesId = resources[1];
        this.mRaceColor = resources[0];
        this.mTimeImageId = resources[2];
        this.mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setLikeIcon(ImageView image, boolean like){
        switch (typeLike) {
            case Constants.SELECT_RACE_5K:
                if(like){
                    image.setImageResource(R.drawable.like_5k);
                }
                else{
                    image.setImageResource(R.drawable.unlike_5k);
                }
                break;
            case Constants.SELECT_RACE_10K:
                if(like){
                    image.setImageResource(R.drawable.like_10k);
                }
                else{
                    image.setImageResource(R.drawable.unlike_10k);
                }
                break;
            case Constants.SELECT_RACE_15K:
                if(like){
                    image.setImageResource(R.drawable.like_15k);
                }
                else{
                    image.setImageResource(R.drawable.unlike_15k);
                }
                break;
            case Constants.SELECT_RACE_HALF_MAR:
                if(like){
                    image.setImageResource(R.drawable.like_half);
                }
                else{
                    image.setImageResource(R.drawable.unlike_half);
                }
                break;
            case Constants.SELECT_RACE_FULL_MAR:
                if(like){
                    image.setImageResource(R.drawable.like_full);
                }
                else{
                    image.setImageResource(R.drawable.unlike_full);
                }
                break;
            case Constants.SELECT_RACE_OTHER:
                if(like){
                    image.setImageResource(R.drawable.like_other);
                }
                else{
                    image.setImageResource(R.drawable.unlike_other);
                }
                break;
        }
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
        /*ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);*/
        if(headerStr.length() > 7){
            holder.header.setText(Utilities.getDateTimeRaceTitle(headerStr.substring(0, 10)));
            LogUtil.e(Constants.LOG_TAG, "HEADER FOR TIME/LENGTH: " + headerStr);
            //convertView.setVisibility(View.GONE);
            holder.header.setVisibility(View.GONE);
        }
        else{
            holder.header.setText(Utilities.getDateTimeRaceTitle(headerStr));
            //convertView.setVisibility(View.VISIBLE);
            holder.header.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Race race = getChild(groupPosition, childPosition);
        LogUtil.e(Constants.LOG_TAG, "Race: " + race.getRaceDate() + "|"+ race.getFinisherTime());
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

            holder.likeButton = (ImageView) convertView
                    .findViewById(R.id.races_detail_race_like);
            holder.likeTotal = (TextView) convertView
                    .findViewById(R.id.races_detail_race_like_total);
            holder.average = (TextView) convertView
                    .findViewById(R.id.races_detail_race_average);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        // Set image, text color
        holder.average.setTextColor(mRaceColor);
        holder.likeTotal.setTextColor(mRaceColor);
        holder.time.setTextColor(mRaceColor);
        holder.miles.setTextColor(mRaceColor);
        holder.name.setTextColor(mRaceColor);
        holder.date.setTextColor(mRaceColor);
        holder.images.setImageResource(mImagesId);
        holder.shareBtn.setImageResource(mShareBtn);
        holder.likeButton.setImageResource(mLikeButton);
        holder.time.setCompoundDrawablesWithIntrinsicBounds(mTimeImageId, 0, 0,
                0);
        //Picasso.with(mContext).load("http://cachtrimun.org/wp-content/uploads/Tac-dung-tri-mun-dieu-ky-tu-tao-bien.jpg").into(holder.images);
        holder.average.setText("Average Pace: " + race.getAveragePace());
        holder.likeTotal.setText("0 people like this");
        if((race.getLikes() != null) && (race.getLikes().size() > 0)) {
            holder.likeTotal.setText(race.getLikes().size() + " people like this");
            if(isLiked(race)){
                //holder.likeButton.setImageResource(R.drawable.like);
                setLikeIcon(holder.likeButton, true);
            }
            else{
                //holder.likeButton.setImageResource(R.drawable.unlike);
                setLikeIcon(holder.likeButton, true);
            }
        }

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
            LogUtil.d(Constants.LOG_TAG, " race type: " + raceType);
        } catch (Exception ex) {
            LogUtil.d(Constants.LOG_TAG, "rerror whenget race type");
            ex.printStackTrace();
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
            case Constants.SELECT_RACE_OTHER:
                raceDistance = race.getRaceMiles();
                break;
        }

        //Date raceDate = (Date) race.get(Constants.RACEDATE);
        holder.time.setText(finishTimeStr);
        holder.miles.setText(raceDistance + " MI");
        //holder.name.setText(race.get(Constants.RACENAME).toString());
        holder.name.setText(race.getName());
        //holder.date.setText(mDateFormat.format(raceDate));
        //holder.date.setText(race.getRaceDate().substring(0,10));
        holder.date.setText(Utilities.getDateTimeEachRace(race.getRaceDate().substring(0,10)));
        holder.images.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {

                new LoadRaceImageAsync().execute(race);
                if (mCurrentView != null) {

                    ObjectAnimator animator;
                    animator = ObjectAnimator.ofFloat(mCurrentView,
                            "translationX", 0);
                    animator.setDuration(200);
                    animator.start();
                    mCurrentView = null;
                    actionDownX = -1;
                }
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
                /*if (    ((race.getBibUrl() != null) && (!race.getBibUrl().isEmpty()))
                        || ((race.getMedalUrl() != null) && (!race.getMedalUrl().isEmpty()))
                        ||((race.getPersonUrl() != null) && (!race.getPersonUrl().isEmpty()))){

                    *//*Utilities.showAlertMessage(mContext, "Temporary not show image",
                            mContext.getString(R.string.dialog_race_tile));*//*
                    new LoadRaceImageAsync().execute(race);
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
                else {

                    Utilities.showAlertMessage(mContext, mContext
                                    .getString(R.string.dialog_race_no_image_message),
                            mContext.getString(R.string.dialog_race_tile));
                }*/

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

        holder.likeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                mLikeItemListener.onLikeItem(race);
                /*if(!holder.likeButton.isSelected()){
                    holder.likeButton.setSelected(true);
                }*/
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

    public void setData(LinkedHashMap<String, List<Race>> mRacesDetail) {
        /*this.mHistories.entrySet().clear();
        this.mHistories.keySet().clear();
        this.mHistories.clear();*/
        //this.mHistories = null;
        this.mHistories = mRacesDetail;//new LinkedHashMap<String, List<Race>>(mRacesDetail);
        //notifyDataSetInvalidated();
        //this.notifyDataSetChanged();
    }

    public LinkedHashMap<String, List<Race>> getData() {
        return this.mHistories;
    }

    /*@Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }*/
    /*
    @Override
    public void unregisterDataSetObserver (DataSetObserver observer){
        super.unregisterDataSetObserver(observer);
    }*/

    private class HeaderViewHolder {

        TextView header;
    }

    private class ViewHolder {

        TextView date, miles, name, time;
        ImageView images, shareBtn;
        RelativeLayout itemLayout, delete;
        ImageView likeButton;
        TextView average, likeTotal;
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

    public void setOnLikeItemListener(OnLikeItemClickListener likeItemClick) {
        this.mLikeItemListener =  likeItemClick;
    }

    public boolean isLiked(Race race){
        boolean result = false;
        List<Like> listLike = race.getLikes();
        if(listLike != null && listLike.size() >0){
            for(Like like : listLike){
                if(like.getUserID().equals( loggedUserId)){
                    return true;
                }
            }

        }
        return result;
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Race> getGroup(int groupPosition) {
        String key = (String) (new ArrayList(mHistories.keySet()))
                .get(groupPosition);
        LogUtil.e(Constants.LOG_TAG, "get list race group: " +key);
        return mHistories.get(key);
    }

    @Override
    public Race getChild(int groupPosition, int childPosition) {
        LogUtil.e(Constants.LOG_TAG, "get  race detail: " +getGroup(groupPosition).get(childPosition));
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

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private class LoadRaceImageAsync extends
            AsyncTask<Race, Void, Void> {

        private Dialog dialog;
        private List<Bitmap> images;


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = CustomLoadingDialog.show(mContext, "", "", false, false);
        }

        @Override
        protected Void doInBackground(Race... paramVarArgs) {
            // TODO Auto-generated method stub
            Race raceInfo = paramVarArgs[0];
            images = new ArrayList<Bitmap>();
            if ((raceInfo.getMedalUrl() != null) &&(!raceInfo.getMedalUrl().isEmpty())) {

                try {
                    Bitmap bmp = getBitmapFromURL(ServiceApi.SERVICE_URL + raceInfo.getMedalUrl());
                    images.add(bmp);
                } catch (Exception e) {

                    LogUtil.e("LoadRaceImageAsync", e.getMessage());
                }
            }

            if ((raceInfo.getBibUrl() != null) &&(!raceInfo.getBibUrl().isEmpty())) {

                try {
                    Bitmap bmp = getBitmapFromURL(ServiceApi.SERVICE_URL + raceInfo.getBibUrl());
                    images.add(bmp);
                } catch (Exception e) {

                    LogUtil.e("LoadRaceImageAsync", e.getMessage());
                }
            }
            if ((raceInfo.getPersonUrl() != null) &&(!raceInfo.getPersonUrl().isEmpty())) {

                try {
                    Bitmap bmp = getBitmapFromURL(ServiceApi.SERVICE_URL + raceInfo.getPersonUrl());
                    images.add(bmp);
                } catch (Exception e) {

                    LogUtil.e("LoadRaceImageAsync", e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            DialogFragment dialogFragment = RaceImagesDialog
                    .getInstance(images);
            dialogFragment.show(
                    ((BaseActivity) mContext).getSupportFragmentManager(), "");
            dialog.dismiss();
        }
    }
}
