package epfl.sweng.authentication;

import epfl.sweng.events.Event;

public abstract class AuthenticationEvent extends Event {
	private static final long serialVersionUID = -5936292490766850403L;

	public static class AuthenticatedEvent extends ServerAuthenticationEvent {
		private static final long serialVersionUID = 6215745042573035974L;
		
		private String sessionID;

		public AuthenticatedEvent(String sessionID) {
			this.sessionID = sessionID;
		}
		
		public String getSessionID() {
			return this.sessionID;
		}
	}
	
	public static class AuthenticationErrorEvent extends ServerAuthenticationEvent {
		private static final long serialVersionUID = -5567148060264321495L;
		
		private String error;

		public AuthenticationErrorEvent(String error) {
			this.error = error;
		}
		
		public String getError() {
			return this.error;
		}
	}
}
