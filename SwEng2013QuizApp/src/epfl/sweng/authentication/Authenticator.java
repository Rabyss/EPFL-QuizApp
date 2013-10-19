package epfl.sweng.authentication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;

public class Authenticator extends EventEmitter implements EventListener {
	private static final int SWENG_OK = 200;
	private static final int TEQUILA_OK = 302;
	private static final int TEQUILA_WRONG_CREDITENTIAL = 200;
	
	private String mUsername;
	private String mPassword;
	private String mToken;
	private String mSession;
	
	public Authenticator(String username, String password) {
		this.mUsername = username;
		this.mPassword = password;
	}
	
	public void authenticate() {
		ServerCommunicator.getInstance().addListener(this);
		requestToken();
	}
	
	private void requestToken() {
		RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/login");
		ServerCommunicator.getInstance().doHttpGet(req, new ServerAuthenticationEvent.GettingTokenEvent());
	}
	
	private void tequilaAuth() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("requestkey", this.mToken));
		params.add(new BasicNameValuePair("username", this.mUsername));
		params.add(new BasicNameValuePair("password", this.mPassword));
		
		try {
			UrlEncodedFormEntity entity;
			entity = new UrlEncodedFormEntity(params);
			RequestContext req = new RequestContext("https://tequila.epfl.ch/cgi-bin/tequila/login", entity);
			req.addHeader(entity.getContentType());
			
            ServerCommunicator.getInstance().doHttpPost(req, new ServerAuthenticationEvent.TequilaStatusEvent());
		} catch (UnsupportedEncodingException e) {
			this.error("Error: Unsupported Encoding Exception.");
		}
	}
	
	private void requestSessionID() {
		String json = "{\"token\": \""+this.mToken+"\"}";
		
		try {
			StringEntity entity;
			entity = new StringEntity(json);
			RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/login", entity);
            req.addHeader("Content-type", "application/json");
			ServerCommunicator.getInstance().doHttpPost(req, new ServerAuthenticationEvent.GettingSessionIDEvent());
		} catch (UnsupportedEncodingException e) {
			this.error("Error: Unsupported Encoding Exception.");
		}
	}
	
	private void error(String message) {
		this.emit(new AuthenticationEvent.AuthenticationErrorEvent(message));
		ServerCommunicator.getInstance().removeListener(this);
	}
	
	public void on(ServerAuthenticationEvent.GettingTokenEvent event) {
		int status = event.getStatus();
		
		if (status == SWENG_OK) {
			try {
				String json = event.getToken();
				this.mToken = new JSONToken(json).getToken();
			} catch (JSONException e) {
				this.error("Error: malformed JSON (token).");
			}
			
			tequilaAuth();
		} else {
			this.error("Error "+status+" on SwEng Server.");
		}
		
	}
	
	public void on(ServerAuthenticationEvent.TequilaStatusEvent event) {
		int status = event.getStatus();
		
		if (status == TEQUILA_OK) {
			requestSessionID();
		} else if (status == TEQUILA_WRONG_CREDITENTIAL) {
			this.error("Wrong username or password.");
		} else {
			this.error("Error "+status+" on Tequila Server.");
		}
	}
	
	public void on(ServerAuthenticationEvent.GettingSessionIDEvent event) {
		int status = event.getStatus();
		
		if (status == SWENG_OK) {
			try {
				String json = event.getSessionID();
				this.mSession = new JSONSession(json).getSession();
				
				this.emit(new AuthenticationEvent.AuthenticatedEvent(mSession));
			} catch (JSONException e) {
				this.error("Error: malformed JSON (session).");
			}
		} else {
			this.error("Error "+status+" on SwengServer"); 
		}
	}
}
