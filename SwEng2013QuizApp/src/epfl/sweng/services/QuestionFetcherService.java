package epfl.sweng.services;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import epfl.sweng.context.AppContext;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerResponse;
import epfl.sweng.showquestions.ReceivedQuestionEvent;

public class QuestionFetcherService extends EventEmitter implements Service,
        EventListener {

    public QuestionFetcherService(EventListener listener) {
        addListener(listener);
        Proxy.getInstance().addListener(this);
    }

    @Override
    public void execute() {
        RequestContext reqContext = new RequestContext();
        reqContext.addHeader("Authorization", "Tequila "
                + AppContext.getContext().getSessionID());
        reqContext
                .setServerURL(ServerCommunicator.SWENG_GET_RANDOM_QUESTION_URL);
        Proxy.getInstance().doHttpGet(reqContext, new ReceivedQuestionEvent());
    }

    public void on(ReceivedQuestionEvent event) {
        ServerResponse response = event.getResponse();
        int status = response.getStatusCode();
        if (status == HttpStatus.SC_NOT_FOUND) {
            this.emit(new NothingInCacheEvent());
        } else if (status >= HttpStatus.SC_BAD_REQUEST) {
            this.emit(new ConnexionErrorEvent());
        } else {
            QuizQuestion quizQuestion = null;
            try {
                quizQuestion = new QuizQuestion(response.getEntity().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.emit(new ShowQuestionEvent(quizQuestion));
        }
    }

}
