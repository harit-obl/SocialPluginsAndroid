package com.objectlounge.facebooklibrary;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class OBLFacebookQuey {
	
	public static OBLFacebookUser userprofile;
	public Activity activity;
	public static Session session;
	OBLFacebookQueryInterface fbqinterface;
	OBLLog objlog;
	OBLError error;

	public OBLFacebookQuey(Activity _activity) {
		// TODO Auto-generated constructor stub
		activity = _activity;
		//Check whether the calling activity has implemented the OBLFacebookQueryInterface interface.
		fbqinterface = (OBLFacebookQueryInterface) activity; 
		objlog = new OBLLog();
		error=new OBLError();
	}

	
	//Fetches The User's Profile Details And Passes it to the The Calling Activity Through OBLFacebookQueryInterface userInfoReceived() method.
	//If The Permission is not granted it will set "No Permission" to that field. And if the user has not set any field then "Not Available" will be 
	//displayed.
	public void fetchUserProfile() {

		session = Session.getActiveSession();
		if (session.isOpened()) {
			Request request = Request.newMeRequest(session,
					new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							// TODO Auto-generated method stub
							if (response.getError() != null) {
								Log.e("Query Error",response.getError().toString());
								objlog.logMessage("ERROR: "
										+ response.getError().toString());
								error.setName(response.getError().getCategory().toString());
								error.setMessage(response.getError().getErrorMessage());
								fbqinterface.userInfoReceived(null, error);

							} else {
								List<String> permissions = new ArrayList<String>();
								permissions = session.getPermissions();
								userprofile = new OBLFacebookUser();

								// if (permissions.contains("public_profile")) {
							
								if (user.getId() == null) {
									userprofile
											.setsocialMediaId("Not Available");
								} else {
									userprofile.setsocialMediaId(user.getId());
								}
								if (user.getName() == null) {
									userprofile.setname("Not Available");
								} else {
									userprofile.setname(user.getName());
								}
								if (user.getFirstName() == null) {
									userprofile.setFirstName("Not Available");
								} else {
									userprofile.setFirstName(user
											.getFirstName());
								}
								if (user.getMiddleName() == null) {
									userprofile.setMiddlename("Not Available");
								} else {
									userprofile.setMiddlename(user
											.getMiddleName());
								}
								if (user.getLastName() == null) {
									userprofile.setLastName("Not Available");
								} else {
									userprofile.setLastName(user.getLastName());
								}
								if (user.getProperty("gender") == null) {
									userprofile.setGender("Not Available");
								} else {
									userprofile.setGender(user.getProperty(
											"gender").toString());
								}
								if (user.getUsername() == null) {
									userprofile.setProfileName("Not Available");
								} else {
									userprofile.setProfileName(user
											.getUsername());
								}

								if (permissions.contains("user_birthday")) {
									if (user.getBirthday() == null) {
										userprofile
												.setBirthdate("Not Available");
									} else {
										userprofile.setBirthdate(user
												.getBirthday());
									}
								} else {
									userprofile.setBirthdate("No Permission");
								}

								if (permissions.contains("email")) {
									if (user.getProperty("email") == null) {
										userprofile.setEmail("Not Available");
									} else {
										userprofile.setEmail(user.getProperty(
												"email").toString());
									}
								} else {
									userprofile.setEmail("No Permssion");
								}

								if (permissions.contains("user_location")) {

									if (user.getLocation() == null) {
										userprofile
										.setCurrentLocation("Not Available");
									} else {
										if (user.getLocation().getProperty(
												"name") == null) {
											userprofile
													.setCurrentLocation("Not Available");
										} else {
											userprofile.setCurrentLocation(user
													.getLocation()
													.getProperty("name")
													.toString());
										}
									}
								} else {
									userprofile
											.setCurrentLocation("No Permission");
								}

								if (permissions.contains("user_hometown")) {
									if (user.getProperty("hometown") == null) {
										userprofile
												.setHomeTown("Not Available");
									} else {

										try {

											JSONObject obj1 = new JSONObject(
													user.getProperty("hometown")
															.toString());

											userprofile.setHomeTown(obj1
													.getString("name"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								} else {
									userprofile.setHomeTown("No Permission");
								}
								fbqinterface.userInfoReceived(userprofile,null);
							}
//							if (user.getLocation()!=null)
//								Log.e("User Country",user.getLocation().toString());
						}

					});
			request.executeAsync();

		}
		else
		{
			objlog.logMessage("You are currently logged out.");
			error.setName("Logged Out");
			error.setMessage("You are currently logged out.");
			error.setDescription("Please Login Again To See User Details");
			fbqinterface.userInfoReceived(null, error);
		}

	}

	//Fetches the User's Friends Detail.
	public void allFriends() {
		
		session = Session.getActiveSession();
		final List<OBLFacebookFriend> friendlist = new ArrayList<OBLFacebookFriend>();
		
		if (session.isOpened()) {
			Request request = Request.newMyFriendsRequest(session,
					new Request.GraphUserListCallback() {
						
						@Override
						public void onCompleted(List<GraphUser> users,
								Response response) {
							// TODO Auto-generated method stub
							
							if (response.getError() != null) {
								objlog.logMessage("ERROR: "
										+ response.getError().toString());
								error.setName(response.getError().getCategory().toString());
								error.setMessage(response.getError().getErrorMessage());
								fbqinterface.userInfoReceived(null, error);
							} else {
								for (int cnt = 0; cnt < users.size(); cnt++) {
									friendlist.add(new OBLFacebookFriend(users
											.get(cnt).getId(), users.get(cnt)
											.getName(),users.get(cnt).getProperty("gender").toString()));
								}
								fbqinterface.friendsInfoReceived(friendlist,null);
								
							}
						}

					});
			Bundle bundle=new Bundle();
			bundle.putString("fields","gender,name,id");
			request.setParameters(bundle);
			request.executeAsync();

		}
		else	//If Session is Closed
		{
			objlog.logMessage("You are currently logged out.");
			error.setName("Logged Out");
			error.setMessage("You are currently logged out.");
			error.setDescription("Please Login Again To See Friends Details");
			fbqinterface.friendsInfoReceived(null, error);
		}
	}

	public static OBLFacebookFriend fetchFriendsProfile(
			ArrayList<String> facebookIds) {
		return null;
	}
}
