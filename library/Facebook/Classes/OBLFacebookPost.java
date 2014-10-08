package com.objectlounge.facebooklibrary;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class OBLFacebookPost extends OBLPost {

	Context context;
	Activity activity;
	Session session;
	List<String> permission;
	OBLFacebookPostInterface fbpostinterface;
	OBLLog objlog;
	public static String status, title, description, image, url;
	OBLFacebookLogin objlogin;
	OBLError error;

	public OBLFacebookPost(Context _context, Activity _activity) {
		// TODO Auto-generated constructor stub
		context = _context;
		activity = _activity;
		// Check whether the user activity has implemented the
		// OBLFacebookPostInterface.
		try {
			fbpostinterface = (OBLFacebookPostInterface) activity;
		} catch (ClassCastException e) {
			objlog.logMessage(activity.getLocalClassName()
					+ " must implement OBLFacebookPostInterface for getting Post result");
			activity.finish();
		}

		objlog = new OBLLog();
		objlogin = new OBLFacebookLogin(context, activity);
		error = new OBLError();
	}

	// Post a feed on User's Profile with the message passed by the user.
	// _status=message to post.
	@Override
	public void post(String _status) {
		// TODO Auto-generated method stub
		if (_status != null)
			status = _status;
		session = Session.getActiveSession();
		if (session == null) {
			session = new Session(context);
			Session.setActiveSession(session);
		}

		if (session.isOpened()) {
			permission = new ArrayList<String>();
			permission = session.getPermissions();
			if (permission.contains(OBLFacebookPermission.PUBLISH_ACTIONS)) {
				Bundle postParams = new Bundle();
				postParams.putString("message", status);
				postParams.putString("privacy", "{\"value\":\"EVERYONE\"}");
				Request.Callback callback = new Request.Callback() {
					@Override
					public void onCompleted(Response response) {

						JSONObject graphResponse = null;
						String postId = null;
						if (response.getError() == null) {
							graphResponse = response.getGraphObject()
									.getInnerJSONObject();

							try {
								postId = graphResponse.getString("id");

							} catch (JSONException e) {

								objlog.logMessage("Error Posting "
										+ "JSON error " + e.getMessage());
								error.setName("Post Error");
								error.setMessage(e.getMessage());
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);
							}

						} else {
							if (response.getError().getErrorCode() == 200) {
								objlog.logMessage("Error: Publish Permission Not Granted");
								error.setName("Permission Missing");
								error.setMessage("Publish Permission Is Not Granted");
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);

							} else if (response.getError().getErrorCode() == 190) {
								objlog.logMessage("Error: The user has not authorized this application.");
								error.setName("Not Authorized");
								error.setMessage("User Is Not Authorized.");
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);
								objlogin.logout();
								OBLFacebookLogin.setPostCheck(true);
								OBLFacebookLogin.setPosttype(1);
								objlogin.loginWithPermission(
										new String[] {
												OBLFacebookPermission.BASIC_INFO,
												OBLFacebookPermission.PUBLISH_ACTIONS },
										null);
							} else if (response.getError().getErrorCode() == 506) {
								objlog.logMessage("Error: This Post Is Identical to the Previous Post.");
								error.setName("Identical Post");
								error.setMessage(response.getError()
										.getErrorMessage());
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);
							}
						}

						FacebookRequestError error1 = response.getError();
						if (error1 != null) {
							objlog.logMessage("RESPONSE ERROR: "
									+ error1.getErrorMessage());
							error.setName("Posting Error");
							error.setMessage(error1.getErrorMessage());
							error.setDescription("");
							fbpostinterface.postingCompleted(false, error);

						} else {
							fbpostinterface.postingCompleted(true, null);
							objlog.logMessage("Post Id: " + postId);
						}
					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			} else {
				objlog.logMessage("Error: Publish Permission Not Granted");
				OBLFacebookLogin.setPostCheck(true);
				OBLFacebookLogin.setPosttype(1);
				objlogin.getpostpermission(OBLFacebookLogin.REQUEST_CODE_LOGIN_PUBLISH);
			}
		} else {
			OBLFacebookLogin.setPostCheck(true);
			OBLFacebookLogin.setPosttype(1);
			objlogin.loginWithPermission(new String[] {
					OBLFacebookPermission.BASIC_INFO,
					OBLFacebookPermission.PUBLISH_ACTIONS }, null);
		}
	}

	// Post a feed on User's Profile with the title, message, description, image
	// url and website url.
	// _title=Title of the post.
	// _status=Message of the post.
	// _description=Description of the post.
	// _image=Url of the image.
	// _url=Url of the website.
	public void postsStatusWithDetailsDescription(String _status,
			String _title, String _description, String _image, String _url) {
		if (_status != null)
			status = _status;
		if (_title != null)
			title = _title;
		if (_description != null)
			description = _description;
		if (_image != null)
			image = _image;
		if (_url != null)
			url = _url;
		session = Session.getActiveSession();
		if (session == null) {
			session = new Session(context);
			Session.setActiveSession(session);
		}
		if (session.isOpened()) {
			permission = session.getPermissions();
			if (permission.contains("publish_actions")) {
				Bundle postParams = new Bundle();
				postParams.putString("name", title);
				postParams.putString("description", description);
				postParams.putString("message", status);
				postParams.putString("link", url);
				postParams.putString("picture", image);
				postParams.putString("privacy", "{\"value\":\"EVERYONE\"}");
				Request.Callback callback = new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						JSONObject graphResponse = null;
						String postId = null;
						if (response.getError() == null) {
							graphResponse = response.getGraphObject()
									.getInnerJSONObject();

							try {
								postId = graphResponse.getString("id");
							} catch (JSONException e) {
								objlog.logMessage("Error Posting "
										+ "JSON error " + e.getMessage());
								error.setName("Post Error");
								error.setMessage(e.getMessage());
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);

							}
						} else {
							objlog.logMessage("RESPONSE ERROR: "
									+ response.getError().toString());
							if (response.getError().getErrorCode() == 200) {

								objlog.logMessage("Error: Publish Permission Not Granted");
								error.setName("Permission Missing");
								error.setMessage("Publish Permission Is Not Granted");
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);

							} else if (response.getError().getErrorCode() == 190) {
								objlog.logMessage("Error: The user has not authorized this application.");
								error.setName("Not Authorized");
								error.setMessage(response.getError()
										.getErrorMessage());
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);
								objlogin.logout();
								OBLFacebookLogin.setPostCheck(true);
								OBLFacebookLogin.setPosttype(2);
								objlogin.loginWithPermission(
										new String[] {
												OBLFacebookPermission.BASIC_INFO,
												OBLFacebookPermission.PUBLISH_ACTIONS },
										null);
							} else if (response.getError().getErrorCode() == 506) {
								objlog.logMessage("Error: This Post Is Identical to the Previous Post.");
								error.setName("Identical Post");
								error.setMessage(response.getError()
										.getErrorMessage());
								error.setDescription("");
								fbpostinterface.postingCompleted(false, error);
							}

						}

						FacebookRequestError error1 = response.getError();
						if (error1 != null) {
							objlog.logMessage("RESPONSE ERROR: "
									+ error1.getErrorMessage());
							error.setName("Post Error");
							error.setMessage(error1.getErrorMessage());
							error.setDescription("");
							fbpostinterface.postingCompleted(false, error);

						} else {
							fbpostinterface.postingCompleted(true, null);
							objlog.logMessage("Post Id: " + postId);
						}
					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			} else {
				objlog.logMessage("Error: Publish Permission Not Granted");
				OBLFacebookLogin.setPostCheck(true);
				OBLFacebookLogin.setPosttype(2);
				objlogin.getpostpermission(OBLFacebookLogin.REQUEST_CODE_LOGIN_PUBLISH);
			}
		} else {
			OBLFacebookLogin.setPostCheck(true);
			OBLFacebookLogin.setPosttype(2);
			objlogin.loginWithPermission(new String[] {
					OBLFacebookPermission.BASIC_INFO,
					OBLFacebookPermission.PUBLISH_ACTIONS }, null);
		}
	}

	public void postonFriendsWall(String friend_uid, String userId,
			String title, String caption, String description, String link,
			String imageUrl) {
		Bundle params = new Bundle();
		params.putString("name", title);
		params.putString("caption", caption);
		params.putString("from", userId);
		params.putString("to", friend_uid);
		Log.e("friends User Id", friend_uid);
		// params.putString("privacy", "{\"value\":\"CUSTOM\",\"allow\":"
		// + friend_uid + ",\"deny\":\"100001436507768\"}");
		// params.putString("privacy", "{\"value\":\"SELF\"}");
		params.putString("description", description);
		params.putString("link", link);
		params.putString("picture", imageUrl);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(activity,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException errorInvite) {
						if (errorInvite == null) {
							// When the story is posted, echo the success
							// and the post Id.
							Log.e("Values ", values.toString());
							final String postId = values.getString("post_id");
							if (postId != null) {
								fbpostinterface.inviteCompleted(true, null);
							} else {
								// User clicked the Cancel button
								fbpostinterface.inviteCompleted(false, null);

							}
						} else if (errorInvite instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							fbpostinterface.inviteCompleted(false, null);

						} else {
							// Generic, ex: network error
							error.setName("Post Error");
							error.setMessage(errorInvite.getMessage());
							error.setDescription("");
							fbpostinterface.inviteCompleted(false, error);
						}
					}

				}).build();
		feedDialog.show();

		// WebDialog.FeedDialogBuilder builder = new FeedDialogBuilder(context);
		// builder.setTo(friend_uid);
		// builder.setFrom(userId);
		// builder.setDescription("Post On Faceboook Wall");
		// builder.setLink("https://www.google.com");
		// WebDialog dialog = builder.build();
		// dialog.show();
	}

	// }

	public void sendInvitation() {
		session = Session.getActiveSession();
		if (session == null) {
			session = new Session(context);
			Session.setActiveSession(session);
		}
		if (session.isOpened()) {
			Bundle params = new Bundle();
			params.putString("message", "YOUR_MESSAGE_HERE");

			WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
					activity, Session.getActiveSession(), params))
					.setOnCompleteListener(new OnCompleteListener() {
						@Override
						public void onComplete(Bundle values,
								FacebookException error) {
							if (error != null) {
								if (error instanceof FacebookOperationCanceledException) {
									Toast.makeText(
											context.getApplicationContext(),
											"Request cancelled",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context.getApplicationContext(),
											"Network Error", Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								final String requestId = values
										.getString("request");
								if (requestId != null) {
									Toast.makeText(
											context.getApplicationContext(),
											"Request sent", Toast.LENGTH_SHORT)
											.show();
								} else {
									Toast.makeText(
											context.getApplicationContext(),
											"Request cancelled",
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					}).build();
			requestsDialog.show();
		}
	}
}
