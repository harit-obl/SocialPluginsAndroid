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
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;

public class OBLFacebookLogin extends OBLLogin {

	private static List<String> ReadPermission, PublishPermission,
			OldPermission, NewPermission;
	private Session session;
	private Context context;
	private Activity activity;
	private static SessionLoginBehavior loginBehaviour = SessionLoginBehavior.SSO_WITH_FALLBACK;
	public static final String ONLY_NATIVE = "native";
	public static final String ONLY_WEBVIEW = "webview";
	public static final String NATIVE_WEBVIEW = "both";
	private static final String PUBLISH_PERMISSION_PREFIX = "publish";
	private static final String MANAGE_PERMISSION_PREFIX = "manage";
	public static final int REQUEST_CODE_LOGIN = 199188;
	public static final int REQUEST_CODE_LOGIN_READ = 199187;
	public static final int REQUEST_CODE_LOGIN_PUBLISH = 199189;
	OBLLog obllog;
	OBLFacebookLoginInterface inter;
	OBLFacebookPost oblpost;
	OBLError error;
	public static boolean postcheck = false; // Used when login is required
												// while posting.
	public static int posttype = 0; // Check whether the user has called for
									// loging from post() or
									// postsStatusWithDetailsDescription().

	public OBLFacebookLogin(Context _context, Activity _activity) {
		this.context = _context;
		this.activity = _activity;
		obllog = new OBLLog();
		error = new OBLError();
		
		// Check if the activity has implemented the OBLFacebookLoginInterface
		// interface.
		try {
			inter = (OBLFacebookLoginInterface) activity;
		} catch (ClassCastException e) {
			obllog.logMessage(activity.getLocalClassName()
					+ " must implement OBLFacebookLoginInterface for getting login result");
			activity.finish();
		}
	}

	// Initialize (Start) Session.
	// This method restores the session from previous stored state if available
	// or creates a new instance of session.
	// Call this method on start to check whether the user is logged in or
	// logged out.
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

	// This method gets the user logged in without any permission(basic
	// permissions which are default will be asked).
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

