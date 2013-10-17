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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void requestSessionID() {
		String json = "{\"token\": \""+this.mToken+"\"}";
		
		System.err.println(json);
		
		try {
			StringEntity entity;
			entity = new StringEntity(json);
			RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/login", entity);
            req.addHeader("Content-type", "application/json");
			ServerCommunicator.getInstance().doHttpPost(req, new ServerAuthenticationEvent.GettingSessionIDEvent());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void on(ServerAuthenticationEvent.GettingTokenEvent event) {
		try {
			String json = event.getResponse();
			this.mToken = new JSONToken(json).getToken();
			
			tequilaAuth();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void on(ServerAuthenticationEvent.TequilaStatusEvent event) {
		// TODO if (event.getStatus().equals(200 OK)) {
		requestSessionID();
		//} else {
			// TODO Not OK case
			// requestToken();
		// }
	}
	
	public void on(ServerAuthenticationEvent.GettingSessionIDEvent event) {
		// TODO Error => requestToken();
		System.err.println("on(GettingSessionIDEvent)");
		try {
			String json = event.getResponse();
			this.mSession = new JSONSession(json).getSession();
			
			ServerCommunicator.getInstance().removeListener(this);
			
			this.emit(new AuthenticationEvent.AuthenticatedEvent(mSession));
			// TODO Do something neat :3
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
