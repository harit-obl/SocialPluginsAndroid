SocialPluginsAndroid
====================

Social media plugin for android


### Introduction

SocialPluginsiOS provides integration with Facebook, Google+ and Twitter in Android in the simplest and easiest way.

It has different libraries for Facebook, Google+ and Twitter, so you can use any one of them or all at the same time.

### Features

####Facebook Library:

* Login/Logout with facebook in your application (with different permissions). 
* Fetch facebook user's profile.
* Post feed status in user's wall.
* Fetch facebook user's friends and their profile.
* Provides functinality to turn debugging ON or OFF (Log message makes easy to locate error).

####Google+ Library:

* Login/Logout with Google+ in your application. (with PLUS_LOGIN Scope)
* Fetch Google+ user's profile. (with PLUS_ME Scope)
* Sharing on user's wall.
* Fetch Google+ user's friends and their profile.
* Provides functionality to turn debugging ON or OFF (Log message makes easy to locate error).

####Twitter Library:

### Requirements

* Minimum Android 2.2



### Initial Setup

####Initial Setup For Facebook

* Create A New Project
* Download the Facebook-Android-SDK from [Facebook](https://developers.facebook.com/docs/android/) and FacebookLibrary from [FacebookLibrary](https://github.com/ObjectLounge/SocialPluginsAndroid/tree/beta).
* Import and Copy the FacebookLibrary and Facebook-Android-SDK to your current workspace.
* Go to your Facebook Account and create a Facebook App.
* Enter all the details (for help visit this [link](https://developers.facebook.com/docs/android/getting-started/)) and obtain your APP ID for the project.
* Go to your Project Manifest File and perform the following steps:
  * Add the following two lines inside the <application> tag:
  
   <code>
          &lt;activity android:name="com.facebook.LoginActivity"&gt;&lt;/activity&gt;
    </code>
    <code>
          &lt;meta-data android:name="com.facebook.sdk.ApplicationId" android:value="YOUR APP ID"/&gt;
  </code>

  Insert your project's App Id in the value attribute of the meta-data tag.

  * Add Internet Permission.

* Go to Project->Properties->Android->Library and Add FacebookLibrary.

####Intial Setup For Google+

- Create new Project in Eclipse.
- To authenticate and communicate with the Google+ APIs,you must first register your digitally signed .apk file's public certificate in the Google APIs Console:
   (for ref: https://developers.google.com/+/mobile/android/getting-started).
   Once you get the client id your project is registered successfully.
   
- Import google-play-service_lib or directly download from https://developers.google.com/+/downloads/
- Import GooglePlusLibrary project.
- Add GooglePlusLibray into your Main project
    ######To add a reference to a library project, follow these steps:

    * Make sure that both the project library and the application project that depends on it are in your workspace. If one of the projects is missing, import it into your workspace.
    * In the Package Explorer, right-click the dependent project and select Properties.
    * In the Properties window, select the "Android" properties group at left and locate the Library properties at right.
    * Click Add to open the Project Selection dialog.
    * From the list of available library projects, select a project and click OK.
    * When the dialog closes, click Apply in the Properties window.
    * Click OK to close the Properties window.
    
- Declare permissions
    * <code> &lt;uses-permission android:name="android.permission.INTERNET"&gt; </code>
    * <code> &lt;uses-permission android:name="android.permission.GET_ACCOUNTS"&gt; </code>
    * <code> &lt;uses-permission android:name="android.permission.USE_CREDENTIALS"&gt; </code>

###API Documentation

####Facebook Library:

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
  - Class fetches the user and user's friend's profile details.
  - Implement **[OBLFacebookQueryInterface](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Interface/OBLFacebookQueryInterface.java)** for receiving user profile details and user's friend's details.

Social media posting:

* `OBLFacebookPost`
  - Refer to **[OBLFacebookPost.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookPost.java)**
  - This class allows user to post on user's wall.
  - Implement **[OBLFacebookPostInterface](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Interface/OBLFacebookPostInterface.java)** for receiving post status update and also receive error message if it occurs while posting.


Profile details:

* `OBLFacebookUser`
  - Refer to **[OBLFacebookUser.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookUser.java)**
  - Main class having all profile information of Facebook use

* `OBLFacebookFriend`
  - Refer to **[OBLFacebookFriend.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/Facebook/Classes/OBLFacebookFriend.java)**
  - Class having all profile information of Facebook friend

Log (Debug):

* `OBLLog`
  - Refer to **[OBLLog.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/OBLLog.java)**
  - Call this class setDebuggingON() method and set debugging to TRUE to turn on the error logs.

####Google+ Library

You can see the demo code present in SampleGoogleCode Folder.

Login/Logout:
* `OBLGooglePlusLogin`
     - Refer to **[OBLGooglePlusLogin.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusLogin.java)**
     - OBLGooglePlusLogin class provides basic utilites like login, logout.
     - User has to call login() from the Main Activity to create the object of PlusClient and after creating the object it will directly call connect() for connecting the object.
     - logout() method will disconnect the PlusClient Object and clear the DefaultAccount.
     - Implement **[OBLGooglePlusLoginInterface.java](https://github.com/objectlounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Interface/OBLGooglePlusLoginInterface.java)** to check the results of log in and log out.
     
Social media query (Fetch data):
* `OBLGooglePlusQuery`
     - Refer to **[OBLGooglePlusQuery.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusQuery.java)**
     - Class fetches the user and user's friends profile.
     - The fetchUserProfile() will return the user's profile and allFriends() will return the friend's profile.
     - Implement **[OBLGooglePlusQueryInterface.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Interface/OBLGooglePlusQueryInterface.java)** in Main Activity to display the profile's by passing the objects of **[OBLProfileDetails.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLProfileDetails.java)** and **[OBLGooglePlusFriend.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusFriend.java)** classes in void userInfoReceived(OBLGooglePlusUser user) and void friendsInfoReceived(List<code>&lt;OBLGooglePlusFriend&gt;</code>oblgpuser) from **[OBLGooglePlusQuery.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusQuery.java)**
   
Social media posting:
* `OBLGooglePlusShare`
      - Refer to **[OBLGooglePlusShare.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusShare.java)**
      - This class allows user to share on user's wall.
      - Implement **[OBLGooglePlusShareInterface.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Interface/OBLGooglePlusShareInterface.java)** to check whether sharing has been done or not.
      
Profile details:
* `OBLGooglePlusUser`
       - Refer to **[OBLGooglePlusUser.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusUser.java)**
       - This class contains all the profile information of Google+ user.

* `OBLGooglePlusFriend`
       - Refer to **[OBLGooglePlusFriend.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/GooglePlus/Classes/OBLGooglePlusFriend.java)**
       - This class contains all the profile information of Google+ user's friends.
       
Log (Debug):

* `OBLLog`
  - Refer to **[OBLLog.java](https://github.com/ObjectLounge/SocialPluginsAndroid/blob/beta/library/OBLLog.java)**
  - Call this class and set debuggingON to YES to turn on the error logs like `[OBLLog setDebuggingON:YES];`
