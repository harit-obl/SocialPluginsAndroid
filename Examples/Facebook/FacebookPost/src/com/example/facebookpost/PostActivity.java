package com.example.facebookpost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.facebooklibrary.OBLError;
import com.example.facebooklibrary.OBLFacebookLogin;
import com.example.facebooklibrary.OBLFacebookLoginInterface;
import com.example.facebooklibrary.OBLFacebookPost;
import com.example.facebooklibrary.OBLFacebookPostInterface;
import com.example.facebooklibrary.OBLLog;
/*
 * This a demo for allowing the user to post on their wall. The user 
 */
public class PostActivity extends Activity implements OBLFacebookPostInterface, OBLFacebookLoginInterface{

	Button post;
	OBLFacebookLogin objlogin;
	OBLFacebookPost objpost;
	OBLLog objlog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        post=(Button)findViewById(R.id.btn_post);
        
        //Check whether the user is logged in or logged out before closing the application.
        objlogin=new OBLFacebookLogin(this, this);
        //Set the login behaviour
        objlogin.setLoginBehaviour(OBLFacebookLogin.NATIVE_WEBVIEW);
        objlogin.initSession(savedInstanceState);
        
        objpost=new OBLFacebookPost(this, this);
        
        
        post.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Call post() to post on Wall with a message only.
				objpost.post("Post Message");
				
				//Call postsStatusWithDetailsDescription() to post on Wall with title, message, description, image and url.
				//objpost.postsStatusWithDetailsDescription("Post Message", "Post Title ", "Post Description", "Image Url", "Website Url");
			}
		});
        
       
    }

    
    //Checks if the post was successful or not. Error is displayed if it occurs while posting.
	@Override
	public void postingCompleted(boolean posted,OBLError error) {
		// TODO Auto-generated method stub
		if (posted==true)
		{
			Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG).show();
		}
		else if (posted==false)
		{
			Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG).show();
		}
		if (error!=null)
		{
			Log.i("Error","Name: "+error.getName()+" Message: "+error.getMessage()+" Description: "+error.getDescription());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// Pass the requestCode, resultCode and data to ActivityResult() method of OBLFacebookLogin for processing the result.
		objlogin.ActivtyResult(requestCode, resultCode, data);
	}
	
	
	
	//When the user is loging in or loging out, loginResult() method is called to notify whether the user is logged in or logged out. 
	// Error is displayed if it occurs during this process.
	@Override
	public void loginResult(boolean result,OBLError error) {
		// TODO Auto-generated method stub
		if (result==true)
		{
			Log.i("Login Status","Logged In");
		}
		else 
		{
			Log.i("Login Status","Logged Out");
		}
		if (error!=null)
		{
			Log.i("Error","Name: "+error.getName()+" Message: "+error.getMessage()+" Description: "+error.getDescription());
		}
	}

}
