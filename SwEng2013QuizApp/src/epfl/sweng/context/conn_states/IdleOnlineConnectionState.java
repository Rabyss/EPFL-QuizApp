package epfl.sweng.context.conn_states;

import epfl.sweng.context.ConnectionEvent;

public class IdleOnlineConnectionState extends ConnectionState {

    @Override
    public ConnectionState getNextState(ConnectionEvent event) throws UnknownTransitionException {
        switch (event.getType()) {
            case OFFLINE_CHECKBOX_CLICKED:
            	
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
