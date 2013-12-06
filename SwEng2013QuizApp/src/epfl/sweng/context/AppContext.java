package epfl.sweng.context;

import epfl.sweng.context.connstates.ConnectionState;
import epfl.sweng.events.EventEmitter;

import java.util.Collection;

/**
 * Singleton used to carry the global state of the program at a given time.
 */
public enum AppContext {
    INSTANCE;


    private String mSessionID;
    private ConnectionStateMachine mConnectionStateMachine =  new ConnectionStateMachine();


    public static AppContext getContext() {
        return INSTANCE;
    }

    /**
     * Used when you need to change the ID of the session.
     * @param sessionID - the new ID of the session
     */
    public void setSessionID(String sessionID) {
        mSessionID = sessionID;
    }

    public String getSessionID() {
        return mSessionID;
    }

    public boolean isOnline() {
        return mConnectionStateMachine.isOnline();
    }

    public final ConnectionState getCurrentConnectionState() {
        return mConnectionStateMachine.getCurrentConnectionState();
    }

    // HELPERS TO DEAL WITH LISTENERS

    public void addAsListener(EventEmitter eventEmitter) {
        eventEmitter.addListener(mConnectionStateMachine);
    }

    public void addAsListeners(Collection<EventEmitter> eventEmitters) {
        for (EventEmitter eventEmitter : eventEmitters) {
            addAsListener(eventEmitter);
        }
    }

    public void removeAsListener(EventEmitter eventEmitter) {
        eventEmitter.removeListener(mConnectionStateMachine);
    }
    
    public void resetState() {
        mConnectionStateMachine.reset();
    }

}
