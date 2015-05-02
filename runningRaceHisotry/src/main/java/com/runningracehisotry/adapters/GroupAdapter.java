package com.runningracehisotry.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.runningracehisotry.R;
import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.models.Group;
import com.runningracehisotry.models.Message;
import com.runningracehisotry.models.User;
import com.runningracehisotry.webservice.ServiceApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quynt3 on 2015/04/18.
 */
public class GroupAdapter extends BaseAdapter {

    private List<Group> groups;
    private LayoutInflater mInflater;

    public GroupAdapter(Dialog dialog, List<Group> groups) {
        super();
        this.groups = groups;
        this.mInflater = (LayoutInflater) dialog.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (groups != null) {

            return groups.size();
        }

        return 0;
    }

    @Override
    public Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parrent) {

        Group group = groups.get(position);
        ViewHolder holder = null;
        if (converView == null) {

            converView = mInflater.inflate(
                    R.layout.row_group, parrent,
                    false);
            holder = new ViewHolder();
            holder.text = (TextView) converView
                    .findViewById(R.id.row_group_name);
            converView.setTag(holder);
        } else {

            holder = (ViewHolder) converView.getTag();
        }

        holder.text.setText(group.getGroupName());

        return converView;
    }

    private class ViewHolder {
        TextView text;
    }
}
