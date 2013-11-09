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
			try {
				this.emit(new ShowQuestionEvent(quizQuestion));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		removeListener(super.getActivity());
	}

	public void setActivity(ShowQuestionsActivity activity) {
		super.setActivity(activity);
	}
}
