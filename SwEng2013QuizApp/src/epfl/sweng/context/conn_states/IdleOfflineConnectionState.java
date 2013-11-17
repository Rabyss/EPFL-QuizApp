package epfl.sweng.context.conn_states;

import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class IdleOfflineConnectionState extends ConnectionState {
    @Override
    public ConnectionState getNextState(ConnectionEvent event) throws UnknownTransitionException {
        switch (event.getType()) {
            case ADD_OR_RETRIEVE_QUESTION:
                return new IdleOfflineConnectionState();
            case OFFLINE_CHECKBOX_CLICKED:
                return new ServerSyncConnectionState();
            default:
                throw new UnknownTransitionException(event);
        }
    }

    @Override
    public boolean isOnline() {
        return false;
    }
}
