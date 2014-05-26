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
        objlog=new OBLLog();
        objlog.setDebuggingON(true);
        
        objlogin=new OBLFacebookLogin(this, this);
        objlogin.setLoginBehaviour(OBLFacebookLogin.NATIVE_WEBVIEW);
        
        objpost=new OBLFacebookPost(this, this);
        
        
        post.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				objpost.post("Ahmedabad");
			}
		});
        
       
    }

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
		objlogin.ActivtyResult(requestCode, resultCode, data);
	}
	
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
