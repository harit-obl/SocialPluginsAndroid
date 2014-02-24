package com.example.facebooklibrary;

import android.util.Log;

public class OBLLog {

	public static boolean debuggingON;
	
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

	public  void setDebuggingON(boolean debuggingON) {
		OBLLog.debuggingON = debuggingON;
	}
	
}
