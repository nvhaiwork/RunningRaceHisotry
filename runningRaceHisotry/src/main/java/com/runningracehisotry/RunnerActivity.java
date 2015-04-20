package com.runningracehisotry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.runningracehisotry.adapters.NewMyShoeAdapter;
import com.runningracehisotry.adapters.NewRunnerAdapter;
import com.runningracehisotry.adapters.RunnersAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Runner;
import com.runningracehisotry.models.Shoe;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;
import com.runningracehisotry.views.CustomAlertDialog.OnPositiveButtonClick;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddGroupMemberRequest;
import com.runningracehisotry.webservice.base.AddGroupRequest;
import com.runningracehisotry.webservice.base.GetAllRunnerRequest;
import com.runningracehisotry.webservice.base.GetAllShoesRelatedObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RunnerActivity extends BaseActivity {

    private ListView mListView;
    private List<Runner> mRunners;
    private NewRunnerAdapter mRunnersAdapter;
    private int friendId;
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
		/*final Dialog dialog = CustomLoadingDialog.show(RunnerActivity.this, "",
				"", false, false);*/

        // Set adpater for listview
        mRunners = new ArrayList<Runner>();
        mRunnersAdapter = new NewRunnerAdapter(RunnerActivity.this, mRunners,
                mImageLoader);
		/*mListView.setAdapter(mRunnersAdapter);
		mListView.setOnItemClickListener(this);*/
        loadListRunner();
        // Query available runners
		/*ParseQuery<ParseUser> query = ParseUser.getQuery();
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
		});*/

    }

    private IWsdl2CodeEvents callBackEvent = new IWsdl2CodeEvents() {
        @Override
        public void Wsdl2CodeStartedRequest() {
            //mLoadingDialog = CustomLoadingDialog.show(MyShoesActivity.this,"", "", false, false);
        }

        @Override
        public void Wsdl2CodeFinished(String methodName, final Object data) {

            if (methodName.equals(ServiceConstants.METHOD_GET_ALL_RUNNERS)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processGotListRunner(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertMessage(RunnerActivity.this, "Error Parse when get list shoes", "");
                        } finally {
                        }
                    }
                });
            }
            else if (methodName.equals(ServiceConstants.METHOD_ADD_GROUP)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processAddedGroup(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertMessage(RunnerActivity.this, "Error Parse when get list shoes", "");
                        } finally {
                        }
                    }
                });
            }
            else if (methodName.equals(ServiceConstants.METHOD_ADD_GROUP_MEMBER)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processAddedMemberOfGroup(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utilities.showAlertMessage(RunnerActivity.this, "Error Parse when get list shoes", "");
                        } finally {
                        }
                    }
                });
            }
        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {

            /*if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }*/
        }

        @Override
        public void Wsdl2CodeEndedRequest() {

        }
    };

    private void loadListRunner() {
        GetAllRunnerRequest request = new GetAllRunnerRequest();
        request.setListener(callBackEvent);
        new Thread(request).start();
    }

    private void processGotListRunner(Object data) throws JSONException{
        JSONArray arr= new JSONArray(data.toString());
        //JSONObject jsonObjectReceive = new JSONObject(data.toString());
        LogUtil.d(mCurrentClassName, "Response processGotListRunner: " + data.toString());
        int len = arr.length();
        mRunners = new ArrayList<Runner>();
//        if(len > 0){
//            Runner runner = null;
//            for (int i = 0; i< len; i++){
//                JSONObject obj = arr.getJSONObject(i);
//                LogUtil.d(mCurrentClassName,"Response Obj: " + i + " toString: " + obj.toString());
//                int runnerId = obj.getInt("id");
//                String name = obj.getString("full_name");
//                String imageUrl = obj.getString("profile_image");
//
//                runner = new Runner(runnerId,name + " |" + imageUrl,imageUrl);
//                mRunners.add(runner);
//            }
//        }
        Type listType = new TypeToken<List<Runner>>(){}.getType();
        Gson  gson = new Gson();
        mRunners = gson.fromJson(data.toString(), listType);

        mRunnersAdapter = new NewRunnerAdapter(RunnerActivity.this, mRunners,
                mImageLoader);
        mListView.setAdapter(mRunnersAdapter);
        mListView.setOnItemClickListener(this);
        mRunnersAdapter.notifyDataSetChanged();
        //mRunnersAdapter.notifyDataSetChanged();


    }


    private void processAddedMemberOfGroup(Object data) throws JSONException {

        JSONObject obj = new JSONObject(data.toString());
        //JSONObject jsonObjectReceive = new JSONObject(data.toString());
        LogUtil.d(mCurrentClassName, "Response processAddedMemberOfGroup: " + data.toString());
        String result = obj.getString("result");

        if(result.equalsIgnoreCase("true")){
            LogUtil.d(mCurrentClassName, "Response processAddedMemberOfGroup done");
            mRunnersAdapter.notifyDataSetChanged();
        }
        else{
            LogUtil.d(mCurrentClassName, "Response processAddedMemberOfGroup failed");
        }
    }


    private void processAddedGroup(Object data)  throws JSONException {
        JSONObject obj = new JSONObject(data.toString());
        //JSONObject jsonObjectReceive = new JSONObject(data.toString());
        LogUtil.d(mCurrentClassName, "Response processAddedGroup: " + data.toString());
        String result = obj.getString("result");
        int groupId = obj.getInt("group_id");

        if(result.equalsIgnoreCase("true")){
            LogUtil.d(mCurrentClassName, "Response processAddedGroup done with frienId|groupID: "
                    + friendId + "|" + groupId);
            AddGroupMemberRequest request = new AddGroupMemberRequest(friendId, groupId);
            request.setListener(callBackEvent);
            new Thread(request).start();
        }
        else{
            LogUtil.d(mCurrentClassName, "Response processAddedGroup failed");
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
		super.onPause();
//		mUser.put(Constants.FRIENDS, mFriends);
//		mUser.saveInBackground();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        // TODO Auto-generated method stub
        super.onItemClick(adapterView, view, position, id);

        if (adapterView.getId() == R.id.runners_you_may_know_list) {

            Runner user = (Runner) adapterView.getAdapter().getItem(
                    position);
            LogUtil.d(mCurrentClassName,"Add runner id: " + user.getId());
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
    private void doAddFriend(final Runner user) {

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
                        dialog.dismiss();
                        processAddGroup(user);
                        LogUtil.d(mCurrentClassName,"Execute Add");


                    }
                });

        dialog.show();
    }

    private void processAddGroup(Runner user) {
        AddGroupRequest request = new AddGroupRequest();
        request.setListener(callBackEvent);
        new Thread(request).start();
        friendId = user.getId();


        mRunners.remove(user);
        mRunnersAdapter.notifyDataSetChanged();
    }
}
