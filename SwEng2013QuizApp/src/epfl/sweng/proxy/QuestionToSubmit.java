package epfl.sweng.proxy;

import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerEvent;

import java.io.Serializable;

public class QuestionToSubmit implements Serializable {
    private static final long serialVersionUID = 6288410483133964979L;
    private RequestContext reqContext;
    private ServerEvent event;

    public QuestionToSubmit(RequestContext reqContext, ServerEvent event) {
        this.reqContext = reqContext;
        this.event = event;
    }

    public ServerEvent getEvent() {
        return event;
    }

    public RequestContext getReqContext() {
        return reqContext;
    }
}