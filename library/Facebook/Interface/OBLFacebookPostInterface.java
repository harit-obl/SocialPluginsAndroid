package com.objectlounge.facebooklibrary;

public interface OBLFacebookPostInterface {

	void postingCompleted(boolean posted, OBLError error);

	void inviteCompleted(boolean invited, OBLError error);
}
