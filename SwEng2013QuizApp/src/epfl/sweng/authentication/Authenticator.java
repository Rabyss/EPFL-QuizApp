package epfl.sweng.authentication;

import epfl.sweng.servercomm.ServerCommunicator;

public class Authenticator {
	private String username;
	private String password;
	private String token;
	
	public Authenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	private String requestToken() {
		// String json = ServerCommunicator.getInstance().getHttp("https://sweng-quiz.appspot.com/login");
		return "";
	}
}
