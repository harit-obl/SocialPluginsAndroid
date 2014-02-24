package com.example.facebooklibrary;

public class OBLFacebookFriend extends OBLProfileDetails {

	public String gender;
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public OBLFacebookFriend(String id,String _name,String _gender){
		this.socialMediaId=id;
		this.name=_name;
		this.gender=_gender;
	}
	
	public String getsocialMediaId() {
		return socialMediaId;
	}
	public void setsocialMediaId(String id)
	{
		socialMediaId=id;
	}

	public String getname()
	{
		return name;
	}
	public void setname(String _name) {
		name = _name;
	}
}
