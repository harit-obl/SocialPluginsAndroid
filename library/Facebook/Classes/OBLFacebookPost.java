package com.example.facebooklibrary;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class OBLFacebookPost extends OBLPost {

	Context context;
	Activity activity;
	Session session;
	List<String> permission;
	FacebookPostInterface fbpostinterface;
	OBLLog objlog;
	public static String status,title,description,image,url;
	public static final int REQUEST_CODE_POST=199193;
	public static final int REQUEST_CODE_POST_DETAILS=199194;

	public OBLFacebookPost(Context _context, Activity _activity) {
		// TODO Auto-generated constructor stub
		context = _context;
		activity = _activity;
		fbpostinterface = (FacebookPostInterface) activity;
		objlog=new OBLLog();
	}

	@Override
	public void post(String _status) {
		// TODO Auto-generated method stub
		if (_status!=null)
		status=_status;
		session = Session.getActiveSession();
		if (session==null)
		{
			OBLFacebookLogin slogin=new OBLFacebookLogin(context,activity);
			slogin.setLoginBehaviour(SessionLoginBehavior.SUPPRESS_SSO);
			slogin.postlogin(REQUEST_CODE_POST);
		}
		
		else if (session.isOpened()  ) {
			permission = new ArrayList<String>();
			permission = session.getPermissions();
			if (permission.contains("publish_actions")) {
				Bundle postParams = new Bundle();
				postParams.putString("message", status);
				postParams.putString("privacy", "{\"value\":\"EVERYONE\"}");
				Request.Callback callback = new Request.Callback() {
					@Override
					public void onCompleted(Response response) {

						JSONObject graphResponse = null;
						String postId = null;
						if (response.getError()==null)
						{
						graphResponse = response.getGraphObject()
								.getInnerJSONObject();
						
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							
							objlog.logMessage("Error Posting "+
									"JSON error " + e.getMessage());
							fbpostinterface.postingCompleted(false);

						}
						}
						else
						{
							objlog.logMessage("RESPONSE ERROR: "+response.getError().toString());
							fbpostinterface.postingCompleted(false);

						}
						
						
						FacebookRequestError error = response.getError();
						if (error != null) {
							objlog.logMessage("RESPONSE ERROR: "+error.getErrorMessage());
							fbpostinterface.postingCompleted(false);

						} else {
							fbpostinterface.postingCompleted(true);
							objlog.logMessage("Post Id: "+postId);
						}
					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			} else {
				objlog.logMessage("Error: Publish Permission Not Granted");
				fbpostinterface.postingCompleted(false);

			}
		}
		else
		{
			OBLFacebookLogin slogin=new OBLFacebookLogin(context,activity);
			slogin.setLoginBehaviour(SessionLoginBehavior.SUPPRESS_SSO);
			slogin.postlogin(REQUEST_CODE_POST);
			
		}
	}

	public void postsStatusWithDetailsDescription(String _status, String _title,
			String _description, String _image, String _url) {
		if (_status!=null)
		status=_status;
		if (_title!=null)
		title=_title;
		if (_description!=null)
		description=_description;
		if (_image!=null)
		image=_image;
		if (_url!=null)
		url=_url;
		session = Session.getActiveSession();
		if (session==null )
		{
			OBLFacebookLogin slogin=new OBLFacebookLogin(context,activity);
			slogin.setLoginBehaviour(SessionLoginBehavior.SUPPRESS_SSO);
			slogin.postlogin(REQUEST_CODE_POST);
		}
		else if (session.isOpened()) {
			permission = session.getPermissions();
			if (permission.contains("publish_actions")) {
				Bundle postParams = new Bundle();
				postParams.putString("name", title);
				postParams.putString("description", description);
				postParams.putString("message", status);
				postParams.putString("link", url);
				postParams
						.putString(
								"picture",
								image);
				postParams.putString("privacy", "{\"value\":\"EVERYONE\"}");
				Request.Callback callback = new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						JSONObject graphResponse = null;
						String postId = null;
						if (response.getError()==null)
						{
						graphResponse = response.getGraphObject()
								.getInnerJSONObject();
						
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							objlog.logMessage("Error Posting "+
									"JSON error " + e.getMessage());
							fbpostinterface.postingCompleted(false);

						}
						}
						else
						{
							objlog.logMessage("RESPONSE ERROR: "+response.getError().toString());
							fbpostinterface.postingCompleted(false);

						}
						
						FacebookRequestError error = response.getError();
						if (error != null) {
							objlog.logMessage("RESPONSE ERROR: "+error.getErrorMessage());
							fbpostinterface.postingCompleted(false);

						} else {
							fbpostinterface.postingCompleted(true);
							objlog.logMessage("Post Id: "+postId);
						}
					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			} else {
				objlog.logMessage("Error: Publish Permission Not Granted");
				fbpostinterface.postingCompleted(false);

			}
		}
		else
		{
			OBLFacebookLogin slogin=new OBLFacebookLogin(context,activity);
			slogin.setLoginBehaviour(SessionLoginBehavior.SUPPRESS_SSO);
			slogin.postlogin(REQUEST_CODE_POST_DETAILS);
		}
	}

}
