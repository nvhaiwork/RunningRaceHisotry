/**
 *
 */
package com.runningracehisotry.adapters;

import java.util.List;

import com.runningracehisotry.R;
import com.runningracehisotry.models.MenuModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Sniper
 *
 */
public class LeftMenuAdapter extends BaseAdapter {

	private List<MenuModel> mMenus;
	private LayoutInflater mInflater;

	public LeftMenuAdapter(Context context, List<MenuModel> menus) {
		super();
		this.mMenus = menus;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		if (mMenus != null) {

			return mMenus.size();
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return mMenus.get(pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		MenuModel menu = mMenus.get(pos);
		if (view == null) {

			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.layout_menu_item, arg2, false);
			holder.mImage = (ImageView) view.findViewById(R.id.menu_item_img);
			holder.mText = (TextView) view.findViewById(R.id.menu_item_text);
			view.setTag(holder);
		} else {

			holder = (ViewHolder) view.getTag();
		}

		if (menu.getImage() == null) {

			holder.mImage.setVisibility(View.GONE);
		} else {

			holder.mImage.setImageResource(menu.getImage());
			holder.mImage.setVisibility(View.VISIBLE);
		}

		holder.mText.setText(menu.getDislayText());
		return view;
	}

	private class ViewHolder {

		private ImageView mImage;
		private TextView mText;
	}
}
