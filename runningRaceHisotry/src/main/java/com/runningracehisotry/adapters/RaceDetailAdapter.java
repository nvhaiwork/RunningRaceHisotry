/**
 * 
 */
package com.runningracehisotry.adapters;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.runningracehisotry.BaseActivity;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.RaceImagesDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;
import com.runningracehisotry.views.CustomAlertDialog.OnPositiveButtonClick;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author nvhaiwork
 *
 */
public class RaceDetailAdapter extends BaseExpandableListAdapter {

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
	private OnRaceItemClickListenner mRaceItemClick;
	private OnShareItemClickListenner mShareItemListenner;
	private int mRaceColor, mImagesId, mTimeImageId;
	private Map<String, List<HashMap<String, Object>>> mHistories;

	/**
	 * {@link Constructor}
	 * 
	 * @param resources
	 *            Color of text, images of images button, image of time text
	 * */
	public RaceDetailAdapter(Context context,
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

	public interface OnShareItemClickListenner {

		public void onShareItem(HashMap<String, Object> raceInfo);
	}

	public void setOnShareItemListenner(
			OnShareItemClickListenner shareItemListenner) {

		this.mShareItemListenner = shareItemListenner;
	}

	public interface OnRaceItemClickListenner {

		public void onRaceItemClick(HashMap<String, Object> raceInfo);
	}

	public void setRaceItemClick(OnRaceItemClickListenner raceItemClick) {

		this.mRaceItemClick = raceItemClick;
	}

	public interface OnRaceItemDelete {

		public void onRaceItemDelete(HashMap<String, Object> raceInfo);
	}

	public void setOnRaceItemDelete(OnRaceItemDelete raceItemDelete) {

		this.mRaceItemDelete = raceItemDelete;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public HashMap<String, Object> getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return getGroup(groupPosition).get(childPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final HashMap<String, Object> race = getChild(groupPosition,
				childPosition);
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
		long finishTime = (Integer) race.get(Constants.FINISHTIME) * 1000;
		String finishTimeStr = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(finishTime),
				TimeUnit.MILLISECONDS.toMinutes(finishTime)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(finishTime)
						% TimeUnit.MINUTES.toSeconds(1));
		int raceType = 0;
		try {

			raceType = (Integer) (race.get(Constants.EVENTTYPE));
		} catch (Exception ex) {

			raceType = Integer.valueOf((String) race.get(Constants.EVENTTYPE));
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

		Date raceDate = (Date) race.get(Constants.RACEDATE);
		holder.time.setText(finishTimeStr);
		holder.miles.setText(raceDistance + " MI");
		holder.name.setText(race.get(Constants.RACENAME).toString());
		holder.date.setText(mDateFormat.format(raceDate));
		holder.images.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (race.containsKey(Constants.BIB)
						|| race.containsKey(Constants.MEDAL)
						|| race.containsKey(Constants.PERSON)) {

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
				} else {

					Utilities.showAlertMessage(mContext, mContext
							.getString(R.string.dialog_race_no_image_message),
							mContext.getString(R.string.dialog_race_tile));
				}
			}
		});

		holder.shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				mShareItemListenner.onShareItem(race);
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

			holder.delete.setOnClickListener(new OnClickListener() {

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
							new OnNegativeButtonClick() {

								@Override
								public void onButtonClick(final View view) {
									// TODO Auto-generated method stub

									dialog.dismiss();
								}
							});
					dialog.setPositiveButton(mContext.getString(R.string.yes),
							new OnPositiveButtonClick() {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub

		String key = (String) (new ArrayList(mHistories.keySet()))
				.get(groupPosition);
		return mHistories.get(key).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<HashMap<String, Object>> getGroup(int groupPosition) {
		// TODO Auto-generated method stub

		String key = (String) (new ArrayList(mHistories.keySet()))
				.get(groupPosition);
		return mHistories.get(key);
	}

	/**
	 * Get group key by position
	 * 
	 * @param groupPosition
	 *            Position of group
	 * @return Key of group at position
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getGroupKey(int groupPosition) {

		return (String) (new ArrayList(mHistories.keySet())).get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mHistories.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

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
		holder.header.setOnTouchListener(new OnTouchListener() {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Set data
	 * */
	public void setData(Map<String, List<HashMap<String, Object>>> histories) {

		this.mHistories = histories;
	}

	private class HeaderViewHolder {

		TextView header;
	}

	private class ViewHolder {

		TextView date, miles, name, time;
		ImageView images, shareBtn;
		RelativeLayout itemLayout, delete;
	}

	private class LoadRaceImageAsync extends
			AsyncTask<HashMap<String, Object>, Void, Void> {

		private Dialog dialog;
		private List<Bitmap> images;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(mContext, "", "", false, false);
		}

		@Override
		protected Void doInBackground(HashMap<String, Object>... paramVarArgs) {
			// TODO Auto-generated method stub
			HashMap<String, Object> raceInfo = paramVarArgs[0];
			images = new ArrayList<Bitmap>();
			if (raceInfo.containsKey(Constants.MEDAL)) {

				try {

					ParseObject object = (ParseObject) raceInfo
							.get(Constants.MEDAL);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					images.add(bmp);
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			if (raceInfo.containsKey(Constants.BIB)) {

				try {

					ParseObject object = (ParseObject) raceInfo
							.get(Constants.BIB);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					images.add(bmp);
				} catch (ParseException e) {

					LogUtil.e("LoadRaceImageAsync", e.getMessage());
				}
			}

			if (raceInfo.containsKey(Constants.PERSON)) {

				try {

					ParseObject object = (ParseObject) raceInfo
							.get(Constants.PERSON);
					object.fetch();
					ParseFile image = object.getParseFile(Constants.DATA
							.toUpperCase());
					byte[] data = image.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					images.add(bmp);
				} catch (ParseException e) {

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

	class MyTouchListener implements OnTouchListener {

		private HashMap<String, Object> mRaceInfo;

		/**
		 * 
		 */
		public MyTouchListener(HashMap<String, Object> raceInfo) {
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
					calcuateDifference(v, mRaceInfo);
				}
				break;
			}

			return true;
		}
	}

	private void calcuateDifference(final View holder,
			final HashMap<String, Object> raceInfo) {

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

}
