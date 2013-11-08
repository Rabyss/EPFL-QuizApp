package epfl.sweng.proxy;

import java.util.HashMap;

import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerEvent;

public class Proxy implements IServer, EventListener {
	private boolean isOnline= true;
	private ServerCommunicator serverComm= ServerCommunicator.getInstance();
	private HashMap<RequestContext, ServerEvent> postQuestion;
	private HashMap<RequestContext, ServerEvent> getQuestion;
	
	@Override
	public void doHttpGet(RequestContext reqContext, ServerEvent event) {
		if (isOnline) {
			serverComm.doHttpGet(reqContext, event);
		} else {
			getQuestion.put(reqContext, event);
		}
		
	}

	@Override
	public void doHttpPost(RequestContext reqContext, ServerEvent event) {
		if (isOnline) {
			serverComm.doHttpPost(reqContext, event);
		} else {
			postQuestion.put(reqContext, event);
		}
		
	}
	public void on(ConnectedEvent.OfflineEvent event) {
		
	}
	public void on(ConnectedEvent.OnlineEvent event) {
		
	}
	

}
