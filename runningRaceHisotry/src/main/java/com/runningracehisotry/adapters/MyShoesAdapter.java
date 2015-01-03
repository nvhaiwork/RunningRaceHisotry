/**
 * 
 */
package com.runningracehisotry.adapters;

import java.util.List;

import com.parse.ParseObject;
import com.runningracehisotry.BaseActivity;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;
import com.runningracehisotry.views.CustomAlertDialog.OnPositiveButtonClick;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author nvhaiwork
 *
 */
public class MyShoesAdapter extends BaseAdapter {

	private Context mContext;
	private int actionUpX = 0;
	private View mCurrentView;
	private int difference = 0;
	private int actionDownX = 0;
	private ImageView mCheckView;
	private boolean mIsSelectShoe;
	private List<ParseObject> mShoes;
	private LayoutInflater mInflater;
	private OnShoeItemDelete mShoeItemDelete;
	private OnShoeItemClickListenner mShoeItemClick;

	public MyShoesAdapter(Context context, List<ParseObject> shoes,
			boolean isSelectShoe) {
		super();

		this.mShoes = shoes;
		this.mContext = context;
		this.mIsSelectShoe = isSelectShoe;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public interface OnShoeItemClickListenner {

		public void onShoeItemClick(int position);
	}

	public void setShoeItemClick(OnShoeItemClickListenner shoeItemClick) {

		this.mShoeItemClick = shoeItemClick;
	}

	public interface OnShoeItemDelete {

		public void onShoeItemDelete(ParseObject shoe);
	}

	public void setShoeItemDelete(OnShoeItemDelete shoeItemDelete) {

		this.mShoeItemDelete = shoeItemDelete;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mShoes != null) {

			return mShoes.size();
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public ParseObject getItem(int position) {
		// TODO Auto-generated method stub
		return mShoes.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final ParseObject shoe = getItem(position);
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.layout_shoes_item, parent,
					false);
			holder.image = (ImageView) convertView
					.findViewById(R.id.my_shoes_item_img);
			holder.name = (TextView) convertView
					.findViewById(R.id.my_shoes_item_name);
			holder.type = (TextView) convertView
					.findViewById(R.id.my_shoes_item_type);
			holder.detail = (TextView) convertView
					.findViewById(R.id.my_shoes_item_detail);
			holder.delete = (RelativeLayout) convertView
					.findViewById(R.id.shoe_item_delete);
			holder.itemLayout = (RelativeLayout) convertView
					.findViewById(R.id.shoe_item_info_layout);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.image.setImageResource(R.drawable.ic_shoe);
		holder.name.setText(shoe.getString(Constants.BRAND));
		holder.type.setText(shoe.getString(Constants.MODEL));
		float distanceF = (float) shoe.getDouble(Constants.DISTANCE);
		holder.detail.setText(String.format("%.2f miles (%.2f km)", distanceF,
				distanceF / 0.62137119));
		if (shoe.containsKey(Constants.IMAGE)) {
			ParseObject imageObject = shoe.getParseObject(Constants.IMAGE);
			if (imageObject != null) {

				Utilities.displayParseImage(imageObject, holder.image, 0);
			}
		}

		if (mIsSelectShoe) {

			holder.itemLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub

					if (mCheckView != null) {

						mCheckView.setVisibility(View.INVISIBLE);
					}

					mCheckView = (ImageView) paramView
							.findViewById(R.id.my_shoes_check_view);
					mCheckView.setVisibility(View.VISIBLE);
					mCheckView.setTag(position);
				}
			});
		} else {

			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final CustomAlertDialog dialog = new CustomAlertDialog(
							mContext);
					dialog.setCancelableFlag(false);
					dialog.setTitle(mContext
							.getString(R.string.dialog_shoe_tile));
					dialog.setMessage(mContext
							.getString(R.string.dialog_confirm_delete_shoe));
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

									mShoeItemDelete.onShoeItemDelete(shoe);
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

			holder.itemLayout.setOnTouchListener(new MyTouchListener(shoe,
					position));
		}

		return convertView;
	}

	/**
	 * Return selected position
	 * */
	public int getSelectedPosition() {

		if (mCheckView != null) {

			int selected = (Integer) mCheckView.getTag();
			return selected;
		}

		return -1;
	}

	private class ViewHolder {

		ImageView image;
		TextView name, type, detail;
		RelativeLayout itemLayout, delete;
	}

	class MyTouchListener implements OnTouchListener {

		private int mPosition;
		private ParseObject mShoe;

		/**
		 * 
		 */
		public MyTouchListener(ParseObject shoe, int position) {
			// TODO Auto-generated constructor stub
			this.mShoe = shoe;
			this.mPosition = position;
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
					calcuateDifference(v, mShoe, mPosition);
				}
				break;
			}

			return true;
		}
	}

	private void calcuateDifference(final View holder, final ParseObject shoe,
			final int position) {

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

						mShoeItemClick.onShoeItemClick(position);
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
