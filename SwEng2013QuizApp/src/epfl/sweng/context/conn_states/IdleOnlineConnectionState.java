package epfl.sweng.context.conn_states;

import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.testing.TestCoordinator;

public class IdleOnlineConnectionState extends ConnectionState {

    @Override
    public ConnectionState getNextState(ConnectionEvent event) throws UnknownTransitionException {
        switch (event.getType()) {
            case OFFLINE_CHECKBOX_CLICKED:
                TestCoordinator.check(TestCoordinator.TTChecks.OFFLINE_CHECKBOX_ENABLED);
                return new IdleOfflineConnectionState();
            case ADD_OR_RETRIEVE_QUESTION:
                return new ServerInCommunicationConnectionState();
            default:
                throw new UnknownTransitionException(event);
        }
    }

    @Override
    public boolean isOnline() {
        return true;
    }
}
