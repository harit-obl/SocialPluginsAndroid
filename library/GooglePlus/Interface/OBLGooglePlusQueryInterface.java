package com.example.googlepluslibrary;

import java.util.List;

public interface OBLGooglePlusQueryInterface {
	void userInfoReceived(OBLGooglePlusUser user);
	void friendsInfoReceived(List<OBLGooglePlusFriend> oblgpuser);

}
