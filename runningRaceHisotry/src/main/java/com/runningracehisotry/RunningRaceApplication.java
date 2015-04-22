/**
 * 
 */
package com.runningracehisotry;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.runningracehisotry.models.User;

import android.app.Application;

/**
 * @author nvhaiwork
 *
 */
@ReportsCrashes(formKey = "", mailTo = "nvhai.work.vn@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL,
		ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class RunningRaceApplication extends Application {
    private User currentUser;
    private boolean isSocialLogin;
    private static RunningRaceApplication mInstance;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ACRA.init(this);
		Parse.initialize(this, getString(R.string.parse_app_id),
				getString(R.string.parse_client_key));
		ParseFacebookUtils.initialize(getString(R.string.fb_app_id));
		ParseTwitterUtils.initialize("5jjxXbxvr2p0DzHlUEADCDI0Z",
				"Pt9xNAShOGjKi8XnjSUUJ4P2w0H9oxDXHBCpQEknoVMnWUK6Bq");
		ParseInstallation.getCurrentInstallation().saveInBackground();

        mInstance = this;
	}
    public static RunningRaceApplication getInstance() {
        return mInstance;
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isSocialLogin() {
        return isSocialLogin;
    }

    public void setSocialLogin(boolean isSocialLogin) {
        this.isSocialLogin = isSocialLogin;
    }
}
