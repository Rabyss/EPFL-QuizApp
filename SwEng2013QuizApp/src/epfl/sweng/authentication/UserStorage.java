package epfl.sweng.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class UserStorage {
	private static UserStorage sInstance = null;
	private static final String NAME = "user_session";
	private static final String SESSION_KEY = "SESSION_ID";
	private Context mContext;
	
	private UserStorage(Context context) {
		mContext = context;
	}
	
	public static synchronized UserStorage getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new UserStorage(context);
		}
		
		return sInstance;
	}
	
	public void storeSessionID(String sessionID) {
		SharedPreferences prefs = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(SESSION_KEY, sessionID);
		editor.apply();
	}
	
	public String getSessionID() {
		SharedPreferences prefs = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		String sessionID = prefs.getString(SESSION_KEY, null);
		return sessionID;
	}
	
	public void removeSessionID() {
		SharedPreferences prefs = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.clear();
		editor.apply();
	}
}
