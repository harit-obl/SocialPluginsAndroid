package com.example.googlepluslibrary;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnPeopleLoadedListener;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

public class OBLGooglePlusQuery implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		OnPeopleLoadedListener {

	OBLGooglePlusUser userprofile = new OBLGooglePlusUser();
	OBLLog obllog =  new OBLLog();
	List<OBLGooglePlusFriend> arrayOfGPFriends = new ArrayList<OBLGooglePlusFriend>();
	OBLGooglePlusLogin oblpluslogin;
	OBLGooglePlusQueryInterface gpInterface;
	Person person;
	OBLGooglePlusFriend oblgpfriend;
	Activity activity;
	Context context;
	PlusClient plusClient;
	String clientId;
	String[] scopes = new String[] { Scopes.PLUS_ME,
			Scopes.PLUS_LOGIN };
	String[] actions = new String[] {
			"http://schemas.google.com/AddActivity",
			"http://schemas.google.com/BuyActivity" };
	String accountName="";
	int gender_value;
	int temp = 0;
	public OBLGooglePlusQuery(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		gpInterface = (OBLGooglePlusQueryInterface) activity;
		oblpluslogin = new OBLGooglePlusLogin(context, activity);
	}

	public void createPlusClient(Context mcontext, Activity mactivity) {
		context = mcontext;
		activity = mactivity;
		if (plusClient == null) {
			plusClient = new PlusClient.Builder(context, this, this)
					.setActions(actions).setScopes(scopes).build();
			plusClient.connect();
		}
	}

	public void fetchUserProfile() {
		{
			temp = 1;
			plusClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		person = plusClient.getCurrentPerson();
		if (temp == 1) {
			userprofile.setFirstName(person.getName().getGivenName());
			userprofile.setMiddlename(person.getName().getMiddleName());
			userprofile.setLastName(person.getName().getFamilyName());
			userprofile.setProfileName(person.getDisplayName());
			userprofile.setBirthdate(person.getBirthday());
			userprofile.setCurrentLocation(person.getCurrentLocation());
			gender_value = person.getGender();
			userprofile.setGender(gender_value == 0 ? "Male" : "Female");
			userprofile.setEmail(plusClient.getAccountName());
			accountName = userprofile.getFirstName();
			gpInterface.userInfoReceived(userprofile);
			temp = 0;
		}
		if (temp == 2) {
			plusClient.loadVisiblePeople(this, null);
			temp = 0;
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPeopleLoaded(ConnectionResult con, PersonBuffer buffer,
			String arg2) {
		int count = buffer.getCount();
		String gender = "";
		for (int i = 0; i < count; i++) {
			if (buffer.get(i).getGender() == 0) {
				gender = "Male";
			}
			if (buffer.get(i).getGender() == 1) {
				gender = "Female";
			}
			arrayOfGPFriends.add(new OBLGooglePlusFriend(buffer.get(i).getId(),
					buffer.get(i).getDisplayName(),
					buffer.get(i).getBirthday(), gender, buffer.get(i)
							.getImage().getUrl()));
		}
		gpInterface.friendsInfoReceived(arrayOfGPFriends);
	}

	public void allFriends() {
		temp = 2;
		plusClient.connect();
	}
}
