package epfl.sweng.proxy;

import epfl.sweng.events.Event;

public abstract class ConnectedEvent extends Event {

	private static final long serialVersionUID = -7735029679304640644L;

	/**
	 * Event when the mode changes from online to offline
	 *
	 */
	public static class OfflineEvent extends ConnectedEvent{

		private static final long serialVersionUID = 2087429744645919983L;
		
	}
	/**
	 * Event when the mode changes from offline to online
	 *
	 */
	public static class OnlineEvent extends ConnectedEvent{

		private static final long serialVersionUID = -710510124592475349L;
		
	}
}
