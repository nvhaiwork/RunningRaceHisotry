package com.runningracehisotry;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.runningracehisotry.adapters.RunnersAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;
import com.runningracehisotry.views.CustomAlertDialog.OnPositiveButtonClick;

public class RunnerActivity extends BaseActivity {

	private ListView mListView;
	private List<ParseUser> mRunners;
	private RunnersAdapter mRunnersAdapter;

	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_runners;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		mListView = (ListView) findViewById(R.id.runners_you_may_know_list);
		final Dialog dialog = CustomLoadingDialog.show(RunnerActivity.this, "",
				"", false, false);

		// Set adpater for listview
		mRunners = new ArrayList<ParseUser>();
		mRunnersAdapter = new RunnersAdapter(RunnerActivity.this, mRunners,
				mImageLoader);
		mListView.setAdapter(mRunnersAdapter);
		mListView.setOnItemClickListener(this);

		// Query available runners
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException error) {
				// TODO Auto-generated method stub

				if (users != null) {

					for (ParseUser user : users) {

						if (mUser.getObjectId().equals(user.getObjectId())) {

							continue;
						}

						if (user.getString(Constants.FULLNAME) == null
								|| user.getString(Constants.FULLNAME).trim()
										.equals("")) {

							continue;
						}

						boolean isFriend = false;
						for (ParseUser friend : mFriends) {

							if (friend.getObjectId().equals(user.getObjectId())) {

								isFriend = true;
								break;
							}
						}

						if (isFriend) {

							continue;
						}

						mRunners.add(user);
					}
				}

				mRunnersAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mUser.put(Constants.FRIENDS, mFriends);
		mUser.saveInBackground();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		super.onItemClick(adapterView, view, position, id);

		if (adapterView.getId() == R.id.runners_you_may_know_list) {

			ParseUser user = (ParseUser) adapterView.getAdapter().getItem(
					position);
			doAddFriend(user);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		backToHome();
	}

	/**
	 * Display pop up allow user add friend to community group
	 * */
	private void doAddFriend(final ParseUser user) {

		final CustomAlertDialog dialog = new CustomAlertDialog(
				RunnerActivity.this);
		dialog.setCancelableFlag(false);
		dialog.setTitle(getString(R.string.dialog_user_title));
		dialog.setMessage(getString(R.string.dialog_user_message));
		dialog.setNegativeButton(getString(R.string.no),
				new OnNegativeButtonClick() {

					@Override
					public void onButtonClick(final View view) {
						// TODO Auto-generated method stub

						dialog.dismiss();
					}
				});
		dialog.setPositiveButton(getString(R.string.yes),
				new OnPositiveButtonClick() {

					@Override
					public void onButtonClick(View view) {
						// TODO Auto-generated method stub

						mRunners.remove(user);
						mFriends.add(user);
						dialog.dismiss();
						mRunnersAdapter.notifyDataSetChanged();
					}
				});

		dialog.show();
	}
}
