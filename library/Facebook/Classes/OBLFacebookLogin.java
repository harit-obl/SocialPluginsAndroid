package com.example.facebooklibrary;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;

public class OBLFacebookLogin extends OBLLogin {

	private static List<String> ReadPermission, PublishPermission,
			OldPermission, NewPermission;
	private Session session;
	private Context context;
	private Activity activity;
	private static SessionLoginBehavior loginBehaviour = SessionLoginBehavior.SSO_WITH_FALLBACK;
	private static SessionDefaultAudience defaultAudience = SessionDefaultAudience.EVERYONE;
	public static final String ONLY_NATIVE = "native";
	public static final String ONLY_WEBVIEW = "webview";
	public static final String NATIVE_WEBVIEW = "both";
	public static final String EVERYONE = "everyone";
	public static final String FRIENDS = "friends";
	public static final String ONLY_ME = "onlyme";
	private static final String PUBLISH_PERMISSION_PREFIX = "publish";
	private static final String MANAGE_PERMISSION_PREFIX = "manage";
	public static final int REQUEST_CODE_LOGIN = 199188;
	public static final int REQUEST_CODE_LOGIN_READ = 199187;
	public static final int REQUEST_CODE_LOGIN_PUBLISH = 199189;
	OBLLog obllog;
	OBLFacebookLoginInterface inter;
	OBLFacebookPost oblpost;
	OBLError error;
	public static boolean postcheck = false;
	public static int posttype = 0;

	public OBLFacebookLogin(Context _context, Activity _activity) {
		this.context = _context;
		this.activity = _activity;
		obllog = new OBLLog();
		error=new OBLError();
		try {
			inter = (OBLFacebookLoginInterface) activity;
		} catch (ClassCastException e) {
			obllog.logMessage(activity.getLocalClassName()
					+ " must implement OBLFacebookLoginInterface for getting login result");
			activity.finish();
		}
	}

