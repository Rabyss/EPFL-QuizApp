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

/**
 * Proxy for ServerCommunicator class <br/>
 * Handle offline mode with questions caching <br/>
 * Implements Singleton Pattern <br/>
 * Listen to: ServerCommunicator to handle callbacks. <br/>
 * Emits events to AppContext to control state machine
 */
public final class Proxy extends EventEmitter implements IServer, EventListener {

	private final static int HTTP_ERROR_THRESHOLD = 500;
	private final static int HTTP_ERROR_INTERMEDIATE_THRESHOLD = 400;
	
	/** Singleton Instance */
	private static Proxy sInstance = null;
	
	/** ServerCommunicator for delegation */ 
	private final IServer serverComm;
	
	/** Cache for questions to be submitted next time online */
	private ArrayList<QuestionToSubmit> postQuestion;
	
	/** Cache for retrieving questions while offline */ 
	private ArrayList<ServerResponse> getQuestion;
	
	/** Temporary for a question we tried to submit online but IOException */
	private QuestionToSubmit questionToSubmit;

	private Proxy() {
		serverComm = ServerCommunicator.getInstance();
		postQuestion = new ArrayList<Proxy.QuestionToSubmit>();
		getQuestion = new ArrayList<ServerResponse>();
		serverComm.addListener(this);
		AppContext.getContext().addAsListener(this);
	}

	/**
	 * Get the singleton instance of Proxy
	 * 
	 * @return the singleton instance
	 */
	public static synchronized Proxy getInstance() {
		if (sInstance == null) {
			sInstance = new Proxy();
		}
		return sInstance;
	}

	@Override
	public void doHttpGet(RequestContext reqContext, ServerEvent event) {

		if (isOnline()) {
			// state machine transition
			this.emit(new ConnectionEvent(
					ConnectionEventType.ADD_OR_RETRIEVE_QUESTION));
			// Continue in on(ReceivedQuestionEvent) if server reachable
			// else (IOException) continue in on(GetConnectionErrorEvent)
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
			RequestContext reqContext = postQuestion.get(0).getReqContext();
			ServerEvent postEvent = postQuestion.get(0).getEvent();
			postQuestion.remove(0);
			doHttpPost(reqContext, postEvent);
		} else {

			this.emit(new ConnectionEvent(
					ConnectionEventType.COMMUNICATION_SUCCESS));

		}

	}

	public void on(ReceivedQuestionEvent event) {
		// data cannot be null because server answered something
		ServerResponse data = event.getResponse();
		
		
		if (data.getStatusCode() <= HTTP_ERROR_THRESHOLD) {
			if (data.getStatusCode() >= HTTP_ERROR_INTERMEDIATE_THRESHOLD) {

				event = new ReceivedQuestionEvent();
				event.setResponse(null);

				this.emit(new ConnectionEvent(
						ConnectionEventType.COMMUNICATION_SUCCESS));
			} else {
				getQuestion.add(data);
				this.emit(new ConnectionEvent(
						ConnectionEventType.COMMUNICATION_SUCCESS));
			}
		
		// Server answered >= 500 status, go to offline mode
		} else {
			this.on(new GetConnectionErrorEvent());
		}
		
		this.emit(event);
	}

	/**
	 * Server unreachable or answered >= 500 status code
	 * @param event
	 */
	public void on(GetConnectionErrorEvent event) {
		// transition for state machine
		this.emit(new ConnectionEvent(ConnectionEventType.COMMUNICATION_ERROR));
		ReceivedQuestionWithError receiveEvent = new ReceivedQuestionWithError();
		receiveEvent.setResponse(offlineRandomQuestion());
		this.emit(receiveEvent);
	}

	public void on(PostedQuestionEvent event) {
		ServerResponse data = event.getResponse();
		if (data != null && data.getStatusCode() < HTTP_ERROR_THRESHOLD) {
			if (data.getStatusCode() >= HTTP_ERROR_INTERMEDIATE_THRESHOLD) {
				event.setResponse(null);
			} else {
				ServerResponse serverResponse = new ServerResponse(
						data.getEntity(), data.getStatusCode());
				getQuestion.add(serverResponse);
			}
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
		//TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		this.emit(new ConnectionEvent(ConnectionEventType.COMMUNICATION_ERROR));
		this.emit(event);
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
