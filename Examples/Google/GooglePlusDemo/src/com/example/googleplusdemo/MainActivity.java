package com.example.googleplusdemo;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.googlepluslibrary.OBLGooglePlusLogin;
import com.example.googlepluslibrary.OBLGooglePlusLoginInterface;
import com.example.googlepluslibrary.OBLLog;

public class MainActivity extends Activity implements OnClickListener,
		OBLGooglePlusLoginInterface {

	boolean result;
	Button sign_btn;
	OBLGooglePlusLogin oblgplogin;
	OBLLog obllog = new OBLLog();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sign_btn = (Button) findViewById(R.id.sign_button);
		sign_btn.setOnClickListener(this);
		oblgplogin = new OBLGooglePlusLogin(this, this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.sign_button) {
			oblgplogin.loginUsingInstalledApp(this, this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		obllog.logMessage("Calling onActivityResult of OBLGooglePlusLogin");
		oblgplogin.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void googlePlusLoginCompleted(boolean value) {
		if (value == true) {
			if (value == true) {
				obllog.setDebuggingON(true);
				obllog.logMessage("Login Successfull");
				Intent intent = new Intent(this, OtherActivity.class);
				startActivity(intent);
			}
		}
	}

	@Override
	public void googlePlusLogoutCompleted(boolean value) {

	}

}
