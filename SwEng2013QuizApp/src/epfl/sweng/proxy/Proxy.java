package epfl.sweng.proxy;

import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpStatus;

import epfl.sweng.context.AppContext;
import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.context.ConnectionEvent.ConnectionEventType;
import epfl.sweng.editquestions.PostedQuestionEvent;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.servercomm.RequestContext;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.servercomm.ServerEvent;
import epfl.sweng.servercomm.ServerResponse;
import epfl.sweng.showquestions.ReceivedQuestionEvent;
import epfl.sweng.showquestions.ReceivedQuestionWithError;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public final class Proxy extends EventEmitter implements IServer, EventListener {
	public final static String SWENG_SERVER_URL = "https://sweng-quiz.appspot.com";
	public final static String SWENG_SUBMIT_QUESTION_URL = SWENG_SERVER_URL
			+ "/quizquestions/";
	public final static String SWENG_GET_RANDOM_QUESTION_URL = SWENG_SUBMIT_QUESTION_URL
			+ "random";

	private final static int HTTP_ERROR_THRESHOLD = 400;
	private ServerCommunicator serverComm;
	private ArrayList<QuestionToSubmit> postQuestion;
	private ArrayList<ServerResponse> getQuestion;
	private static Proxy sInstance = null;
	private QuestionToSubmit questionToSubmit;
//	private boolean offlineToOnline = false;

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

		if (isOnline()) {
			this.emit(new ConnectionEvent(
					ConnectionEventType.ADD_OR_RETRIEVE_QUESTION));
			serverComm.doHttpGet(reqContext, event);
		} else {
			ReceivedQuestionEvent receiveEvent = new ReceivedQuestionEvent();
			receiveEvent.setResponse(offlineRandomQuestion());
			this.emit(receiveEvent);
		}

	}

	@Override
	public void doHttpPost(RequestContext reqContext, ServerEvent event) {
		questionToSubmit = new QuestionToSubmit(reqContext, event);
		if (isOnline()) {
			this.emit(new ConnectionEvent(
					ConnectionEventType.ADD_OR_RETRIEVE_QUESTION));
			reqContext.getEntity().toString();
			serverComm.doHttpPost(reqContext, event);
		} else {
			postQuestion.add(questionToSubmit);
			// TODO Send other status code to display different toast ?
			PostedQuestionEvent pqe = new PostedQuestionEvent();
			pqe.setResponse(new ServerResponse(questionToSubmit.reqContext
					.getEntity().toString(), HttpStatus.SC_OK));
			this.emit(pqe);
		}

	}

	public boolean isOnline() {
		return AppContext.getContext().isOnline();
	}

	private ServerResponse offlineRandomQuestion() {
		Random r = new Random();
		if (getQuestion.isEmpty()) {
			// TODO retourn�� un ServerResponse diff��rent?
			return new ServerResponse(null, HttpStatus.SC_NOT_FOUND);
		} else {
			return getQuestion.get(r.nextInt(getQuestion.size()));
		}
	}

	public void on(OnlineEvent event) {
		if (!postQuestion.isEmpty()) {
//			offlineToOnline = true;
			RequestContext reqContext = postQuestion.get(0).getReqContext();
			ServerEvent postEvent = postQuestion.get(0).getEvent();
			postQuestion.remove(0);
			doHttpPost(reqContext, postEvent);
		} else {
//			if (offlineToOnline) {
//				offlineToOnline = false;
//			TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_DISABLED);
			this.emit(new ConnectionEvent(
						ConnectionEventType.COMMUNICATION_SUCCESS));
//			}
		}

	}

	public void on(ReceivedQuestionEvent event) {
		ServerResponse data = event.getResponse();
		if (data != null && data.getStatusCode() < HTTP_ERROR_THRESHOLD) {

			getQuestion.add(data);
			this.emit(new ConnectionEvent(
					ConnectionEventType.COMMUNICATION_SUCCESS));
		} else {
			this.on(new GetConnectionErrorEvent());
		}
		this.emit(event);
	}

	public void on(PostedQuestionEvent event) {
		ServerResponse data = event.getResponse();
		if (data != null && data.getStatusCode() < HTTP_ERROR_THRESHOLD) {
			ServerResponse serverResponse = new ServerResponse(
					data.getEntity(), data.getStatusCode());
			getQuestion.add(serverResponse);
			this.emit(new ConnectionEvent(
					ConnectionEventType.COMMUNICATION_SUCCESS));
			this.emit(event);
			// post other cached questions that wait to be posted
			on(new OnlineEvent());

		} else {

			this.on(new PostConnectionErrorEvent());

		}

	}

	public void on(PostConnectionErrorEvent event) {
		postQuestion.add(0, questionToSubmit);
		TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		this.emit(new ConnectionEvent(ConnectionEventType.COMMUNICATION_ERROR));
		this.emit(event);
	}

	public void on(GetConnectionErrorEvent event) {
		this.emit(new ConnectionEvent(ConnectionEventType.COMMUNICATION_ERROR));
		ReceivedQuestionWithError receiveEvent = new ReceivedQuestionWithError();
		receiveEvent.setResponse(offlineRandomQuestion());
		this.emit(receiveEvent);
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
