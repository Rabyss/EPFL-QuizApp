package epfl.sweng.authentication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;

public class Authenticator implements EventListener {
	private String username;
	private String password;
	private String token;
	private String json;	
	
	public Authenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	private void requestToken() {
		ServerCommunicator.getInstance().addListener(this);
		RequestContext req = new RequestContext("https://sweng-quiz.appspot.com/login");
		ServerCommunicator.getInstance().doHttpGet(req, new AuthenticationEvent.GettingTokenEvent());
	}
	
	private void auth() {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("requestkey", this.token));
		params.add(new BasicNameValuePair("username", this.username));
		params.add(new BasicNameValuePair("password", this.password));
		
		try {
			UrlEncodedFormEntity entity;
			entity = new UrlEncodedFormEntity(params);
			RequestContext req = new RequestContext("https://tequila.epfl.ch/cgi-bin/tequila/login", entity);
			ServerCommunicator.getInstance().doHttpPost(req, new AuthenticationEvent.TequilaStatusEvent());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void on(AuthenticationEvent.GettingTokenEvent event) {
		try {
			this.json = event.getResponse();
			this.token = new JSONToken(this.json).getToken();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void on(AuthenticationEvent.TequilaStatusEvent event) {
		
	}
	
	public void on(AuthenticationEvent.GettingSessionIDEvent event) {
	}
	
}
