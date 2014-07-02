package com.objectlounge.facebooklibrary;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;

public class OBLFacebookLogin extends OBLLogin {

	private static List<String> ReadPermission, PublishPermission,
			OldPermission, NewPermission;
	private Session session;
	private Context context;
	private Activity activity;
	// Login Behavior
	private static SessionLoginBehavior loginBehaviour = SessionLoginBehavior.SSO_WITH_FALLBACK;
	// Login Behaviour Constants
	public static final String ONLY_NATIVE = "native";
	public static final String ONLY_WEBVIEW = "webview";
	public static final String NATIVE_WEBVIEW = "both";
	// Prefix Constants
	private static final String PUBLISH_PERMISSION_PREFIX = "publish";
	private static final String MANAGE_PERMISSION_PREFIX = "manage";
	// Request Id Constants
	public static final int REQUEST_CODE_LOGIN = 19188;
	public static final int REQUEST_CODE_LOGIN_READ = 19187;
	public static final int REQUEST_CODE_LOGIN_PUBLISH = 19189;
	// Object Of Classes
	OBLLog obllog;
	OBLFacebookLoginInterface inter;
	OBLFacebookPost oblpost;
	OBLError error;
	// Check Variables
	public static boolean postcheck = false; // Used when login is required
												// while posting.
	public static int posttype = 0; // Check whether the user has called for
									// loging from post() or
									// postsStatusWithDetailsDescription().
	public boolean nativeAppAvail;
	private static ArrayList<String> mandate_Permission = new ArrayList<String>();
	private static ArrayList<String> optional_Permission = new ArrayList<String>();

