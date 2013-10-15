package epfl.sweng.servercomm;

import epfl.sweng.events.Event;

public abstract class ServerEvent extends Event {
	private static final long serialVersionUID = -2895588419411857521L;
	private String mResponse;
	
	public ServerEvent() {
		super();
	}
	
	public ServerEvent(String response) {
		super();
		this.mResponse = response;
	}

	public String getResponse() {
		return mResponse;
	}

	public void setResponse(String mResponse) {
		this.mResponse = mResponse;
	}
}
