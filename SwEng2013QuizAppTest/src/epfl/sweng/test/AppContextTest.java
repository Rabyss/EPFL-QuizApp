package epfl.sweng.test;

import android.test.AndroidTestCase;
import epfl.sweng.context.AppContext;
import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.context.conn_states.ConnectionState;
import epfl.sweng.context.conn_states.IdleOfflineConnectionState;
import epfl.sweng.context.conn_states.IdleOnlineConnectionState;
import epfl.sweng.context.conn_states.ServerInCommunicationConnectionState;
import epfl.sweng.context.conn_states.ServerSyncConnectionState;
import epfl.sweng.events.EventEmitter;

public class AppContextTest extends AndroidTestCase {


    public void testSetAndGetSessionID() {
        String firstSID = "SID 1";
        String secSID = "SID 2";

        assertFalse("the test is not usefull", firstSID.equals(secSID));

        AppContext.getContext().setSessionID(firstSID);
        assertEquals("the setter for session ID does not work", AppContext.getContext().getSessionID(), firstSID);

        AppContext.getContext().setSessionID(secSID);
        assertEquals("the setter for session ID does not work", AppContext.getContext().getSessionID(), secSID);
    }

    public void testTransitions() {
        EventEmitter eventEmitter = new EventEmitter() {};
        AppContext.getContext().addAsListener(eventEmitter);

        eventEmitter.emit(new ConnectionEvent(ConnectionEvent.ConnectionEventType.OFFLINE_CHECKBOX_CLICKED));
        connectFromIdleOffline(eventEmitter);

        //must turn offline
        eventEmitter.emit(new ConnectionEvent(ConnectionEvent.ConnectionEventType.OFFLINE_CHECKBOX_CLICKED));
        assertEquals("must be in idle (offline) state", getCurrentConnectionState(), IdleOfflineConnectionState.class);

        connectFromIdleOffline(eventEmitter);

        eventEmitter.emit(new ConnectionEvent(ConnectionEvent.ConnectionEventType.ADD_OR_RETRIEVE_QUESTION));
        assertEquals("must be in server comm state", getCurrentConnectionState(),
        		ServerInCommunicationConnectionState.class);

        eventEmitter.emit(new ConnectionEvent(ConnectionEvent.ConnectionEventType.COMMUNICATION_ERROR));
        assertEquals("must be in idle (offline) state", getCurrentConnectionState(), IdleOfflineConnectionState.class);

        AppContext.getContext().removeAsListener(eventEmitter);
    }

    private void connectFromIdleOffline(EventEmitter eventEmitter) {
        assertEquals("state must be idle (offline)", getCurrentConnectionState(), IdleOfflineConnectionState.class);
        eventEmitter.emit(new ConnectionEvent(ConnectionEvent.ConnectionEventType.OFFLINE_CHECKBOX_CLICKED));
        assertEquals("state must be server sync", getCurrentConnectionState(), ServerSyncConnectionState.class);
        eventEmitter.emit(new ConnectionEvent(ConnectionEvent.ConnectionEventType.COMMUNICATION_SUCCESS));
        assertEquals("must be in idle (online) state", getCurrentConnectionState(), IdleOnlineConnectionState.class);
    }

    private Class<? extends ConnectionState> getCurrentConnectionState() {
    	return AppContext.getContext().getCurrentConnectionState().getClass();
    }
}
