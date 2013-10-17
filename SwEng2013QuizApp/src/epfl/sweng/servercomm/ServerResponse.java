package epfl.sweng.servercomm;

/**
 * 
 * Simple class that models a response of the server containing the status code
 * and the entity of the response.
 * 
 */
public class ServerResponse {

	private String mStrEntity;
	private int mStatusCode;

	
	public ServerResponse(String strEntity, int statusCode) {
		this.mStrEntity = strEntity;
		this.mStatusCode = statusCode;
	}

	
	public String getEntity() {
		return mStrEntity;
	}

	
	public int getStatusCode() {
		return mStatusCode;
	}
}
