package com.runningracehisotry.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShoeDistanceHistoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private SimpleDateFormat mDateFormat;
	private List<HashMap<String, Object>> mShoeHistories;

	public ShoeDistanceHistoryAdapter(Context context,
			List<HashMap<String, Object>> shoeHistories) {
		super();

		this.mShoeHistories = shoeHistories;
		this.mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		if (mShoeHistories != null) {

			return mShoeHistories.size();
		}

		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mShoeHistories.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parrent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		HashMap<String, Object> history = (HashMap<String, Object>) getItem(position);
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.layout_shoe_distance_history_item, parrent, false);
			holder.historyText = (TextView) convertView
					.findViewById(R.id.shoe_distance_history_item_text);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		Date histDate = (Date) history.get(Constants.DATE);
		float miles = (Integer) history.get(Constants.ADDED);
		String itemText = String.format("%s: you added %.2f miles",
				mDateFormat.format(histDate), miles);
		holder.historyText.setText(itemText);
		return convertView;
	}

	private class ViewHolder {

		TextView historyText;
	}
}