	public OBLFacebookLogin(Context _context, Activity _activity) {
		this.context = _context;
		this.activity = _activity;
		obllog = new OBLLog();
		error = new OBLError();
		session = new Session(context);
		session = Session.getActiveSession();

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
		nativeAppAvail = true;
		// Check Native App is installed if login behavior is set to SSO only.
		if (getLoginBehaviour() == SessionLoginBehavior.SSO_ONLY)
			nativeAppAvail = checkNativeApp();

		if (nativeAppAvail) {
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
	}

	// Handles the result of login process and updates the session according to
	// the result.
	// @param requestCode= The requestCode parameter from the forwarded call
	// @param resultCode= An int containing the resultCode parameter from the
	// forwarded call.
	// @param data= The Intent passed as the data parameter from the forwarded
	// call.
	// @return= A boolean indicating whether the requestCode matched a pending
	// authorization request for this Session.
	public boolean ActivtyResult(int requestCode, int resultCode, Intent data) {

		session = Session.getActiveSession();
		if (session == null) {
			if (requestCode == REQUEST_CODE_LOGIN)
				login();
		} else {

			session.onActivityResult(this.activity, requestCode, resultCode,
					data);
		}

		if (resultCode == Activity.RESULT_OK && session.isOpened()
				&& requestCode == REQUEST_CODE_LOGIN) {
			// onSessionStateChange(session, session.getState(), null);
			inter.loginResult(true, null);
		}

		if (requestCode == REQUEST_CODE_LOGIN_READ
				&& resultCode == Activity.RESULT_OK) {
			List<String> acceptedPermission = new ArrayList<String>();

			acceptedPermission = session.getPermissions();
			boolean check_mandate = true;

			for (int i = 0; i < mandate_Permission.size(); i++) {
				if (!acceptedPermission.contains(mandate_Permission.get(i))
						&& !mandate_Permission.get(i).equals(
								OBLFacebookPermission.PUBLISH_ACTIONS)) {
					error.setName("Permission Missing");
					error.setMessage("Permission Is Needed For Using This App.");
					error.setDescription("");
					inter.loginResult(false, error);
					check_mandate = false;
					break;
				}
			}
			if (check_mandate) {
				if (PublishPermission.size() != 0) {

					NewPermission = new ArrayList<String>();
					OldPermission = new ArrayList<String>();

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
							session.openForPublish(new Session.OpenRequest(
									activity)
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
									oblpost.postsStatusWithDetailsDescription(
											null, null, null, null, null);
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
						} else {
							inter.loginResult(true, null);
						}
					}

				} else {
					inter.loginResult(true, null);
				}
			} else {

			}

		}

		// Check When the publish permission are requested, whether the user
		// accepted or cancelled the publish permission
		if (requestCode == REQUEST_CODE_LOGIN_PUBLISH
				&& resultCode == Activity.RESULT_OK) {
			//
			List<String> acceptedPermission = new ArrayList<String>();
			acceptedPermission = session.getPermissions();
			boolean check_mandate = true;
			for (int i = 0; i < mandate_Permission.size(); i++) {
				if (!acceptedPermission.contains(mandate_Permission.get(i))) {
					error.setName("Permission Missing");
					error.setMessage("Permission Is Needed For Using This App.");
					error.setDescription("");
					inter.loginResult(false, error);
					check_mandate = false;
					break;
				}
			}
			if (check_mandate) {
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
						} else if (posttype == 0) {
							inter.loginResult(true, null);
						}
					} else {
						obllog.logMessage("Error: Publish Permission Not Granted");
						error.setName("Permission Missing");
						error.setMessage("Publish Permission Is Not Granted");
						error.setDescription("");
						oblpost.fbpostinterface.postingCompleted(false, error);
					}
					setPostCheck(false);
				} else {
					inter.loginResult(true, null);
				}
			}
		}

		// If the user cancels the login process or it gets cancelled due to
		// some another reason.
		if (resultCode == Activity.RESULT_CANCELED) {
			obllog.logMessage("Login Aborted");
			error.setName("Login Aborted");
			error.setMessage("Login Process Cancelled");
			error.setDescription("");
			inter.loginResult(false, error);
			if (requestCode == REQUEST_CODE_LOGIN
					|| requestCode == REQUEST_CODE_LOGIN_READ)
				logout();
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
		if (session != null) {
			session.closeAndClearTokenInformation();
			session = new Session(context);
			Session.setActiveSession(Session.getActiveSession());
			onSessionStateChange(session, session.getState(), null);
			Session.setActiveSession(session);
		}

		// onSessionStateChange(Session.getActiveSession(),
		// Session.getActiveSession().getState(), null);
		return true;
	}

	// This method is used whenever the state of session changes and it checks
	// whether the session is opened or closed. It passes the result to the
	// user using the loginResult() method of OBLFacebookLoginInterface
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			obllog.logMessage("Logged In...");
			// inter.loginResult(true, null);
		} else if ((state.isClosed() || state != SessionState.OPENED)
				&& state != SessionState.CREATED_TOKEN_LOADED) {
			obllog.logMessage("Logged Out...");
			// inter.loginResult(false, null);
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
	// If no permissions are passed then it will call the login() method.
	// @params permissions= Array of permissions which are needed when loging.
	public void loginWithPermission(String[] mandate, String[] optional) {

		session = Session.getActiveSession();

		nativeAppAvail = true;
		if (getLoginBehaviour() == SessionLoginBehavior.SSO_ONLY)
			nativeAppAvail = checkNativeApp();

		if (nativeAppAvail) {
			// If Permission is null then do login with basic permission
			if (mandate == null) {
				mandate = new String[0];
			}

			if (optional == null) {
				optional = new String[0];
			}
			mandate_Permission.clear();
			optional_Permission.clear();
			if (mandate.length == 0 && optional.length == 0) {
				login();
			} else {
				// Divide the Read and Publish Permissions
				ReadPermission = new ArrayList<String>();
				PublishPermission = new ArrayList<String>();

				for (int i = 0; i < mandate.length; i++) {
					if (isPublishPermission(mandate[i])) {
						PublishPermission.add(mandate[i]);
					} else {
						ReadPermission.add(mandate[i]);
					}
					mandate_Permission.add(mandate[i]);
				}

				for (int i = 0; i < optional.length; i++) {
					if (isPublishPermission(optional[i])) {
						PublishPermission.add(optional[i]);
					} else {
						ReadPermission.add(optional[i]);
					}
					optional_Permission.add(optional[i]);
				}

				// Get Active Session

				if (ReadPermission.size() != 0) {

					if (session == null
							|| session.getState() == SessionState.CLOSED_LOGIN_FAILED
							|| session.getState() == SessionState.CLOSED) {
						session = new Session(context);
					}
					if (!session.isOpened()) {
						session.openForRead(new Session.OpenRequest(activity)
								.setLoginBehavior(getLoginBehaviour())
								.setPermissions(ReadPermission)
								.setRequestCode(REQUEST_CODE_LOGIN_READ));
					}
					if (session.isOpened()) {
						session.requestNewReadPermissions(new Session.NewPermissionsRequest(
								activity, ReadPermission).setLoginBehavior(
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
							|| session.getState() == SessionState.CLOSED_LOGIN_FAILED
							|| session.getState() == SessionState.CLOSED) {
						session = new Session(context);
					}
					if (!session.isOpened()) {
						// Cannot Login with only publish permission. Ask For
						// basic
						// read Permission first and then login with publish
						// permission.
						NewPermission.add(OBLFacebookPermission.BASIC_INFO);
						String[] temppermissions = new String[NewPermission
								.size()];
						for (int i = 0; i < NewPermission.size(); i++) {
							temppermissions[i] = NewPermission.get(i);
						}
						setPostCheck(true);
						setPosttype(0);
						loginWithPermission(temppermissions, null);
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
			ArrayList<String> publishPermission = new ArrayList<String>();
			publishPermission.add(OBLFacebookPermission.PUBLISH_ACTIONS);
			session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					activity, publishPermission).setLoginBehavior(
					getLoginBehaviour()).setRequestCode(requestcode));
		}
		Session.setActiveSession(session);
	}

	public void checkPermission() {

	}
}