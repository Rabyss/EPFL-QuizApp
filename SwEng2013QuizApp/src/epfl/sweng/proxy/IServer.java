package epfl.sweng.proxy;

import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerEvent;

public interface IServer {
	//public static IServer getInstance();

	void doHttpGet(RequestContext reqContext, ServerEvent event);
	void doHttpPost(RequestContext reqContext, ServerEvent event);
	void addListener(EventListener listener);
    void removeListener(EventListener listener);
	
}
