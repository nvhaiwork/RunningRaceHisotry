package com.runningracehisotry;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseUser;
import com.runningracehisotry.adapters.RunnersAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.views.CustomLoadingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FriendsActivity extends BaseActivity {

	private ListView mFriendListview;
	private RunnersAdapter mFriendAdapter;

	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_friends;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		mFriendListview = (ListView) findViewById(R.id.friends_list);
		mFriendListview.setOnItemClickListener(this);

		// Initiation data
		new LoadFriendsAsyncTask().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		super.onItemClick(adapterView, view, position, id);

		if (adapterView.getId() == R.id.friends_list) {

			Intent selectRaceIntent = new Intent(FriendsActivity.this,
					SelectRaceActivity.class);
			selectRaceIntent.putExtra(
					Constants.INTENT_SELECT_RACE_FROM_FRIENDS, position);
			startActivity(selectRaceIntent);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		backToHome();
	}

	private class LoadFriendsAsyncTask extends AsyncTask<Void, Void, Void> {

		Dialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = CustomLoadingDialog.show(FriendsActivity.this, "", "",
					false, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			List<ParseUser> errorFriend = new ArrayList<ParseUser>();
			for (ParseUser friend : mFriends) {

				try {

					friend.fetchIfNeeded();
				} catch (Exception e) {

					errorFriend.add(friend);
					LogUtil.e("Load friend async", e.getMessage());
				}
			}

			mFriends.removeAll(errorFriend);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (mFriendAdapter == null) {

				mFriendAdapter = new RunnersAdapter(FriendsActivity.this,
						mFriends, mImageLoader);
				mFriendListview.setAdapter(mFriendAdapter);
			} else {

				mFriendAdapter.notifyDataSetChanged();
			}
			dialog.dismiss();
		}
	}
}
