/**
 * 
 */
package com.runningracehisotry;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.runningracehisotry.adapters.LeftMenuAdapter;
import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.MenuModel;
import com.runningracehisotry.utilities.CustomSharedPreferences;
import com.runningracehisotry.utilities.Utilities;
import com.runningracehisotry.views.CustomAlertDialog;
import com.runningracehisotry.views.CustomLoadingDialog;
import com.runningracehisotry.views.CustomAlertDialog.OnNegativeButtonClick;
import com.runningracehisotry.views.CustomAlertDialog.OnPositiveButtonClick;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author nvhaiwork
 *
 */
public class BaseActivity extends FragmentActivity implements OnClickListener,
		OnItemClickListener {

	private SlidingMenu mMenu;
	protected static ParseUser mUser;
	protected String mCurrentClassName;
	protected ImageLoader mImageLoader;
	private RelativeLayout mAboutLayout;
	protected UiLifecycleHelper uiHelper;
	protected DisplayImageOptions mOptions;
	protected LinearLayout mBottomBtnLayout;
	protected boolean isHideActionBarButtons;
	protected static List<ParseObject> mShoes;
	protected static List<ParseUser> mFriends;
	protected ImageView mBotLeftBtnImg, mBotRightBtnImg;
	protected static List<HashMap<String, Object>> mHistory;
	protected TextView mBotLeftBtnTxt, mBotRightBtnTxt, mBotMidBtnTxt;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		int layoutId = addContent();
		setContentView(layoutId);
		CustomSharedPreferences.init(getApplicationContext());
		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);
		mImageLoader = ImageLoader.getInstance();
		if (!mImageLoader.isInited()) {

			mImageLoader.init(ImageLoaderConfiguration
					.createDefault(BaseActivity.this));
		}

		initView();
	}

	/**
	 * Set layout xml file and other flags
	 * 
	 * @return xml layout id
	 * */
	protected int addContent() {

		return R.layout.activity_base;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
					}
				});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mCurrentClassName = this.getLocalClassName();
		uiHelper.onResume();
	}

	/**
	 * Back to home and finish activity
	 * */
	protected void backToHome() {

		Intent homeIntent = new Intent(BaseActivity.this,
				SelectRaceActivity.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
		finish();
	}

	/**
	 * Initiation views, get/set views
	 * */
	protected void initView() {

		mBotLeftBtnImg = (ImageView) findViewById(R.id.bottom_button_left_img);
		mBotLeftBtnTxt = (TextView) findViewById(R.id.bottom_button_left_text);
		mBotMidBtnTxt = (TextView) findViewById(R.id.bottom_button_center_text);
		mBotRightBtnImg = (ImageView) findViewById(R.id.bottom_button_right_img);
		mBotRightBtnTxt = (TextView) findViewById(R.id.bottom_button_right_text);
		mBottomBtnLayout = (LinearLayout) findViewById(R.id.bottom_button_layout);
		if (mBotMidBtnTxt != null) {

			mBotMidBtnTxt.setOnClickListener(this);
		}

		if (mBotLeftBtnImg != null) {

			mBotLeftBtnImg.setOnClickListener(this);
		}

		if (mBotLeftBtnTxt != null) {

			mBotLeftBtnTxt.setOnClickListener(this);
		}

		if (mBotRightBtnImg != null) {

			mBotRightBtnImg.setOnClickListener(this);
		}

		if (mBotRightBtnTxt != null) {

			mBotRightBtnTxt.setOnClickListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(R.layout.layout_header);

		if (!isHideActionBarButtons) {

			ImageView icSetting = (ImageView) actionBar.getCustomView()
					.findViewById(R.id.ic_action_setting);
			ImageView icMenu = (ImageView) actionBar.getCustomView()
					.findViewById(R.id.ic_action_menu);
			TextView icSave = (TextView) actionBar.getCustomView()
					.findViewById(R.id.ic_action_save);

			icMenu.setVisibility(View.VISIBLE);
			icMenu.setOnClickListener(this);
			if (this instanceof ProfileActivity) {

				icSave.setOnClickListener(this);
				icSave.setVisibility(View.VISIBLE);
				icSetting.setVisibility(View.INVISIBLE);
			} else if (this instanceof RacesDetailActivity) {

				icSetting.setOnClickListener(this);
				icSave.setVisibility(View.INVISIBLE);
				icSetting.setVisibility(View.INVISIBLE);
			} else {

				icSetting.setOnClickListener(this);
				icSave.setVisibility(View.INVISIBLE);
				icSetting.setVisibility(View.VISIBLE);
			}
		}

		setupLeftMenu();
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bottom_button_left_text:

			finish();
			return;
		case R.id.bottom_button_right_img:

			doSetting();
			return;
		case R.id.setting_about:

			mAboutLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.ic_action_menu:

			mMenu.toggle();
			break;
		case R.id.ic_action_setting:

			doSetting();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		if (adapterView.getId() == R.id.menu_listview) {

			MenuModel menu = (MenuModel) adapterView.getAdapter().getItem(
					position);
			if (menu.getDislayText().equals(
					getString(R.string.menu_your_history))) {

				if (!mCurrentClassName
						.equalsIgnoreCase(SelectRaceActivity.class
								.getSimpleName())) {
					Intent historyIntent = new Intent(BaseActivity.this,
							SelectRaceActivity.class);
					historyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					historyIntent.putExtra(
							Constants.INTENT_SELECT_RACE_FROM_FRIENDS, -1);
					startActivity(historyIntent);
				}
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_runner_you_may_know))) {

				Intent runnerIntent = new Intent(BaseActivity.this,
						RunnerActivity.class);
				runnerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(runnerIntent);
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_your_community))) {

				Intent communityIntent = new Intent(BaseActivity.this,
						FriendsActivity.class);
				communityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(communityIntent);
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_profile_update))) {

				Intent profileIntent = new Intent(BaseActivity.this,
						ProfileActivity.class);
				profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(profileIntent);
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_share_fb))) {

				FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
						BaseActivity.this)
						.setDescription(getString(R.string.share_app_menu))
						.setCaption(getString(R.string.share_app_menu))
						.setLink(
								"https://play.google.com/store/apps/details?id=com.runningracehisotry")
						.build();

				uiHelper.trackPendingDialogCall(shareDialog.present());
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_share_twitter))) {

				Utilities.doShare(BaseActivity.this, "com.twitter.android",
						getString(R.string.share_app_menu), null);
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_sign_out))) {

				doSignout();
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_running_website))) {

				Utilities.openBrower(BaseActivity.this,
						Constants.RUNNING_WEBSITE);
			} else if (menu.getDislayText().equals(
					getString(R.string.menu_contact_us))) {

				Utilities.contactUs(BaseActivity.this);
			}  else if (menu.getDislayText().equals(
                    getString(R.string.menu_chat))) {

                Intent chatIntent = new Intent(BaseActivity.this,
                        ChatFriendActivity.class);
                chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(chatIntent);
            }

			mMenu.toggle();
			return;
		}
	}

	/**
	 * Sign out current user's login
	 * */
	private void doSignout() {

		final CustomAlertDialog dialog = new CustomAlertDialog(
				BaseActivity.this);
		dialog.setCancelableFlag(false);
		dialog.setTitle(getString(R.string.dialog_logout_title));
		dialog.setMessage(getString(R.string.dialog_logout_message));
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

						saveUserData();
						dialog.dismiss();
					}
				});

		dialog.show();
	}

	/**
	 * Save user date when logout
	 * */
	private void saveUserData() {

		final Dialog dialog = CustomLoadingDialog.show(BaseActivity.this, "",
				"", false, false);
//		if (mShoes != null) {
//
//			mUser.put(Constants.SHOES, mShoes);
//		}
//
//		if (mHistory != null) {
//
//			mUser.put(Constants.DATA, mHistory);
//		}
//
//		if (mFriends != null) {
//
//			mUser.put(Constants.FRIENDS, mFriends);
//		}

//		mUser.saveInBackground(new SaveCallback() {
//
//			@Override
//			public void done(ParseException arg0) {
//				// TODO Auto-generated method stub
//
//				CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME,
//						"");
//				CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD,
//						"");
//
//				// Setting
//				CustomSharedPreferences.setPreferences(
//						Constants.PREF_SETTING_SOUND, false);
//				CustomSharedPreferences.setPreferences(
//						Constants.PREF_SETTING_LINK_FACEBOOK, false);
//				CustomSharedPreferences.setPreferences(
//						Constants.PREF_SETTING_SOUND_LEVEL, 0);
//				Intent loginChoiceIntent = new Intent(BaseActivity.this,
//						LoginChoiceScreen.class);
//				loginChoiceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//						| Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(loginChoiceIntent);
//				mShoes = null;
//				mHistory = null;
//				mFriends = null;
//				AddRaceActivity.setRace(null);
//				dialog.dismiss();
//				finish();
//			}
//		});
        CustomSharedPreferences.setPreferences(Constants.PREF_USERNAME,
                "");
        CustomSharedPreferences.setPreferences(Constants.PREF_PASSWORD,
                "");

        // Setting
        CustomSharedPreferences.setPreferences(
                Constants.PREF_SETTING_SOUND, false);
        CustomSharedPreferences.setPreferences(
                Constants.PREF_SETTING_LINK_FACEBOOK, false);
        CustomSharedPreferences.setPreferences(
                Constants.PREF_SETTING_SOUND_LEVEL, 0);
        Intent loginChoiceIntent = new Intent(BaseActivity.this,
                LoginChoiceScreen.class);
        loginChoiceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        RunningRaceApplication.getInstance().setCurrentUser(null);
        startActivity(loginChoiceIntent);
        mShoes = null;
        mHistory = null;
        mFriends = null;
        AddRaceActivity.setRace(null);
        dialog.dismiss();
        finish();

	}

	/**
	 * Left menu
	 * */
	private void setupLeftMenu() {

		// Get screen width
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;

		// Setup left menu
		mMenu = new SlidingMenu(this);
		mMenu.setMode(SlidingMenu.LEFT);
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mMenu.setBehindWidth((int) ((width * 3) / 4));
		mMenu.setFadeDegree(0.35f);
		mMenu.setMenu(R.layout.layout_menu);
		ListView menuListview = (ListView) mMenu
				.findViewById(R.id.menu_listview);

		// Setup menu item
		List<MenuModel> menus = new ArrayList<MenuModel>();
		MenuModel menu = null;

        // Chat
        menu = new MenuModel();
        menu.setDislayText(getString(R.string.menu_chat));
        menus.add(menu);

		// Your history
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_your_history));
		menus.add(menu);

		// Runner you may know
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_runner_you_may_know));
		menus.add(menu);

		// Your community
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_your_community));
		menus.add(menu);

		// Profile update
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_profile_update));
		menus.add(menu);

		// Contact Us
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_contact_us));
		menus.add(menu);

		// Share on FB
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_share_fb));
		menu.setImage(R.drawable.ic_fb);
		menus.add(menu);

		// Share on Twitter
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_share_twitter));
		menu.setImage(R.drawable.ic_twitter);
		menus.add(menu);

		// Running Race History website
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_running_website));
		menus.add(menu);

		// Sign Out
		menu = new MenuModel();
		menu.setDislayText(getString(R.string.menu_sign_out));
		menus.add(menu);

		LeftMenuAdapter menuAdapter = new LeftMenuAdapter(BaseActivity.this,
				menus);
		menuListview.setAdapter(menuAdapter);
		menuListview.setOnItemClickListener(this);
	}

	/**
	 * Display setting screen
	 * */
	private void doSetting() {

		Dialog dialog = new Dialog(BaseActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_setting);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);

		// Initiation views
		int soundLv = CustomSharedPreferences.getPreferences(
				Constants.PREF_SETTING_SOUND_LEVEL, 0);
		boolean isSoundOn = CustomSharedPreferences.getPreferences(
				Constants.PREF_SETTING_SOUND, false);
		boolean isLinkFb = CustomSharedPreferences.getPreferences(
				Constants.PREF_SETTING_LINK_FACEBOOK, false);
		final CheckBox soundCb = (CheckBox) dialog
				.findViewById(R.id.setting_sound_rb);
		final CheckBox facebookCb = (CheckBox) dialog
				.findViewById(R.id.setting_link_fb_rb);
		final SeekBar soundSeekBar = (SeekBar) dialog
				.findViewById(R.id.setting_sound_seek_bar);
		mAboutLayout = (RelativeLayout) dialog
				.findViewById(R.id.setting_about_layout);
		soundCb.setChecked(isSoundOn);
		facebookCb.setChecked(isLinkFb);
		soundSeekBar.setProgress(soundLv);

		// Set max value for seekbar
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		soundSeekBar.setMax(maxVol);

		TextView aboutBtn = (TextView) dialog.findViewById(R.id.setting_about);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub

				CustomSharedPreferences.setPreferences(
						Constants.PREF_SETTING_SOUND, soundCb.isChecked());
				CustomSharedPreferences.setPreferences(
						Constants.PREF_SETTING_LINK_FACEBOOK,
						facebookCb.isChecked());
				CustomSharedPreferences.setPreferences(
						Constants.PREF_SETTING_SOUND_LEVEL,
						soundSeekBar.getProgress());
			}
		});

		aboutBtn.setOnClickListener(this);
		dialog.show();
	}

    protected String serializeObject(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

}
