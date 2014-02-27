SocialPluginsAndroid
====================

Social media plugin for Android


## Introduction

SocialPluginsAndroid provides integration with Facebook and Google+ in Android in the simplest and easiest way.

It has different libraries for Facebook and Google+, so you can use any one of them or all at the same time.

##Google+ Library

### Features

* Login/Logout with Google+ in your application. (with PLUS_LOGIN Scope)
* Fetch Google+ user's profile. (with PLUS_ME Scope)
* Sharing on user's wall.
* Fetch Google+ user's friends and their profile.

### Requirements

* Minimum Android 2.2

### Initial Setup

- Create new Project in Eclipse.
- To authenticate and communicate with the Google+ APIs,you must first register your digitally signed .apk file's public certificate in the Google APIs Console:
   (for ref: https://developers.google.com/+/mobile/android/getting-started).
   Once you get the client id your project is registered successfully.
   
- Download google-play-services from your Android SDK Manager. Import and copy google-play-service project in your current workspace (Path of google-play-services will be adt-bundle/sdk/extras/google/google_play_services).
- Download GooglePlusLibrary from [GooglePlusLibrary](https://github.com/ObjectLounge/SocialPluginsAndroid/tree/beta/library). Import and copy GooglePlusLibrary Project in your current workspace.
- Go to Project->Properties->Android->Library and Add GooglePlusLibrary.
- Remove android-support-v4.jar from your libs folder if present.
- Go to your Project Manifest File and perform the following steps:

     - Add the following two lines inside the <code>&lt;application&gt;</code> tag:
   <code>  &lt;meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" /&gt;</code>
   
     - Declare permissions
        - <code> &lt;uses-permission android:name="android.permission.INTERNET"/&gt; </code>
        - <code> &lt;uses-permission android:name="android.permission.GET_ACCOUNTS"/&gt; </code>
        - <code> &lt;uses-permission android:name="android.permission.USE_CREDENTIALS"/&gt; </code>


###Google+ Library Documentation

You can see the demo code present in [Example Folder.](https://github.com/ObjectLounge/SocialPluginsAndroid/tree/beta/Examples)

Login/Logout:
* `OBLGooglePlusLogin`
     - Refer to **[OBLGooglePlusLogin.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusLogin.java)**
     - OBLGooglePlusLogin class provides basic utilites like login, logout.
     - User has to call login() from the Main Activity to create the object of PlusClient and it will directly connect.
     - logout() method will disconnect the PlusClient Object and clear the DefaultAccount.
     - Implement **[OBLGooglePlusLoginInterface.java](https://github.com/objectlounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Interface/OBLGooglePlusLoginInterface.java)** for receiving loging status.
     
Social media query (Fetch data):
* `OBLGooglePlusQuery`
     - Refer to **[OBLGooglePlusQuery.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusQuery.java)**
     - OBLGooglePlusQuery Class fetches the user and user's friends profile.
     - Implement **[OBLGooglePlusQueryInterface.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Interface/OBLGooglePlusQueryInterface.java)** for receiving user profile details and user's friend's details. 
     

Social media posting:
* `OBLGooglePlusShare`
      - Refer to **[OBLGooglePlusShare.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusShare.java)**
      - OBLGooglePlusShare Class allows user to share on user's wall.
      - Implement **[OBLGooglePlusShareInterface.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Interface/OBLGooglePlusShareInterface.java)** to check whether sharing has been done or not.
      
Profile details:
* `OBLGooglePlusUser`
       - Refer to **[OBLGooglePlusUser.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusUser.java)**
       - OBLGooglePlusUser Class contains all the profile information of Google+ user.

* `OBLGooglePlusFriend`
       - Refer to **[OBLGooglePlusFriend.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusFriend.java)**
       - OBLGooglePlusFriend Class contains basic details of Google+ user's friends.


##Facebook Library

### Features

* Login/Logout with facebook in your application (with different permissions). 
* Fetch facebook user's profile.
* Post feed status in user's wall.
* Fetch facebook user's friends and their profile.

### Requirements

* Minimum Android 2.2

### Initial Setup


- Create A New Project in Eclipse.
- Download the Facebook-Android-SDK from [Facebook](https://developers.facebook.com/docs/android/) and FacebookLibrary from [FacebookLibrary](https://github.com/ObjectLounge/SocialPluginsAndroid/tree/beta/library).
- Import and Copy the FacebookLibrary and Facebook-Android-SDK to your current workspace.
- Go to your Facebook Account and create a Facebook App.
- Enter all the details (for help visit this [link](https://developers.facebook.com/docs/android/getting-started/)) and obtain your APP ID for the project.
- Go to your Project res->values->strings.xml file and add this line:

  <code>  &lt;string name="app_id"&gt;YOUR APP ID&lt;/string&gt; </code>
  
  Insert your project's App Id here.
  
- Go to your Project Manifest File and perform the following steps:
  - Add the following two lines inside the <code>&lt;application&gt;</code> tag:
  
   <code>
          &lt;activity android:name="com.facebook.LoginActivity"&gt;&lt;/activity&gt;
    </code>
    <code>
          &lt;meta-data android:name="com.facebook.sdk.ApplicationId" android:value="@string/app_id"/&gt;
  </code>

  

  - Declare permissions
    - <code> &lt;uses-permission android:name="android.permission.INTERNET"/&gt; </code>

- Go to Project->Properties->Android->Library and Add FacebookLibrary.
- Remove android-support-v4.jar from your libs folder if present.
- The login flow for your app will require the users to transition out of, and back into, the Activity. Write the below code on every Activity where you will use the Facebook features. And call ActivityResult() method of OBLFacebookLogin from this method.
  <pre>
  <code>@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		objlogin.ActivtyResult(requestCode, resultCode, data); //objlogin=Object Of OBLFacebookLogin Class
	}
  </code></pre>

###Facebook Library Documentation



You can see the demo code present in [Examples Folder.](https://github.com/ObjectLounge/SocialPluginsAndroid/tree/beta/Examples)

Login/Logout:

* `OBLFacebookLogin`
  - Refer to **[OBLFacebookLogin.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookLogin.java)**
  - Facebook Login class provides basic utilities for the facebook like login, logout, loging with permissions.
  - User has to call login() or loginWithPermission() methods for logging in and call logout() to log out of Facebook.
  - Implement **[OBLFacebookLoginInterface](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Interface/OBLFacebookLoginInterface.java)** for receiving loging status.
* `OBLFacebookPermission`
  - Refer to **[OBLFacebookPermission.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookPermission.java)**
  - List all the Facebook Permissions that can be used with this library.
  

Social media query (Fetch data):

* `OBLFacebookQuey`
  - Refer to **[OBLFacebookQuey.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookQuey.java)**
  - OBLFacebookQuey Class fetches the user and user's friend's profile details.
  - Implement **[OBLFacebookQueryInterface](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Interface/OBLFacebookQueryInterface.java)** for receiving user profile details and user's friend's details.

Social media posting:

* `OBLFacebookPost`
  - Refer to **[OBLFacebookPost.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookPost.java)**
  - OBLFacebookPost Class allows user to post on user's wall.
  - Implement **[OBLFacebookPostInterface](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Interface/OBLFacebookPostInterface.java)** for receiving post status update and also receive error message if it occurs while posting.


Profile details:

* `OBLFacebookUser`
  - Refer to **[OBLFacebookUser.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookUser.java)**
  - OBLFacebookUser Class has all profile information of Facebook user.

* `OBLFacebookFriend`
  - Refer to **[OBLFacebookFriend.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookFriend.java)**
  - OBLFacebookFriend Class has all profile information of Facebook friend.
       
