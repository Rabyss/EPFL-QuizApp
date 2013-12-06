package epfl.sweng.context;

import android.util.Log;
import epfl.sweng.context.conn_states.ConnectionState;
import epfl.sweng.context.conn_states.IdleOnlineConnectionState;
import epfl.sweng.events.EventListener;


public final class ConnectionStateMachine implements EventListener {
    private static final ConnectionState STARTING_STATE = new IdleOnlineConnectionState();

    private static final String TAG = "ConnectionStateMachine";
    
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
            System.out.println("On " + event.getType() + " switched to " + mCurrentState);
        } catch (ConnectionState.UnknownTransitionException e) {
            Log.d(TAG, "Unknown State Transition");
        }
    }

    public ConnectionState getCurrentConnectionState() {
        return mCurrentState;
    }

    public void reset() {
        mCurrentState = STARTING_STATE;
    }
}
