package epfl.sweng.servercomm;

import java.io.Serializable;

/**
 * 
 * Simple class that models a response of the server containing the status code
 * and the entity of the response.
 * 
 */
public class ServerResponse implements Serializable{

	private static final long serialVersionUID = -3594630550846728123L;

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
