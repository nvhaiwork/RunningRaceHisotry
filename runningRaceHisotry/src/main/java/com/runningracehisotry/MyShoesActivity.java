package com.runningracehisotry;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.runningracehisotry.adapters.MyShoesAdapter;
import com.runningracehisotry.adapters.MyShoesAdapter.OnShoeItemClickListenner;
import com.runningracehisotry.adapters.MyShoesAdapter.OnShoeItemDelete;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.views.CustomLoadingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

public class MyShoesActivity extends BaseActivity implements
		OnShoeItemClickListenner, OnShoeItemDelete {

	private ListView mShoeList;
	private boolean isSelectShoe;
	private MyShoesAdapter mShoesAdapter;

	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_shoes;
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		isSelectShoe = getIntent().getBooleanExtra(
				Constants.INTENT_SELECT_SHOE_FOR_RACE, false);
		mShoeList = (ListView) findViewById(R.id.my_shoes_list);
		mBotLeftBtnTxt.setVisibility(View.VISIBLE);
		mBotRightBtnTxt.setVisibility(View.VISIBLE);
		mBottomBtnLayout.setBackgroundColor(getResources().getColor(
				R.color.text_button_bg_my_shoes));
		if (isSelectShoe) {

			mBotRightBtnTxt.setText(getString(R.string.done));
		} else {

			mBotRightBtnTxt.setText(getString(R.string.add));
		}

		mShoeList.setOnItemClickListener(this);
		new FetchShoesAsycn().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == Constants.REQUETS_CODE_ADD_SHOE) {

				mShoesAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.bottom_button_right_text:

			if (isSelectShoe) {

				int selected = mShoesAdapter.getSelectedPosition();
				if (selected >= 0) {

					Intent addShoeIntent = new Intent();
					addShoeIntent.putExtra(
							Constants.INTENT_SELECT_SHOE_FOR_RACE, selected);
					setResult(RESULT_OK, addShoeIntent);
				} else {

					setResult(RESULT_CANCELED);
				}

				finish();
			} else {

				Intent addShoeIntent = new Intent(MyShoesActivity.this,
						AddShoeActivity.class);
				startActivityForResult(addShoeIntent,
						Constants.REQUETS_CODE_ADD_SHOE);
			}

			return;
		}

		super.onClick(v);
	}

	@Override
	public void onShoeItemDelete(ParseObject shoe) {
		// TODO Auto-generated method stub

		if (mShoes != null) {

			try {

				shoe.delete();
				mShoes.remove(shoe);
				mShoesAdapter.notifyDataSetChanged();
				mUser.put(Constants.SHOES, mShoes);
				mUser.saveInBackground();
			} catch (ParseException e) {

				LogUtil.e("onShoeItemDelete", e.getMessage());
			}
		}
	}

	@Override
	public void onShoeItemClick(int position) {
		// TODO Auto-generated method stub
		if (!isSelectShoe) {

			Intent addShoeIntent = new Intent(MyShoesActivity.this,
					AddShoeActivity.class);
			addShoeIntent.putExtra(Constants.INTENT_ADD_SHOE, position);
			startActivityForResult(addShoeIntent,
					Constants.REQUETS_CODE_ADD_SHOE);
		}
	}

	private class FetchShoesAsycn extends AsyncTask<Void, Void, Boolean> {

		private Dialog loadingDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadingDialog = CustomLoadingDialog.show(MyShoesActivity.this, "",
					"", false, false);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (mShoes != null) {

				for (ParseObject shoe : mShoes) {

					try {

						shoe.fetchIfNeeded();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						LogUtil.e("fetchIfNeeded", e.getMessage());
						return false;
					}
				}
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			mShoesAdapter = new MyShoesAdapter(MyShoesActivity.this, mShoes,
					isSelectShoe);
			mShoesAdapter.setShoeItemClick(MyShoesActivity.this);
			mShoesAdapter.setShoeItemDelete(MyShoesActivity.this);
			if (result) {

				mShoeList.setAdapter(mShoesAdapter);
			}

			loadingDialog.dismiss();
		}
	}

}
