package com.runningracehisotry;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.runningracehisotry.adapters.MyShoesAdapter;
import com.runningracehisotry.adapters.MyShoesAdapter.OnShoeItemClickListenner;
import com.runningracehisotry.adapters.MyShoesAdapter.OnShoeItemDelete;
import com.runningracehisotry.adapters.NewMyShoeAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.GetAllShoesRelatedObjectRequest;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyShoesActivity extends BaseActivity implements
		OnShoeItemClickListenner, OnShoeItemDelete {

	private ListView mShoeList;
	private boolean isSelectShoe;
	private MyShoesAdapter mShoesAdapter;
    private CustomLoadingDialog mLoadingDialog;

    private String logTag = "MyShoesActivity";


    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
            //mLoadingDialog = CustomLoadingDialog.show(MyShoesActivity.this,"", "", false, false);
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, final Object data) {

            if (methodName.equals(ServiceConstants.METHOD_GET_ALL_SHOES_WITH_RELATE_OBJ)) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                processGotListShoe(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utilities.showAlertMessage(MyShoesActivity.this, "Error Parse when get list shoes", "");
                            } finally {
                    /*if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }*/
                            }
                        }
                    });


            }
            /*if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }*/
        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };

    private void processGotListShoe(Object data) throws JSONException {
        JSONArray arr= new JSONArray(data.toString());
        //JSONObject jsonObjectReceive = new JSONObject(data.toString());
        LogUtil.d(logTag, "Response: " + data.toString());
        int len = arr.length();
        List<Shoe> lst = new ArrayList<Shoe>();
        if(len > 0){
            Shoe shoe = null;
            for (int i = 0; i< len; i++){
                JSONObject obj = arr.getJSONObject(i);
                LogUtil.d(logTag,"Response Obj: " + i + " toString: " + obj.toString());
                int shoeId = obj.getInt("id");
                int userId = obj.getInt("user_id");
                String model = obj.getString("model");
                String brand = obj.getString("brand");
                String imageUrl = obj.getString("image_url");
                float miles = (float) obj.getDouble("miles_on_shoes");
                shoe = new Shoe(shoeId,brand, model,imageUrl,miles,userId);
                lst.add(shoe);
            }
        }
        NewMyShoeAdapter adapter = new NewMyShoeAdapter(this, lst);
        mShoeList.setAdapter(adapter);
    }

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
		//new FetchShoesAsycn().execute();
        loadListShoesOfCurrentUser();
	}

    private void loadListShoesOfCurrentUser() {
        GetAllShoesRelatedObjectRequest request = new GetAllShoesRelatedObjectRequest();
        request.setListener(callBackEvent);
        new Thread(request).start();
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

	/*private class FetchShoesAsycn extends AsyncTask<Void, Void, Boolean> {

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
	}*/

}
