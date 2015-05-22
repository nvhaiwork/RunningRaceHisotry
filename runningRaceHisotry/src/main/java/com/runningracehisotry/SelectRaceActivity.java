/**
 * 
 */
package com.runningracehisotry;

import java.util.ArrayList;
import com.parse.ParseUser;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Friend;
import com.runningracehisotry.models.User;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author nvhaiwork
 *
 */
public class SelectRaceActivity extends BaseActivity {

	private int mFriendRace;
	private TextView mWelcomeTxt, mTitleTxt;
	private LinearLayout mAddRaceSuccessLayout;
	private ImageView mRace5kBtn, mRace10kBtn, mRace15kBtn, mRaceHalfMarBtn,
			mRaceFullMarBtn, mShoesBtn, mAddRaceBtn, mRaceOther, mRaceOtherFriend;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.runningracehisotry.BaseActivity#addContent()
	 */
	@Override
	protected int addContent() {
		// TODO Auto-generated method stub
		return R.layout.activity_select_race;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.runningracehisotry.BaseActivity#initView()
	 */
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();

		mFriendRace = getIntent().getIntExtra(
				Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
		mRace5kBtn = (ImageView) findViewById(R.id.select_race_5k);
		mRace10kBtn = (ImageView) findViewById(R.id.select_race_10k);
		mRace15kBtn = (ImageView) findViewById(R.id.select_race_15k);
		mTitleTxt = (TextView) findViewById(R.id.selecet_race_title);
		mShoesBtn = (ImageView) findViewById(R.id.select_race_my_shoes);
		mAddRaceBtn = (ImageView) findViewById(R.id.select_race_add_race);
		mWelcomeTxt = (TextView) findViewById(R.id.selecet_race_welcome_back);
		mRaceFullMarBtn = (ImageView) findViewById(R.id.select_race_full_marathon);
		mRaceHalfMarBtn = (ImageView) findViewById(R.id.select_race_half_marathon);
		mAddRaceSuccessLayout = (LinearLayout) findViewById(R.id.select_race_add_race_success);
        mRaceOther  = (ImageView) findViewById(R.id.select_race_other);
        mRaceOtherFriend  = (ImageView) findViewById(R.id.select_race_other_friend);
		mShoesBtn.setOnClickListener(this);
		mRace5kBtn.setOnClickListener(this);
		mRace10kBtn.setOnClickListener(this);
		mRace15kBtn.setOnClickListener(this);
		mAddRaceBtn.setOnClickListener(this);
		mRaceFullMarBtn.setOnClickListener(this);
		mRaceHalfMarBtn.setOnClickListener(this);
        mRaceOther.setOnClickListener(this);
        mRaceOtherFriend.setOnClickListener(this);
		// For ver 1.1, old user does not have friends, so create new friends
		// array
		if (mFriends == null) {

			mFriends = new ArrayList<ParseUser>();
		}

		// Display user welcome
		if (mFriendRace != -1) {

			/*ParseUser user = mFriends.get(mFriendRace);*/
            Friend friend = null;
			mShoesBtn.setVisibility(View.GONE);
			mWelcomeTxt.setVisibility(View.GONE);
			mAddRaceBtn.setVisibility(View.GONE);
            mRaceOther.setVisibility(View.GONE);
            mRaceOtherFriend.setVisibility(View.VISIBLE);
			//String username = (String) friend.getFull_name();
			String username = "Friend";
			String userHis = String.format(
					getString(R.string.select_race_user_history), username);
			mTitleTxt.setText(userHis);
		} else if (RunningRaceApplication.getInstance().getCurrentUser() != null) {
            User user = RunningRaceApplication.getInstance().getCurrentUser();
			String username = user.getFull_name();
			String welcomStr = String.format(
					getString(R.string.select_race_welcome_back), username);
			String userHis = String.format(
					getString(R.string.select_race_user_history), username);
			mTitleTxt.setText(userHis);
			mWelcomeTxt.setText(welcomStr);
		} else {

			CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME, "");
			CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD, "");
			Intent loginIntent = new Intent(SelectRaceActivity.this,LoginChoiceScreen.class);
			startActivity(loginIntent);
			finish();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (mFriendRace != -1) {

			mCurrentClassName = "";
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == Constants.REQUETS_CODE_ADD_RACE) {

				mAddRaceSuccessLayout.setVisibility(View.VISIBLE);
				boolean isSoundOn = CustomSharedPreferences.getPreferences(
						Constants.PREF_SETTING_SOUND, true);
                LogUtil.d("SOUND", "SOUND ON ADD ARCE: " + isSoundOn);
				final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				final int currentVol = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (isSoundOn) {

					int settingVol = CustomSharedPreferences.getPreferences(
							Constants.PREF_SETTING_SOUND_LEVEL, 50);
					MediaPlayer mediaPlayer = MediaPlayer.create(
							getApplicationContext(), R.raw.win);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							settingVol, 0);
					mediaPlayer.start();
				}

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mAddRaceSuccessLayout.setVisibility(View.GONE);
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								currentVol, 0);
					}
				}, 2000);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		boolean isStartRaceDetail = false;
		Intent racesDetail = new Intent(SelectRaceActivity.this,
				RacesDetailActivity.class);
		switch (v.getId()) {
		case R.id.select_race_my_shoes:

			Intent myShoesIntent = new Intent(SelectRaceActivity.this,
					MyShoesActivity.class);
			startActivity(myShoesIntent);
			return;
		case R.id.select_race_add_race:

			AddRaceActivity.setRaceUpdate(null);
			Intent addRaceIntent = new Intent(SelectRaceActivity.this,
					AddRaceActivity.class);
			startActivityForResult(addRaceIntent,
					Constants.REQUETS_CODE_ADD_RACE);
			return;
		case R.id.select_race_5k:

			racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
					Constants.SELECT_RACE_5K);
			isStartRaceDetail = true;
			break;
		case R.id.select_race_10k:

			racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
					Constants.SELECT_RACE_10K);
			isStartRaceDetail = true;
			break;
		case R.id.select_race_15k:

			racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
					Constants.SELECT_RACE_15K);
			isStartRaceDetail = true;
			break;
		case R.id.select_race_half_marathon:

			racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
					Constants.SELECT_RACE_HALF_MAR);
			isStartRaceDetail = true;
			break;
		case R.id.select_race_full_marathon:

			racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
					Constants.SELECT_RACE_FULL_MAR);
			isStartRaceDetail = true;
			break;
            case R.id.select_race_other:

                racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
                        Constants.SELECT_RACE_OTHER);
                isStartRaceDetail = true;
                break;
            case R.id.select_race_other_friend:

                racesDetail.putExtra(Constants.INTENT_SELECT_RACE,
                        Constants.SELECT_RACE_OTHER);
                isStartRaceDetail = true;
                break;

        }

		if (isStartRaceDetail) {

			racesDetail.putExtra(Constants.INTENT_SELECT_RACE_FROM_FRIENDS,
					mFriendRace);
			startActivity(racesDetail);
		}

		super.onClick(v);
	}

}
