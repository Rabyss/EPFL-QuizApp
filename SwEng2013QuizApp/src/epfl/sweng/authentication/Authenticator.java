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
	private String username;
	private String password;
	private String token;
	private String session;
	
	public Authenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void authenticate() {
		requestToken();
	}
	
	private void requestToken() {
		ServerCommunicator.getInstance().addListener(this);
		RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/login");
		ServerCommunicator.getInstance().doHttpGet(req, new ServerAuthenticationEvent.GettingTokenEvent());
	}
	
	private void tequilaAuth() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("requestkey", this.token));
		params.add(new BasicNameValuePair("username", this.username));
		params.add(new BasicNameValuePair("password", this.password));
		
		try {
			UrlEncodedFormEntity entity;
			entity = new UrlEncodedFormEntity(params);
			RequestContext req = new RequestContext("https://tequila.epfl.ch/cgi-bin/tequila/login", entity);
			ServerCommunicator.getInstance().doHttpPost(req, new ServerAuthenticationEvent.TequilaStatusEvent());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void requestSessionID() {
		String json = "{\"token\": \""+this.token+"\"}";
		
		StringEntity entity;
		try {
			entity = new StringEntity(json);
			RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/login", entity);
			ServerCommunicator.getInstance().doHttpPost(req, new ServerAuthenticationEvent.GettingSessionIDEvent());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void on(ServerAuthenticationEvent.GettingTokenEvent event) {
		System.err.println("GettingToken"); // TODO REMOVE ME
		try {
			String json = event.getResponse();
			this.token = new JSONToken(json).getToken();
			
			tequilaAuth();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void on(ServerAuthenticationEvent.TequilaStatusEvent event) {
		System.err.println("TequilaStatus"); // TODO REMOVE ME
		// TODO if (event.getStatus().equals(200 OK)) {
		requestSessionID();
		//} else {
			// TODO Not OK case
		// }
	}
	
	public void on(ServerAuthenticationEvent.GettingSessionIDEvent event) {
		System.err.println("GettingSessionID"); // TODO REMOVE ME
		try {
			String json = event.getResponse();
			this.session = new JSONSession(json).getSession();
			
			this.emit(new AuthenticationEvent.AuthenticatedEvent(session));
			// TODO Do something neat :3
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
