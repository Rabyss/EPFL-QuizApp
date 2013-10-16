package epfl.sweng.authentication;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONSession {
	private JSONObject jsonObject;
		
	public JSONSession(String json) throws JSONException {
		this.jsonObject = new JSONObject(json);
	}
	
	public String getSession() throws JSONException {
		return jsonObject.getString("session");
	}
}
