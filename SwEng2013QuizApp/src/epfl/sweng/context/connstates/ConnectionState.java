package epfl.sweng.context.connstates;

import epfl.sweng.context.AppContext;
import epfl.sweng.context.ConnectionEvent;

public abstract class ConnectionState {
    public abstract ConnectionState getNextState(ConnectionEvent event) throws UnknownTransitionException;

    public abstract boolean isOnline();

    public class UnknownTransitionException extends Exception {
        private static final long serialVersionUID = 401364793246178455L;

        public UnknownTransitionException(ConnectionState connectionState,
                ConnectionEvent event) {
            super("Unknown transition when in state " + connectionState
                    + " and get event " + event.getType() + ".");
        }

        public UnknownTransitionException(ConnectionEvent event) {
            this(AppContext.getContext().getCurrentConnectionState(), event);
        }
    }
}