	// Handles the result of loging process and updates the session according to
	// the result.
	// @param requestCode= The requestCode parameter from the forwarded call
	// @param resultCode= An int containing the resultCode parameter from the
	// forwarded call.
	// @param data= The Intent passed as the data parameter from the forwarded
	// call.
	// @return= A boolean indicating whether the requestCode matched a pending
	// authorization request for this Session.
	public boolean ActivtyResult(int requestCode, int resultCode, Intent data) {
		Session.getActiveSession().onActivityResult(activity, requestCode,
				resultCode, data);

		session = Session.getActiveSession();

		if (resultCode == Activity.RESULT_OK && session.isOpened()) {
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
				} else {
					if (isPostCheck()) {
						oblpost = new OBLFacebookPost(context, activity);
						NewPermission = session.getPermissions();
						if (NewPermission
								.contains(OBLFacebookPermission.PUBLISH_ACTIONS)) {

							if (posttype == 1) {
								oblpost.post(null);
							} else if (posttype == 2) {
								oblpost.postsStatusWithDetailsDescription(null,
										null, null, null, null);
							}
						} else {
							obllog.logMessage("Error: Publish Permission Not Granted");
							error.setName("Permission Missing");
							error.setMessage("Publish Permission Is Not Granted");
							error.setDescription("");
							oblpost.fbpostinterface.postingCompleted(false,
									error);
						}
						setPostCheck(false);
					}
				}

			}

		}

		// Check When the publish permission are requested, whether the user accepted or cancelled the publish permission 
		if (requestCode == REQUEST_CODE_LOGIN_PUBLISH
				&& resultCode == Activity.RESULT_OK) {
			// 
			if (isPostCheck()) {
				oblpost = new OBLFacebookPost(context, activity);
				NewPermission = session.getPermissions();
				if (NewPermission
						.contains(OBLFacebookPermission.PUBLISH_ACTIONS)) {

					if (posttype == 1) {
						oblpost.post(null);
					} else if (posttype == 2) {
						oblpost.postsStatusWithDetailsDescription(null, null,
								null, null, null);
					}
				} else {
					obllog.logMessage("Error: Publish Permission Not Granted");
					error.setName("Permission Missing");
					error.setMessage("Publish Permission Is Not Granted");
					error.setDescription("");
					oblpost.fbpostinterface.postingCompleted(false, error);
				}
				setPostCheck(false);
			}
		}

		// If the user cancels the loging process or it gets cancelled due to some another reason.
		if (resultCode == Activity.RESULT_CANCELED ) {
			obllog.logMessage("Login Aborted");
			error.setName("Login Aborted");
			error.setMessage("Login Process Cancelled");
			error.setDescription("");
			if (session.isOpened())
			inter.loginResult(true, error);
			else
			inter.loginResult(false, error);
		}

		if (session.isOpened()) {
			return true;
		} else {
			return false;
		}
	}

	// Call this method to log out of user's account.
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

	// This method is used whenever the state of session changes and it checks
	// whether the session is opened or closed. It passes the result to the
	// user using the loginResult() method of OBLFacebookLoginInterface
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			obllog.logMessage("Logged In...");
			inter.loginResult(true, null);
		} else if ((state.isClosed() || state != SessionState.OPENED)
				&& state != SessionState.CREATED_TOKEN_LOADED) {
			obllog.logMessage("Logged Out...");
			inter.loginResult(false, null);
		}
	}

	// Check if the permission is a Read Permission or a Publish Permission.
	// @permission= permission to check for.
	public static boolean isPublishPermission(String permission) {
		return permission != null
				&& (permission.startsWith(PUBLISH_PERMISSION_PREFIX) || permission
						.startsWith(MANAGE_PERMISSION_PREFIX));

	}

	// This method is used to log in the user with permissions specified. Read
	// and Publish permission can be given together.
	// It will first login with only Read Permission and then request for
	// publish permission.
	// If now permissions are passed then it will call the login() method.
	// @params permissions= Array of permissions which are needed when loging.
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
						|| session.getState() == SessionState.CLOSED_LOGIN_FAILED || session.getState() == SessionState.CLOSED ) {
					session = new Session(context);
				}
				if (!session.isOpened()) {
					//Cannot Login with only publish permission. Ask For basic read Permission first and then login with publish permission.
					NewPermission.add(OBLFacebookPermission.BASIC_INFO);
					String[] temppermissions=new String[NewPermission.size()];
					for (int i=0;i<NewPermission.size();i++)
					{
						temppermissions[i]=NewPermission.get(i);
					}
					setPostCheck(true);
					setPosttype(0);
					loginWithPermission(temppermissions);
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

	// Get the Login Behaviour.
	public SessionLoginBehavior getLoginBehaviour() {
		return loginBehaviour;
	}

	// Set the Login Behaviour.
	// @params logintype= ONLY_NATIVE will do loging using the native app only.
	// If the native app is not available then it will not proceed.
	// ONLY_WEBVIEW will do loging using the Webview only. If the native app is
	// available then also it will use webview.
	// NATIVE_WEBVIEW will do loging using native app if available else it will
	// use Webview
	public void setLoginBehaviour(String logintype) {
		if (logintype == ONLY_NATIVE)
			OBLFacebookLogin.loginBehaviour = SessionLoginBehavior.SSO_ONLY;
		else if (logintype == ONLY_WEBVIEW)
			OBLFacebookLogin.loginBehaviour = SessionLoginBehavior.SUPPRESS_SSO;
		else if (logintype == NATIVE_WEBVIEW)
			OBLFacebookLogin.loginBehaviour = SessionLoginBehavior.SSO_WITH_FALLBACK;
	}

	// Get Session Status(Open Or Closed). Returns true if session is open or
	// false if session is closed.
	public boolean getStatus() {
		session = Session.getActiveSession();
		if (session.isOpened()
				|| session.getState() == SessionState.CREATED_TOKEN_LOADED) {
			return true;
		} else {
			return false;
		}
	}

	// This method is used to check if the native app is installed on the user
	// device.
	// @params boolean= returns true if the native app is installed or false if
	// not installed.
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

	// If publish permission is not given then this method is used to request
	// publish permission when session is open.
	public void getpostpermission(int requestcode) {
		session = Session.getActiveSession();
		if (session.isOpened()) {
			session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					activity, OBLFacebookPermission.PUBLISH_ACTIONS)
					.setLoginBehavior(getLoginBehaviour()).setRequestCode(
							requestcode));
		}
		Session.setActiveSession(session);
	}
}