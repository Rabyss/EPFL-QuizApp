package epfl.sweng.proxy;

import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import epfl.sweng.context.AppContext;
import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.context.ConnectionEvent.Type;
import epfl.sweng.editquestions.PostedQuestionEvent;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerEvent;
import epfl.sweng.servercomm.ServerResponse;
import epfl.sweng.showquestions.ReceivedQuestionEvent;

public final class Proxy extends EventEmitter implements IServer, EventListener {
	private final static int HTTP_ERROR_THRESHOLD = 400;

	private ServerCommunicator serverComm;
	private ArrayList<QuestionToSubmit> postQuestion;
	private ArrayList<ServerResponse> getQuestion;
	private static Proxy sInstance = null;
	private String jsonQuestion;

	private Proxy() {
		serverComm = ServerCommunicator.getInstance();
		postQuestion = new ArrayList<Proxy.QuestionToSubmit>();
		getQuestion = new ArrayList<ServerResponse>();
		serverComm.addListener(this);
		AppContext.getContext().addAsListener(this);
	}

	public static synchronized Proxy getInstance() {
		if (sInstance == null) {
			sInstance = new Proxy();
		}
		return sInstance;
	}

	@Override
	public void doHttpGet(RequestContext reqContext, ServerEvent event) {
		this.emit(new ConnectionEvent(Type.ADD_OR_RETRIEVE_QUESTION));
		if (isOnline()) {
			serverComm.doHttpGet(reqContext, event);
		} else {
			ServerResponse response = offlineRandomQuestion();
			event.setResponse(response);
			this.emit(event);
		}

	}

	@Override
	public void doHttpPost(RequestContext reqContext, ServerEvent event) {
		if (isOnline()) {
			jsonQuestion = reqContext.getEntity().toString();
			serverComm.doHttpPost(reqContext, event);

			// TODO stocké les submit questions dans le alreadyPostQuestion
		} else {
			postQuestion.add(new QuestionToSubmit(reqContext, event));
			// TODO stocké les submit dans un array qu'il faudra ensuite submit
		}

	}

	public boolean isOnline() {
		return AppContext.getContext().isOnline();
	}

	private ServerResponse offlineRandomQuestion() {
		Random r = new Random();
		if (getQuestion.isEmpty()) {
			// TODO retourné un ServerResponse différent?
			return new ServerResponse(null, HttpStatus.SC_NOT_FOUND);
		} else {
			return getQuestion.get(r.nextInt(getQuestion.size()));
		}
	}

	// public void on(ConnectedEvent.OfflineEvent event) {
	//
	// }
	// public void on(ConnectedEvent.OnlineEvent event) {
	//
	// }
	public void on(ReceivedQuestionEvent event) {
		ServerResponse data = event.getResponse();
		if (data != null && data.getStatusCode() < HTTP_ERROR_THRESHOLD) {
			getQuestion.add(data);
			this.emit(new ConnectionEvent(Type.COMMUNICATION_SUCCESS));
		} else {
			this.emit(new ConnectionEvent(Type.COMMUNICATION_ERROR));
		}
		this.emit(event);
	}

	public void on(PostedQuestionEvent event) {
		ServerResponse data = event.getResponse();
		if (data != null && data.getStatusCode() < HTTP_ERROR_THRESHOLD) {

		} else {

		}
	}

	private class QuestionToSubmit {
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
}
