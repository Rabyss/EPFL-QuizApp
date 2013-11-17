package epfl.sweng.context.conn_states;

import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.testing.TestCoordinator;


public class ServerSyncConnectionState extends ConnectionState {
    @Override
    public ConnectionState getNextState(ConnectionEvent event) throws UnknownTransitionException {
        switch (event.getType()) {
            case COMMUNICATION_ERROR:
                TestCoordinator.check(TestCoordinator.TTChecks.OFFLINE_CHECKBOX_ENABLED);
                return new IdleOfflineConnectionState();
            case COMMUNICATION_SUCCESS:
                TestCoordinator.check(TestCoordinator.TTChecks.OFFLINE_CHECKBOX_DISABLED);
                return new IdleOnlineConnectionState();
            default:
                throw new UnknownTransitionException(event);
        }
    }

    @Override
    public boolean isOnline() {
        return true;
    }
}
