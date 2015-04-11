package com.runningracehisotry.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.runningracehisotry.R;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Shoe;

import java.util.ArrayList;
import java.util.List;

/**
 * create by NTQ
 */
public class NewMyShoeAdapter extends BaseAdapter {

    private Context mContext;
    private List<Shoe> lstShoes;
    private LayoutInflater mInflater;
    //constructor function
    public NewMyShoeAdapter(Context context, List<Shoe> list) {
        this.mContext = context;
        this.lstShoes = new ArrayList<Shoe>(list);
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //get total object
    @Override
    public int getCount() {
        if(lstShoes != null){
            return this.lstShoes.size();
        }
        else{
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
        return lstShoes.get(position).getShoeId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        float distanceF = shoe.getTotalMiles();
        holder.detail.setText(String.format("%.2f miles (%.2f km)", distanceF, distanceF / 0.62137119));
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView name, type, detail;
        RelativeLayout itemLayout, delete;

    }

}