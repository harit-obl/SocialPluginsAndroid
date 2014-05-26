package com.example.facebookdemo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.objectlounge.facebooklibrary.OBLError;
import com.objectlounge.facebooklibrary.OBLFacebookFriend;
import com.objectlounge.facebooklibrary.OBLFacebookLogin;
import com.objectlounge.facebooklibrary.OBLFacebookLoginInterface;
import com.objectlounge.facebooklibrary.OBLFacebookPermission;
import com.objectlounge.facebooklibrary.OBLFacebookPost;
import com.objectlounge.facebooklibrary.OBLFacebookPostInterface;
import com.objectlounge.facebooklibrary.OBLFacebookQueryInterface;
import com.objectlounge.facebooklibrary.OBLFacebookQuey;
import com.objectlounge.facebooklibrary.OBLFacebookUser;
import com.objectlounge.facebooklibrary.OBLLog;

public class DemoActivity extends Activity implements
		OBLFacebookLoginInterface, OnClickListener, OBLFacebookPostInterface,
		OBLFacebookQueryInterface {

	Button login, logout, post, user, friend;
	OBLFacebookLogin objlogin;
	OBLLog objlog;
	OBLFacebookPost objpost;
	OBLFacebookQuey objquery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		
		PackageInfo info;
		try {

		    info = getPackageManager().getPackageInfo(
		        "com.example.facebookdemo", PackageManager.GET_SIGNATURES);

		    for (Signature signature : info.signatures) {
		        MessageDigest md;
		        md = MessageDigest.getInstance("SHA");
		        md.update(signature.toByteArray());
		        String something = new String(Base64.encode(md.digest(), 0));
		        Log.e("Hash key", something);
		        System.out.println("Hash key" + something);
		    }

		} catch (NameNotFoundException e1) {
		    Log.e("name not found", e1.toString());
		} catch (NoSuchAlgorithmException e) {
		    Log.e("no such an algorithm", e.toString());
		} catch (Exception e) {
		    Log.e("exception", e.toString());
		}

		login = (Button) findViewById(R.id.btn_login);
		logout = (Button) findViewById(R.id.btn_logout);
		post = (Button) findViewById(R.id.btn_post);
		user = (Button) findViewById(R.id.btn_user);
		friend = (Button) findViewById(R.id.btn_friend);
		login.setOnClickListener(this);
		logout.setOnClickListener(this);
		post.setOnClickListener(this);
		user.setOnClickListener(this);
		friend.setOnClickListener(this);

		

		//Initialize the session and check whether the user is logged in or logged out and update the view accordingly.
		objlogin = new OBLFacebookLogin(this, this);
		objlogin.initSession(savedInstanceState);
		objlogin.setLoginBehaviour(OBLFacebookLogin.NATIVE_WEBVIEW);

		
		objpost = new OBLFacebookPost(this, this);
		objquery = new OBLFacebookQuey(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		objlogin.ActivtyResult(requestCode, resultCode, data);
	}

	
	//When the user is loging in or loging out this method is called to notify whether the user is logged in or logged out. 
	// Error is displayed if occurs during this process.
	@Override
	public void loginResult(boolean result,OBLError error) {
		// TODO Auto-generated method stub
		if (result == true) {
			Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
			login.setVisibility(View.GONE);
			logout.setVisibility(View.VISIBLE);
		} else if (result == false) {
			Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
			login.setVisibility(View.VISIBLE);
			logout.setVisibility(View.GONE);
		}
		if (error!=null)
		{
			Log.i("Error","Name: "+error.getName()+" Message: "+error.getMessage()+" Description: "+error.getDescription());
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.btn_login) {
			//To login without permission call login() method of OBLFacebookLogin class.
			//objlogin.login();
			
			/*To login with permission call loginWithPermission(String[] mandate permissions,String[] optional permissions) passing the permissions needed.
			*Use OBLFacebookPermission class for permissions.
			*Mandate Permission: Those permission which are necessary to use the app and only allow user to login if this permission are given.
			*Optional Permission : If user rejects this permissions then also allow user to login into the app. Optional permission are not necessary or not important.
			**/
			objlogin.loginWithPermission(new String[]{OBLFacebookPermission.EMAIL,OBLFacebookPermission.USER_BIRTHDAY},null);
			
		} else if (arg0.getId() == R.id.btn_logout) {
			//To logout call logout() method of OBLFacebookLogin class to log out the user.
			objlogin.logout();
		} else if (arg0.getId() == R.id.btn_post) {
			// For posting with only message call post(String _status) method of OBLFacebookPost class. 
			// objpost.post("Post Message");
			
			// For Posting with message, title, description, image url and website url use postsStatusWithDetailsDescription() method of OBLFacebookPost class.
			objpost.postsStatusWithDetailsDescription(
					"Post Message",//Post Message
					"Post Title",//Post Title
					"Post Description",//Post Description
					"http://ridesharebuddy.com/ride_images/icon200.png",//Image Url
					"http://www.objectlounge.com");//Website Url
		
		} else if (arg0.getId() == R.id.btn_user) {
			
			//Call fetchUserProfile() of OBLFacebookQuey class to get the user's detail.
			objquery.fetchUserProfile();
			
		} else if (arg0.getId() == R.id.btn_friend) {
			//Call allFriends() of OBLFacebookQuey class to get the friends's detail
			objquery.allFriends();
		
		}

	}

	
	//Checks if the post was successful or not. Error is displayed if it occurs while posting.
	@Override
	public void postingCompleted(boolean posted,OBLError error) {
		// TODO Auto-generated method stub
		if (posted) {
			Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG).show();
		}
		if (error!=null)
		{
			Log.i("Error","Name: "+error.getName()+" Message: "+error.getMessage()+" Description: "+error.getDescription());
		}                                                                                                                                                                                                                                                                                                   
	}

	
	// When the user details are requested, userInfoReceived() method is called and the user object containing the user's details is passed.
	@Override
	public void userInfoReceived(OBLFacebookUser user,OBLError error) {
		// TODO Auto-generated method stub
		if (user!=null)
		{
		Log.e("First Name:", user.getFirstName());
		Log.e("Middle Name:", user.getMiddlename());
		Log.e("Last Name:", user.getLastName());
		Log.e("Social Id:", user.getsocialMediaId());
		Log.e("Birthdate:", user.getBirthdate());
		Log.e("Gender:", user.getGender());
		Log.e("Location", user.getCurrentLocation());
		Log.e("Email:", user.getEmail());
		Log.e("Profile Name:", user.getProfileName());
		Log.e("HomeTown:", user.getHomeTown());
		}
		if (error!=null)
		{
			Log.e("Error",error.getMessage());
		}
	}

	// When the friends details are requested, friendsInfoReceived() method is called and the friends list containing the friend's details is passed.
	@Override
	public void friendsInfoReceived(List<OBLFacebookFriend> friends,OBLError error) {
		// TODO Auto-generated method stub
		Log.e("Friends Info","Received");
		Log.e("Friends Details",friends.toString());
		if (friends!=null)
		{
		for (int i = 0; i < friends.size(); i++) {
			Log.e("Friends Details", "ID: " + friends.get(i).getsocialMediaId()
					+ " Name: " + friends.get(i).getname() + " Gender: "
					+ friends.get(i).getGender());
		}
		}
		if (error !=null)
		{
			Log.e("Error",error.getMessage());
		}
	}

}
