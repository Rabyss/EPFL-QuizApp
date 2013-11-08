package epfl.sweng.context;

import epfl.sweng.events.Event;


public final class ConnectionEvent extends Event {

	private static final long serialVersionUID = 4608066880261180790L;
	private final Type mType;

    public ConnectionEvent(Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {
        NONE, ADD_OR_RETRIEVE_QUESTION, COMMUNICATION_SUCCESS, COMMUNICATION_ERROR, OFFLINE_CHECKBOX_CLICKED;
    }
}
