package com.example.facebooklibrary;

import android.content.Intent;

public interface OBLFacebookLoginInterface {

	public void onActivityResult(int requestCode, int resultCode, Intent data);
		// TODO Auto-generated method stub
	public void loginResult(boolean result);
	
}
