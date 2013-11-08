package epfl.sweng.context;

import epfl.sweng.events.Event;


public final class ConnectionEvent extends Event {

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
