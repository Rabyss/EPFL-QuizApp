package epfl.sweng.context;

import epfl.sweng.context.conn_states.ConnectionState;
import epfl.sweng.context.conn_states.IdleOnlineConnectionState;
import epfl.sweng.events.EventListener;


public final class ConnectionStateMachine implements EventListener {
    private static final ConnectionState STARTING_STATE = new IdleOnlineConnectionState();

    private ConnectionState mCurrentState;

    public ConnectionStateMachine() {
        mCurrentState = STARTING_STATE;
    }

    public boolean isOnline() {
        return mCurrentState.isOnline();
    }

    public void on(ConnectionEvent event) {
        try {
            mCurrentState = mCurrentState.getNextState(event);
        } catch (ConnectionState.UnknownTransitionException e) {
            //TODO : think about this part
        }
    }

    public ConnectionState getCurrentConnectionState() {
        return mCurrentState;
    }

}
