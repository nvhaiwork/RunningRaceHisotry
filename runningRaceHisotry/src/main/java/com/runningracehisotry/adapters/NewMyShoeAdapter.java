package com.runningracehisotry.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.runningracehisotry.BaseActivity;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.views.CustomAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * create by NTQ
 */
public class NewMyShoeAdapter extends BaseAdapter {

    private Context mContext;
    private List<Shoe> lstShoes;
    private LayoutInflater mInflater;


    private int actionUpX = 0;
    private View mCurrentView;
    private int difference = 0;
    private int actionDownX = 0;
    private ImageView mCheckView;
    private boolean mIsSelectShoe;


    private OnShoeItemDelete mShoeItemDelete;
    private OnShoeItemClickListener mShoeItemClick;

    public interface OnShoeItemClickListener {

        public void onShoeItemClick(int position);
    }

    public void setShoeItemClick(OnShoeItemClickListener shoeItemClick) {

        this.mShoeItemClick = shoeItemClick;
    }

    public interface OnShoeItemDelete {

        public void onShoeItemDelete(Shoe shoe);
    }

    public void setShoeItemDelete(OnShoeItemDelete shoeItemDelete) {

        this.mShoeItemDelete = shoeItemDelete;
    }


    //constructor function
    public NewMyShoeAdapter(Context context, List<Shoe> list, boolean isSelectShoe) {
        this.mContext = context;
        this.lstShoes = new ArrayList<Shoe>(list);
        this.mIsSelectShoe = isSelectShoe;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //get total object
    @Override
    public int getCount() {
        if (lstShoes != null) {
            return this.lstShoes.size();
        } else {
            return 0;
        }
    }

    //get item base-on position
    @Override
    public Shoe getItem(int position) {
        return lstShoes.get(position);
    }

    // get Item Id
    @Override
    public long getItemId(int position) {
        return lstShoes.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Shoe shoe = getItem(position);
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_shoes_item, parent, false);
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
        holder.name.setText(shoe.getModel());
        holder.type.setText(shoe.getBrand());
        float distanceF = shoe.getMilesOnShoes();
        holder.detail.setText(String.format("%.2f miles (%.2f km)", distanceF, distanceF / 0.62137119));

        if (mIsSelectShoe) {

            holder.itemLayout.setOnClickListener(new View.OnClickListener() {

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

            holder.delete.setOnClickListener(new View.OnClickListener() {

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
     */
    public int getSelectedPosition() {

        if (mCheckView != null) {

            int selected = (Integer) mCheckView.getTag();
            return selected;
        }

        return -1;
    }

    class MyTouchListener implements View.OnTouchListener {

        private int mPosition;
        private Shoe mShoe;

        /**
         *
         */
        public MyTouchListener(Shoe shoe, int position) {
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
                        calculateDifference(v, mShoe, mPosition);
                    }
                    break;
            }

            return true;
        }
    }

    private void calculateDifference(final View holder, final Shoe shoe,
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

    static class ViewHolder {
        ImageView image;
        TextView name, type, detail;
        RelativeLayout itemLayout, delete;

    }

}