package com.example.googlepluspostingdemo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.googlepluslibrary.OBLGooglePlusLogin;
import com.example.googlepluslibrary.OBLGooglePlusShare;
import com.example.googlepluslibrary.OBLGooglePlusShareInterface;
import com.example.googlepluslibrary.OBLLog;

public class MainActivity extends Activity implements OnClickListener,OBLGooglePlusShareInterface{

	Button share_btn,signout_btn;
	OBLGooglePlusLogin oblgplogin;
	OBLGooglePlusShare plusShare = new OBLGooglePlusShare(this, this);
	OBLLog obllog=new OBLLog();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        share_btn=(Button)findViewById(R.id.share_button);
        signout_btn=(Button)findViewById(R.id.signout_button);
        share_btn.setOnClickListener(this);
        signout_btn.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.share_button)
		{
			String title = "Fantasy Crciket";
			String description = "Build your own team, join public or private league and try to win any tournament";
			String status = "I am just creating FantasyCricket app." + " "
					+ description;
			String imageUri = "http://ridesharebuddy.com/ride_images/icon200.png";
			plusShare.postsStatusWithDetailsDescription(status, title,
					description, imageUri);
		}	
		
	}

	@Override
	public void sharingCompleted(boolean shared) {

		if (shared == true) {
			Toast.makeText(getApplicationContext(),"Shared Successfully" , Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(),"Failure to Share" , Toast.LENGTH_SHORT).show();
			}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		plusShare.onActivityResult(requestCode, resultCode, data);
	}

	
	
}
