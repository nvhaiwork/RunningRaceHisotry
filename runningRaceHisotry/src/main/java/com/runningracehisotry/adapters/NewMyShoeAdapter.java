package com.runningracehisotry.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runningracehisotry.models.Shoe;

import java.util.ArrayList;
import java.util.List;

/**
 * create by NTQ
 */
public class NewMyShoeAdapter extends BaseAdapter {

    private Context mContext;
    private List<Shoe> lstShoes;
    //constructor function
    public NewMyShoeAdapter(Context context, List<Shoe> list) {
        this.mContext = context;
        this.lstShoes = new ArrayList<Shoe>(list);
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
    public Object getItem(int position) {
        return null;
    }
    // get Item Id
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    static class ContactItemViewHolder {
        ImageView image;
        TextView name, type, detail;
        RelativeLayout itemLayout, delete;

    }

}