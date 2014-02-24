package com.example.googlepluslibrary;

public class OBLGooglePlusFriend extends OBLGooglePlusUser {

	String gender_value;

	public OBLGooglePlusFriend(String id, String name, String birthdate,
			String gender, String imageUrl) {

		this.socialMediaId = id;
		this.name = name;
		this.birthdate = birthdate;
		this.gender = gender;
		this.imageUrl = imageUrl;

		// TODO Auto-generated constructor stub
	}

	public String getSocialMediaId() {
		return socialMediaId;
	}

	public void setSocialMediaId(String id) {
		socialMediaId = id;
	}

	@Override
	public String getname() {
		return name;
	}

	@Override
	public void setname(String _name) {
		name = _name;
	}

	public void setBithday(String _birthdate) {
		birthdate = _birthdate;
	}

	@Override
	public String getBirthdate() {
		return birthdate;
	}

	@Override
	public void setGender(String _gender) {
		gender = _gender;
	}

	@Override
	public String getGender() {
		return gender;
	}

	public void setImageUrl(String _imageUrl) {
		imageUrl = _imageUrl;
	}

	public String getImageUrl()

	{
		return imageUrl;
	}

}
