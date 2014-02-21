package com.example.facebooklibrary;

import java.util.List;

public interface FacebookQueryInterface {

	void userInfoReceived(OBLFacebookUser user);
	
	
	void friendsInfoReceived(List<OBLFacebookFriend> friends);
}
