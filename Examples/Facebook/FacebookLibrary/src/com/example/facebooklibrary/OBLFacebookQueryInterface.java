package com.example.facebooklibrary;

import java.util.List;

public interface OBLFacebookQueryInterface {
	
	void userInfoReceived(OBLFacebookUser user,OBLError error);
	
	void friendsInfoReceived(List<OBLFacebookFriend> friends,OBLError error );
}