	// Initialize (Start) Session
	public Session initSession(Bundle savedInstanceState) {

		session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(activity, null, null,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(context);
			}
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(activity));
			}
		}
		Session.setActiveSession(session);
		onSessionStateChange(session, session.getState(), null);
		return session;
	}

	@Override
	public void login() {
		session = Session.getActiveSession();
		if (getLoginBehaviour() == SessionLoginBehavior.SSO_ONLY)
			checkNativeApp();

		if (session == null
				|| session.getState() == SessionState.CLOSED_LOGIN_FAILED
				|| session.getState() == SessionState.CLOSED) {
			session = new Session(context);
		}
		if (!session.isOpened()) {
			session.openForRead(new Session.OpenRequest(activity)
					.setLoginBehavior(getLoginBehaviour()).setRequestCode(
							REQUEST_CODE_LOGIN));
		}
		Session.setActiveSession(session);
	}

	public boolean ActivtyResult(int requestCode, int resultCode, Intent data) {
		// uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(activity, requestCode,
				resultCode, data);

		session = Session.getActiveSession();

		if (resultCode == Activity.RESULT_OK && session.isOpened()) {
			// Session.setActiveSession(session);
			onSessionStateChange(session, session.getState(), null);
		}
		
		if (requestCode == REQUEST_CODE_LOGIN_READ
				&& resultCode == Activity.RESULT_OK) {
			if (PublishPermission.size() != 0) {
				OldPermission = new ArrayList<String>();
				NewPermission = new ArrayList<String>();

				// Get Already Accepted Permissions
				if (session != null) {
					OldPermission = session.getPermissions();
				}
				// Store New Needed Permissions
				for (int i = 0; i < PublishPermission.size(); i++) {
					if (!OldPermission.contains(PublishPermission.get(i))) {
						NewPermission.add(PublishPermission.get(i));
					}
				}
				if (NewPermission.size() != 0) {
					if (session == null
							|| session.getState() == SessionState.CLOSED_LOGIN_FAILED) {
						session = new Session(context);
					}
					if (!session.isOpened()) {
						session.openForPublish(new Session.OpenRequest(activity)
								.setLoginBehavior(getLoginBehaviour())
								.setPermissions(NewPermission)
								.setRequestCode(REQUEST_CODE_LOGIN_PUBLISH));
					}
					if (session.isOpened()) {
						session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
								activity, NewPermission).setLoginBehavior(
								getLoginBehaviour()).setRequestCode(
								REQUEST_CODE_LOGIN_PUBLISH));
					}
				}
				else
				{
					if (isPostCheck())
					{
						oblpost = new OBLFacebookPost(context, activity);
						NewPermission = session.getPermissions();
						if (NewPermission.contains("publish_actions")) {

							if (posttype == 1) {
								oblpost.post(null);
							} else if (posttype == 2) {
								oblpost.postsStatusWithDetailsDescription(null, null,
										null, null, null);
							}
						}  else {
							obllog.logMessage("Error: Publish Permission Not Granted");
							error.setName("Permission Missing");
							error.setMessage("Publish Permission Is Not Granted");
							error.setDescription("");
							oblpost.fbpostinterface.postingCompleted(false,error);
						}
						setPostCheck(false);
					}
				}
				
			}

		}

		if (requestCode == REQUEST_CODE_LOGIN_PUBLISH
				&& resultCode == Activity.RESULT_OK) {
			if (isPostCheck()) {
				oblpost = new OBLFacebookPost(context, activity);
				NewPermission = session.getPermissions();
				if (NewPermission.contains(OBLFacebookPermission.PUBLISH_ACTIONS)) {

					if (posttype == 1) {
						oblpost.post(null);
					} else if (posttype == 2) {
						oblpost.postsStatusWithDetailsDescription(null, null,
								null, null, null);
					}
				}  else {
					obllog.logMessage("Error: Publish Permission Not Granted");
					error.setName("Permission Missing");
					error.setMessage("Publish Permission Is Not Granted");
					error.setDescription("");
					oblpost.fbpostinterface.postingCompleted(false,error);
				}
				setPostCheck(false);
			}
		}
		

		if (resultCode == Activity.RESULT_CANCELED) {
			obllog.logMessage("Login Aborted");
			error.setName("Login Aborted");
			error.setMessage("Login Process Cancelled");
			error.setDescription("");
			inter.loginResult(false, error);
		}

		if (session.isOpened()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean logout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
			session = new Session(context);
			Session.setActiveSession(Session.getActiveSession());
			onSessionStateChange(session, session.getState(), null);
			return true;
		}
		Session.setActiveSession(session);
		onSessionStateChange(session, session.getState(), null);
		return false;
	}

	// Session Change Code
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			obllog.logMessage("Logged In...");
			inter.loginResult(true,null);
		} else if ((state.isClosed() || state != SessionState.OPENED)
				&& state != SessionState.CREATED_TOKEN_LOADED) {
			obllog.logMessage("Logged Out...");
			inter.loginResult(false,null);
		}
	}

	public static boolean isPublishPermission(String permission) {
		return permission != null
				&& (permission.startsWith(PUBLISH_PERMISSION_PREFIX) || permission
						.startsWith(MANAGE_PERMISSION_PREFIX));

	}

	public void loginWithPermission(String[] permissions) {
		session = Session.getActiveSession();
		if (getLoginBehaviour() == SessionLoginBehavior.SSO_ONLY)
			checkNativeApp();
		// If Permission is null then do login with basic permission
		if (permissions.length == 0) {
			login();
		} else {
			// Divide the Read and Publish Permissions
			ReadPermission = new ArrayList<String>();
			PublishPermission = new ArrayList<String>();

			for (int i = 0; i < permissions.length; i++) {
				if (isPublishPermission(permissions[i])) {
					PublishPermission.add(permissions[i]);
				} else {
					ReadPermission.add(permissions[i]);
				}
			}

			// Get Active Session

			if (ReadPermission.size() != 0) {
				OldPermission = new ArrayList<String>();
				NewPermission = new ArrayList<String>();

				// Get Already Accepted Permissions
				if (session != null) {
					OldPermission = session.getPermissions();
				}
				// Store New Needed Permissions
				for (int i = 0; i < ReadPermission.size(); i++) {
					if (!OldPermission.contains(ReadPermission.get(i))) {
						NewPermission.add(ReadPermission.get(i));
					}
				}

				if (session == null
						|| session.getState() == SessionState.CLOSED_LOGIN_FAILED
						|| session.getState() == SessionState.CLOSED) {
					session = new Session(context);
				}
				if (!session.isOpened()) {
					session.openForRead(new Session.OpenRequest(activity)
							.setLoginBehavior(getLoginBehaviour())
							.setPermissions(NewPermission)
							.setRequestCode(REQUEST_CODE_LOGIN_READ));
				}
				if (session.isOpened()) {
					session.requestNewReadPermissions(new Session.NewPermissionsRequest(
							activity, NewPermission).setLoginBehavior(
							getLoginBehaviour()).setRequestCode(
							REQUEST_CODE_LOGIN_READ));
				}

				Session.setActiveSession(session);
			}

			else // if Read Permission is null then login with publish
					// permission.
			{
				OldPermission = new ArrayList<String>();
				NewPermission = new ArrayList<String>();

				// Get Already Accepted Permissions
				if (session != null) {
					OldPermission = session.getPermissions();
				}
				// Store New Needed Permissions
				for (int i = 0; i < PublishPermission.size(); i++) {
					if (!OldPermission.contains(PublishPermission.get(i))) {
						NewPermission.add(PublishPermission.get(i));
					}
				}

				if (session == null
						|| session.getState() == SessionState.CLOSED_LOGIN_FAILED) {
					session = new Session(context);
				}
				if (!session.isOpened()) {
					session.openForPublish(new Session.OpenRequest(activity)
							.setLoginBehavior(getLoginBehaviour())
							.setPermissions(NewPermission)
							.setRequestCode(REQUEST_CODE_LOGIN_PUBLISH));
				}
				if (session.isOpened()) {
					session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
							activity, NewPermission).setLoginBehavior(
							getLoginBehaviour()).setRequestCode(
							REQUEST_CODE_LOGIN_PUBLISH));
				}
				Session.setActiveSession(session);
			}
		}

	}

	public SessionLoginBehavior getLoginBehaviour() {
		return loginBehaviour;
	}

	public void setLoginBehaviour(String logintype) {
		if (logintype == ONLY_NATIVE)
			OBLFacebookLogin.loginBehaviour = SessionLoginBehavior.SSO_ONLY;
		else if (logintype == ONLY_WEBVIEW)
			OBLFacebookLogin.loginBehaviour = SessionLoginBehavior.SUPPRESS_SSO;
		else if (logintype == NATIVE_WEBVIEW)
			OBLFacebookLogin.loginBehaviour = SessionLoginBehavior.SSO_WITH_FALLBACK;
	}

	public SessionDefaultAudience getDefaultAudience() {
		return defaultAudience;
	}

	public void setDefaultAudience(String defaultaudience) {
		if (defaultaudience == EVERYONE)
			OBLFacebookLogin.defaultAudience = SessionDefaultAudience.EVERYONE;
		else if (defaultaudience == FRIENDS)
			OBLFacebookLogin.defaultAudience = SessionDefaultAudience.FRIENDS;
		else if (defaultaudience == ONLY_ME)
			OBLFacebookLogin.defaultAudience = SessionDefaultAudience.ONLY_ME;
	}

	// Get Session Status(Open Or Closed)
	public boolean getStatus() {
		session = Session.getActiveSession();
		if (session.isOpened()
				|| session.getState() == SessionState.CREATED_TOKEN_LOADED) {
			return true;
		} else {
			return false;
		}
	}

	

	public boolean checkNativeApp() {
		final PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals("com.facebook.katana")
					|| packageInfo.packageName.equals("com.facebook.android")) {
				return true;
			}
		}
		obllog.logMessage("Native App Not Installed");
		error.setName("Native App Missing");
		error.setMessage("Facebook Native App is Missing.");
		error.setDescription("");
		inter.loginResult(false, error);
		return false;
	}

	public static boolean isPostCheck() {
		return postcheck;
	}

	public static void setPostCheck(boolean check) {
		OBLFacebookLogin.postcheck = check;
	}

	public static int getPosttype() {
		return posttype;
	}

	public static void setPosttype(int posttype) {
		OBLFacebookLogin.posttype = posttype;
	}

	public void getpostpermission(int requestcode) {
		session = Session.getActiveSession();
		if (session.isOpened()) {
			session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					activity,OBLFacebookPermission.PUBLISH_ACTIONS).setLoginBehavior(
					getLoginBehaviour()).setRequestCode(requestcode));
		}
		Session.setActiveSession(session);
	}
}