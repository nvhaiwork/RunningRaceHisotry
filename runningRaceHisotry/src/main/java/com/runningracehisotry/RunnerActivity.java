package com.runningracehisotry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runningracehisotry.adapters.NewRunnerAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Group;
import com.runningracehisotry.models.Runner;
import com.runningracehisotry.utilities.LogUtil;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.ChooseGroupDialog;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;
import com.runningracehisotry.views.CustomAlertDialog.OnPositiveButtonClick;
import com.runningracehisotry.webservice.IWsdl2CodeEvents;
import com.runningracehisotry.webservice.ServiceConstants;
import com.runningracehisotry.webservice.base.AddGroupMemberRequest;
import com.runningracehisotry.webservice.base.AddGroupRequest;
import com.runningracehisotry.webservice.base.GetAllGroupUserRequest;
import com.runningracehisotry.webservice.base.GetAllRunnerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RunnerActivity extends BaseActivity {

    private ListView mListView;
    private List<Runner> mRunners;
    private Runner chosenRunner;
    private NewRunnerAdapter mRunnersAdapter;
    private int friendId;
    private CustomLoadingDialog mLoadingDialog;
    private List<Group> groups;
    private Group chosenGroup;
    private Dialog inputDialog;
    private Dialog chosenDialog;
    private CustomAlertDialog successDialog;
    private String inputGroupName;

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
        if(mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.show(RunnerActivity.this, "", "", false, false);
        }
        loadListRunner();


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
                            chosenGroup = null;
                            //e.printStackTrace();
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                                ex.printStackTrace();
                            }
                            Utilities.showAlertMessage(RunnerActivity.this, getResources().getString(R.string.runner_get_failed), "");
                        }
                        finally {
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                            }
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
                            chosenGroup = null;
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                            }
                            Utilities.showAlertMessage(RunnerActivity.this, "Error Parse when get list shoes", "");
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
                            chosenGroup = null;
                            e.printStackTrace();
                            Utilities.showAlertMessage(RunnerActivity.this, "Error Parse when get list shoes", "");
                        } finally {
                            try{
                                if (mLoadingDialog.isShowing()) {
                                    mLoadingDialog.dismiss();
                                }
                            }
                            catch(Exception ex){
                            }
                        }
                    }
                });
            } else if(methodName.equals(ServiceConstants.METHOD_GET_ALL_GROUP)) {
                LogUtil.d(Constants.LOG_TAG, "processGetGroupOfUser return: " + data);
                if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Group>>(){}.getType();
                List<Group> lstGroup = gson.fromJson(data.toString(), listType);
                showChooseGroupDialog(chosenRunner, lstGroup);
            }
        }

        @Override
        public void Wsdl2CodeFinishedWithException(Exception ex) {
            chosenGroup = null;
            try{
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
            catch(Exception exc){
            }
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
            mRunners.remove(chosenRunner);
            mRunnersAdapter.notifyDataSetChanged();
            showSuccessDialog(chosenRunner.getFullName(), chosenGroup.getGroupName());
            chosenGroup = null;
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

        chosenGroup = new Group();
        chosenGroup.setGroupId(groupId);
        chosenGroup.setGroupName(inputGroupName);

        if(result.equalsIgnoreCase("true")){
            LogUtil.d(mCurrentClassName, "Response processAddedGroup done with frienId|groupID: "
                    + friendId + "|" + groupId);
            AddGroupMemberRequest request = new AddGroupMemberRequest(String.valueOf(chosenRunner.getId()), String.valueOf(groupId));
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

            chosenRunner = user;

            getGroups();
        }
    }

    private void getGroups() {
        mLoadingDialog = CustomLoadingDialog.show(this, "", "", false, false);

        GetAllGroupUserRequest request = new GetAllGroupUserRequest();
        request.setListener(callBackEvent);
        new Thread(request).start();
        LogUtil.d(Constants.LOG_TAG, "getGroupOfUser call");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        backToHome();
    }

    /**
     * Display pop up allow user add friend to community group
     * */
    private void showSuccessDialog(String username, String groupName) {

        successDialog = new CustomAlertDialog(
                RunnerActivity.this);
        successDialog.setCancelableFlag(false);
        successDialog.setTitle("Add user");
        String message = String.format("Add user: %s to group: %s Successful!", new String[]{username, groupName});
        successDialog.setMessage(message);
        successDialog.setPositiveButton(getString(R.string.ok),
                new OnPositiveButtonClick() {

                    @Override
                    public void onButtonClick(View view) {
                        // TODO Auto-generated method stub
                        successDialog.dismiss();
                    }
                });

        successDialog.show();
    }

    private void showChooseGroupDialog(final Runner user, final List<Group> groups) {
        View.OnClickListener onDoneClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenGroup == null) {
                    chosenGroup = groups.get(0);
                }

                AddGroupMemberRequest request = new AddGroupMemberRequest(String.valueOf(chosenRunner.getId()), String.valueOf(chosenGroup.getGroupId()));
                request.setListener(callBackEvent);
                new Thread(request).start();

                if(!mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                }

                if(chosenDialog != null && chosenDialog.isShowing()) {
                    chosenDialog.dismiss();
                }
            }
        };

        View.OnClickListener onCancelClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosenDialog != null && chosenDialog.isShowing()) {
                    chosenDialog.dismiss();
                }
                chosenGroup = null;
            }
        };

        View.OnClickListener onCreateClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenGroup = null;

                showInputGroupDialog();
            }
        };

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenGroup = groups.get(position);
            }
        };
        if(chosenDialog != null && chosenDialog.isShowing()) {
            chosenDialog.dismiss();
        }

        if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        chosenDialog = new ChooseGroupDialog(this, groups, onCancelClick, onDoneClick, itemClickListener, onCreateClick);
        chosenDialog.show();
    }


    private void processAddGroup(Runner user, String newGroupName) {
        if(mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        AddGroupRequest request = new AddGroupRequest(newGroupName);
        request.setListener(callBackEvent);
        new Thread(request).start();
        friendId = user.getId();


        mRunners.remove(user);
        mRunnersAdapter.notifyDataSetChanged();
    }

    private void showInputGroupDialog() {
        if(chosenDialog != null && chosenDialog.isShowing()) {
            chosenDialog.dismiss();
        }

        if(inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }

        inputDialog = new Dialog(this);
        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(R.layout.dialod_input_group);
        inputDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = inputDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        final EditText etGroup = (EditText) inputDialog
                .findViewById(R.id.alert_reset_email);
        TextView btnOK = (TextView) inputDialog
                .findViewById(R.id.alert_ok_btn);

        TextView btnCancel = (TextView) inputDialog
                .findViewById(R.id.alert_cancel_btn);

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                // TODO Auto-generated method stub

                if (!etGroup.getText().toString().equals("")) {
                    inputDialog.dismiss();
                    String groupName = etGroup.getText().toString();
                    inputGroupName = groupName;
                    processAddGroup(chosenRunner, groupName);
                }
            }
        });

        inputDialog.show();
    }
}
