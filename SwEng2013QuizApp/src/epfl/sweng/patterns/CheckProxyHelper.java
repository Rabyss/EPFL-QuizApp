package epfl.sweng.patterns;

import android.net.Proxy;
import epfl.sweng.servercomm.ServerCommunicator;

public class CheckProxyHelper implements ICheckProxyHelper {

	
	public Class<?> getServerCommunicationClass() {
		return ServerCommunicator.class;
	}
	
	public Class<?> getProxyClass() {
		return Proxy.class;
	}
}
