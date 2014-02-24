package com.example.facebooklibrary;

import java.util.List;

public interface OBLFacebookQueryInterface {

	void userInfoReceived(OBLFacebookUser user);
	
	
	void friendsInfoReceived(List<OBLFacebookFriend> friends);
}
