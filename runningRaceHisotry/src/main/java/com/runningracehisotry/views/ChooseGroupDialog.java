/**
 *
 */
package com.runningracehisotry.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.runningracehisotry.R;
import com.runningracehisotry.adapters.GroupAdapter;
import com.runningracehisotry.models.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ubuntu
 * 
 */
public class ChooseGroupDialog extends Dialog {

	private Button btnCancel, btnDone, btnCreate;
	private View.OnClickListener mCancelClick;
	private View.OnClickListener mDoneClick;
    private View.OnClickListener mAddGroupClick;
    private ListView lvGroups;
    private AdapterView.OnItemClickListener onListViewItemClick;
	private boolean isCancelable;
    private List<Group> groups;

	/**
	 * @param context
	 */
	public ChooseGroupDialog(Context context, List<Group> groups, View.OnClickListener mCancelClick, View.OnClickListener mDoneClick, AdapterView.OnItemClickListener onListViewItemClick, View.OnClickListener mAddGroupClick) {
		super(context);
		// TODO Auto-generated constructor stub
		this.isCancelable = true;
        this.groups = groups;

        this.mCancelClick = mCancelClick;
        this.mDoneClick = mDoneClick;
        this.onListViewItemClick = onListViewItemClick;
        this.mAddGroupClick = mAddGroupClick;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_choose_group);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Window window = getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);

		setCancelable(true);

        initComponent();

		if (isCancelable) {

			View rootLayout = findViewById(R.id.parent);
			rootLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}

		if (mCancelClick != null) {
			btnCancel.setOnClickListener(mCancelClick);
		}

        if (mDoneClick != null) {
            btnDone.setOnClickListener(mDoneClick);
        }

        if(mAddGroupClick != null) {
            btnCreate.setOnClickListener(mAddGroupClick);
        }

        if(groups == null) {
            groups = new ArrayList<Group>();
        }
        GroupAdapter adapter = new GroupAdapter(this, groups);
        lvGroups.setAdapter(adapter);

        if(onListViewItemClick != null) {
            lvGroups.setOnItemClickListener(onListViewItemClick);
        }
	}

    private void initComponent() {
        btnDone = (Button) findViewById(R.id.btn_dialog_done);
        btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
        btnCreate = (Button) findViewById(R.id.btn_dialog_create);
        lvGroups = (ListView) findViewById(R.id.lv_dialog_group);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
