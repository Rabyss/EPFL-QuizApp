package epfl.sweng.context;

import android.util.Log;
import epfl.sweng.context.connstates.ConnectionState;
import epfl.sweng.context.connstates.IdleOnlineConnectionState;
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
        } catch (ConnectionState.UnknownTransitionException e) {
            Log.d(TAG, "Unknown State Transition", e);
        }
    }

    public ConnectionState getCurrentConnectionState() {
        return mCurrentState;
    }

    public void reset() {
        mCurrentState = STARTING_STATE;
    }
}
