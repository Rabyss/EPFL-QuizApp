package epfl.sweng.authentication;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONToken {
    private JSONObject jsonObject;
    
    public JSONToken(String json) throws JSONException {
        this.jsonObject = new JSONObject(json);
    }
    
    public String getToken() throws JSONException {
        return jsonObject.getString("token");
    }
}
