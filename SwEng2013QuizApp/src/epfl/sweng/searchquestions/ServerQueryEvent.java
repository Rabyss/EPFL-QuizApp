package epfl.sweng.searchquestions;

import epfl.sweng.servercomm.ServerEvent;

public class ServerQueryEvent extends ServerEvent {
    private static final long serialVersionUID = 5889028973636601866L;

    public ServerQueryEvent() {
    }
    
    public int getStatus() {
        return this.getResponse().getStatusCode();
    }
    
    public String getJSONQuestions() {
        return this.getResponse().getEntity();
    }
}
