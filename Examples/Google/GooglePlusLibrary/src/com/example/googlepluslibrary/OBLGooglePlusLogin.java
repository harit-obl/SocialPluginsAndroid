package com.example.googlepluslibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class OBLGooglePlusLogin extends OBLLogin implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	PlusClient plusClient;
	OBLGooglePlusUser plusUser;
	ConnectionResult mConnectionResult;
	String clientId;
	String[] scopes = new String[] { Scopes.PLUS_ME,
			Scopes.PLUS_LOGIN };
	String[] actions = new String[] {
			"http://schemas.google.com/AddActivity",
			"http://schemas.google.com/BuyActivity" };
	String accountName;
	static Context context;
	Activity activity;
	OBLGooglePlusLoginInterface gplogin;
	Person person;
	OBLLog obllog = new OBLLog();

	public OBLGooglePlusLogin(Context context, Activity activity) {
		OBLGooglePlusLogin.context = context;
		this.activity = activity;
		gplogin = (OBLGooglePlusLoginInterface) activity;
	}

	public void loginUsingInstalledApp(Context mcontext, Activity activity) {

		if (plusClient == null) {
			plusClient = new PlusClient.Builder(context, this, this)
					.setActions(actions).setScopes(scopes).build();
			plusClient.connect();
		} else {
			plusClient.connect();
		}
	}

	@Override
	public void login() {

	}

	@Override
	public void onConnected(Bundle arg0) {
		accountName = plusClient.getAccountName();
		person=plusClient.getCurrentPerson();
		clientId=person.getId();
		obllog.logMessage(accountName);
		gplogin.googlePlusLoginCompleted(true);
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connection) {
		if (connection.hasResolution()) {
			try {
				connection.startResolutionForResult(activity,
						REQUEST_CODE_RESOLVE_ERR);
				Log.e("error", String.valueOf(connection.getErrorCode()));
			} catch (SendIntentException e) {
				plusClient.connect();
			}
		}
		mConnectionResult = connection;
	}

	public void onActivityResult(int requestCode, int responseCode, Intent data) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == Activity.RESULT_OK) {
			mConnectionResult = null;
			plusClient.connect();
		} else if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == Activity.RESULT_CANCELED) {
			mConnectionResult = null;
			obllog.logMessage("Login Cancelled");
		}
	}

	@Override
	public boolean logout() {
		if (plusClient.isConnected()) {
			plusClient.clearDefaultAccount();
			plusClient.disconnect();
			obllog.logMessage("LoggedOut Successfully");
			gplogin.googlePlusLogoutCompleted(true);
			return true;
		}
		return false;
	}

}