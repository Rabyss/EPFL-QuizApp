package epfl.sweng.services;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import epfl.sweng.context.AppContext;
import epfl.sweng.events.EventListener;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerResponse;
import epfl.sweng.showquestions.ReceivedQuestionEvent;
import epfl.sweng.showquestions.ReceivedQuestionWithError;
import epfl.sweng.showquestions.ShowQuestionsActivity;

public class QuestionFetcherService extends QuestionActivityService implements
		Service, EventListener {

	public QuestionFetcherService(ShowQuestionsActivity activity) {
		super(activity);
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
		if (response == null) {
		    this.emit(new ClientErrorEvent());
		} else {
    		int status = response.getStatusCode();
    		if (status == HttpStatus.SC_NOT_FOUND) {
    			this.emit(new NothingInCacheEvent());
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
		removeListener(super.getActivity());
	}
	
	public void on(ReceivedQuestionWithError event) {
	    ServerResponse response = event.getResponse();
	    QuizQuestion quizQuestion = null;
        try {
            quizQuestion = new QuizQuestion(response.getEntity().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.emit(new ShowQuestionEvent(quizQuestion));
        this.emit(new ConnectionErrorEvent());
        removeListener(super.getActivity());
	}

	public void setActivity(ShowQuestionsActivity activity) {
		super.setActivity(activity);
	}
}
