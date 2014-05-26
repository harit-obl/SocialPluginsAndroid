package com.objectlounge.facebooklibrary;

import android.util.Log;

/*Use this class to display error log messages.*/

public class OBLLog {

	public static boolean debuggingON;
	
	//Pass the Error Message which will be displayed in Log.
	public  void logMessage(String message)
	{
		if (debuggingON)
		{
			Log.i("Message",message);
		}
	}

	public  boolean isDebuggingON() {
		return debuggingON;
	}

	//Set debug messages to on/off
	public  void setDebuggingON(boolean debuggingON) {
		OBLLog.debuggingON = debuggingON;
	}
	
}
