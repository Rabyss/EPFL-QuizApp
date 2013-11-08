package epfl.sweng.context;

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

}
