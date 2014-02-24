package com.example.googleplusdemo;

import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.googlepluslibrary.OBLGooglePlusFriend;
import com.example.googlepluslibrary.OBLGooglePlusQuery;
import com.example.googlepluslibrary.OBLGooglePlusLogin;
import com.example.googlepluslibrary.OBLGooglePlusLoginInterface;
import com.example.googlepluslibrary.OBLGooglePlusQueryInterface;
import com.example.googlepluslibrary.OBLGooglePlusShare;
import com.example.googlepluslibrary.OBLGooglePlusShareInterface;
import com.example.googlepluslibrary.OBLGooglePlusUser;
import com.example.googlepluslibrary.OBLLog;

public class OtherActivity extends Activity implements OnClickListener,
		OBLGooglePlusLoginInterface, OBLGooglePlusShareInterface,
		OBLGooglePlusQueryInterface {

	Button signout_btn, share_btn, userprofile_btn, friendprofile_btn;
	OBLGooglePlusLogin plusLogin = new OBLGooglePlusLogin(this, this);
	OBLGooglePlusShare plusShare = new OBLGooglePlusShare(this, this);
	OBLGooglePlusQuery plusQuery = new OBLGooglePlusQuery(this, this);
	OBLLog obllog = new OBLLog();

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.other_activity);
		plusLogin.loginUsingInstalledApp(this, this);
		plusQuery.createPlusClient(getApplicationContext(), this);
		signout_btn = (Button) findViewById(R.id.signout_button);
		share_btn = (Button) findViewById(R.id.share_button);
		userprofile_btn = (Button) findViewById(R.id.userProfile_button);
		friendprofile_btn = (Button) findViewById(R.id.friendsprofile_button);
		signout_btn.setOnClickListener(this);
		share_btn.setOnClickListener(this);
		userprofile_btn.setOnClickListener(this);
		friendprofile_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.signout_button) {
			plusLogin.logout();
		}
		if (v.getId() == R.id.share_button) {
			String title = "Fantasy Crciket";
			String description = "Build your own team, join public or private league and try to win any tournament";
			String status = "I am just creating FantasyCricket app." + " "
					+ description;
			String imageUri = "http://ridesharebuddy.com/ride_images/icon200.png";
			plusShare.postsStatusWithDetailsDescription(status, title,
					description, imageUri);
		}
		if(v.getId() == R.id.userProfile_button)
		{
			plusQuery.fetchUserProfile();
		}
		if(v.getId() == R.id.friendsprofile_button)
		{
			plusQuery.allFriends();
		}
	}

	@Override
	public void sharingCompleted(boolean shared) {

		if (shared == true) {
			obllog.logMessage("Shared Successfully");
		} else {
			obllog.logMessage("Unable to Share");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		plusShare.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void userInfoReceived(OBLGooglePlusUser user) {
		obllog.logMessage(user.getFirstName() + " " + user.getMiddlename()
				+ " " + user.getLastName() + " " + user.getProfileName() + " "
				+ user.getBirthdate() + " " + user.getCurrentLocation() + " "
				+ user.getGender() + " " + user.getEmail());
	}

	@Override
	public void friendsInfoReceived(List<OBLGooglePlusFriend> oblgpuser) {
		// TODO Auto-generated method stub
		obllog.logMessage("Plus Query Method is called");
		for (int i = 0; i < oblgpuser.size(); i++) {
			obllog.logMessage(oblgpuser.get(i).getSocialMediaId() + " "
					+ oblgpuser.get(i).getname() + " ");
		}
	}

	@Override
	public void googlePlusLoginCompleted(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void googlePlusLogoutCompleted(boolean value) {

		if (value == true) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			obllog.logMessage("Unable to Logout");
		}

	}
}
