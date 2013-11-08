package epfl.sweng.proxy;

import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerEvent;

public interface IServer {
	//public static IServer getInstance();

	void doHttpGet(RequestContext reqContext, ServerEvent event);
	void doHttpPost(RequestContext reqContext, ServerEvent event);
	
	
}
