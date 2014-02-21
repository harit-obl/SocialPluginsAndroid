package com.example.facebooklibrary;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class OBLFacebookLogin extends OBLLogin {

	private List<String> ReadPermission, PublishPermission, OldPermission,
			NewPermission;
	private Session session;
	private Context context;
	private Activity activity;
	private UiLifecycleHelper uiHelper;
	private SessionLoginBehavior loginBehaviour = SessionLoginBehavior.SSO_WITH_FALLBACK;
	private static final String PUBLISH_PERMISSION_PREFIX = "publish";
	private static final String MANAGE_PERMISSION_PREFIX = "manage";
	public static final int REQUEST_CODE_LOGIN = 199188;
	public static final int REQUEST_CODE_LOGIN_READ = 199187;
	public static final int REQUEST_CODE_LOGIN_PUBLISH = 199189;
	OBLLog obllog;
	OBLFacebookLoginInterface inter;
	boolean check = false;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			onSessionStateChange(session, state, exception);
		}
	};

	public OBLFacebookLogin(Context _context, Activity _activity) {
		this.context = _context;
		this.activity = _activity;
		obllog = new OBLLog();
		inter = (OBLFacebookLoginInterface) activity;
	}

	// Initialize (Start) Session
	public Session initSession(Bundle savedInstanceState) {
		uiHelper = new UiLifecycleHelper(activity, callback);
		uiHelper.onCreate(savedInstanceState);
		session = Session.getActiveSession();
		Log.i("FINState",session.getState().toString());
		if (session == null) {
			if (savedInstanceState != null) {
				Log.i("Restore",session.getState().toString());
				session = Session.restoreSession(activity, null, null,
						savedInstanceState);
			}
			if (session == null) {
				Log.i("NULL LOGIN",session.getState().toString());
				session = new Session(context);
			}
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				Log.i("OBLFBLOGIN",session.getState().toString());
				session.openForRead(new Session.OpenRequest(activity));
			}
		}
		Session.setActiveSession(session);
		return session;
	}

	@Override
	public void login() {
		session = Session.getActiveSession();

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
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Log.i("Check Value:", String.valueOf(check));
		Session.getActiveSession().onActivityResult(activity, requestCode,
				resultCode, data);
		session = Session.getActiveSession();
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
								.setLoginBehavior(
										SessionLoginBehavior.SUPPRESS_SSO)
								.setPermissions(NewPermission)
								.setRequestCode(REQUEST_CODE_LOGIN_PUBLISH));
					}
					if (session.isOpened()) {
						session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
								activity, NewPermission)
								.setCallback(callback)
								.setLoginBehavior(
										SessionLoginBehavior.SUPPRESS_SSO)
								.setRequestCode(REQUEST_CODE_LOGIN_PUBLISH));
					}
				}
			}

		}
		if ((requestCode == OBLFacebookPost.REQUEST_CODE_POST || requestCode == OBLFacebookPost.REQUEST_CODE_POST_DETAILS)
				&& resultCode == Activity.RESULT_OK) {
			OBLFacebookPost oblpost = new OBLFacebookPost(context, activity);
			if (requestCode == OBLFacebookPost.REQUEST_CODE_POST) {
				oblpost.post(null);
			} else if (requestCode == OBLFacebookPost.REQUEST_CODE_POST_DETAILS) {
				oblpost.postsStatusWithDetailsDescription(null, null, null,
						null, null);
			}

		}

		if (resultCode == Activity.RESULT_CANCELED) {
			obllog.logMessage("Login Aborted");
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
		Session.setActiveSession(session);
		if (state.isOpened()) {
			obllog.logMessage("Logged In...");
			if (check == true)
				check = false;
			else
				inter.loginResult(true);
		} else if (state.isClosed()
				&& state != SessionState.CLOSED_LOGIN_FAILED) {
			obllog.logMessage("Logged Out...");
			inter.loginResult(false);
		}
	}

	public static boolean isPublishPermission(String permission) {
		return permission != null
				&& (permission.startsWith(PUBLISH_PERMISSION_PREFIX) || permission
						.startsWith(MANAGE_PERMISSION_PREFIX));

	}

	public void loginWithPermission(String[] permissions) {
		session = Session.getActiveSession();
		obllog.setDebuggingON(true);
		// If Permission is null then do login with basic permission
		if (permissions.length == 0) {
			login();
		} else {
			// Divide the Read and Publish Permissions
			ReadPermission = new ArrayList<String>();
			PublishPermission = new ArrayList<String>();

			for (int i = 0; i < permissions.length; i++) {
				if (isPublishPermission(permissions[i])) {
					check = true;
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
							activity, NewPermission).setCallback(callback)
							.setLoginBehavior(getLoginBehaviour())
							.setRequestCode(REQUEST_CODE_LOGIN_READ));
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
							activity, NewPermission).setCallback(callback)
							.setLoginBehavior(getLoginBehaviour())
							.setRequestCode(REQUEST_CODE_LOGIN_PUBLISH));
				}
				Session.setActiveSession(session);
			}
		}

	}

	public void resume() {
		uiHelper.onResume();
	}

	public void pause() {
		uiHelper.onPause();
	}

	public void destroy() {
		uiHelper.onDestroy();
	}

	public SessionLoginBehavior getLoginBehaviour() {
		return loginBehaviour;
	}

	public void setLoginBehaviour(SessionLoginBehavior loginBehaviour) {
		this.loginBehaviour = loginBehaviour;
	}

	public void saveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		uiHelper.onSaveInstanceState(outState);
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

	public void postlogin(int requestcode) {
		session = Session.getActiveSession();

		if (session == null
				|| session.getState() == SessionState.CLOSED_LOGIN_FAILED
				|| session.getState() == SessionState.CLOSED) {
			session = new Session(context);
		}
		if (!session.isOpened()) {
			session.openForPublish(new Session.OpenRequest(activity)
					.setLoginBehavior(getLoginBehaviour())
					.setRequestCode(requestcode)
					.setPermissions("publish_actions"));
		}
		Session.setActiveSession(session);
	}
}