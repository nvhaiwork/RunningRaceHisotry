package com.runningracehisotry.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runningracehisotry.R;
import com.runningracehisotry.models.History;
import com.runningracehisotry.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by QuyNguyen on 4/16/2015.
 */
public class NewShoeDistanceHistoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private SimpleDateFormat mDateFormat;
    private List<History> mShoeHistories;

    public NewShoeDistanceHistoryAdapter(Context context, List<History> shoeHistories) {
        super();

        this.mShoeHistories = shoeHistories;
        this.mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public History getItem(int position) {
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
        History history =  getItem(position);
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


        String itemText = Utilities.getDisplayedHistoryOfShoe(history);
        holder.historyText.setText(itemText);
        return convertView;
    }

    private class ViewHolder {

        TextView historyText;
    }
}
