package epfl.sweng.servercomm;

import epfl.sweng.events.Event;

public abstract class ServerEvent extends Event {
    private static final long serialVersionUID = -2895588419411857521L;
    private ServerResponse mResponse;
    
    public ServerEvent() {
        super();
    }
    
    public ServerEvent(ServerResponse response) {
        super();
        this.mResponse = response;
    }

    public ServerResponse getResponse() {
        return mResponse;
    }

    public void setResponse(ServerResponse response) {
        this.mResponse = response;
    }
}
