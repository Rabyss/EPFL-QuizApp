package epfl.sweng.proxy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import epfl.sweng.cache.SQLiteCache;
import epfl.sweng.context.AppContext;
import epfl.sweng.context.ConnectionEvent;
import epfl.sweng.context.ConnectionEvent.ConnectionEventType;
import epfl.sweng.context.conn_states.ServerSyncConnectionState;
import epfl.sweng.editquestions.PostedQuestionEvent;
import epfl.sweng.entry.SwitchSuccessfulEvent;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.quizquestions.MalformedQuestionException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.QueryParser.QueryParserResult;
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
	private SQLiteCache cache;

	/** Temporary for a question we tried to submit online but IOException */
	private QuestionToSubmit questionToSubmit;

	private ProxyState state = ProxyState.NORMAL;
	private QueryParserResult query;

	private ArrayList<ServerResponse> results;
	private String next;

	private Proxy(Context context) {
		serverComm = ServerCommunicator.getInstance();
		postQuestion = new ArrayList<Proxy.QuestionToSubmit>();
		serverComm.addListener(this);
		AppContext.getContext().addAsListener(this);
		cache = new SQLiteCache(context);
	}

	/**
	 * Get the singleton instance of Proxy
	 * 
	 * @return the singleton instance
	 */
	public static synchronized Proxy getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Proxy(context);
		}
		return sInstance;
	}

	@Override
	public void doHttpGet(RequestContext reqContext, ServerEvent event) {
		if (isOnline()) {
			if (state == ProxyState.SEARCH) {
				reqContext
						.setServerURL("https://sweng-quiz.appspot.com/search");
				reqContext.addHeader("Content-type", "application/json");
				StringEntity queryEntity = null;
				try {
					queryEntity = new StringEntity("{ \"query\": \""
							+ query.getQueryString() + "\" }");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				reqContext.setEntity(queryEntity);
				this.emit(new ConnectionEvent(
						ConnectionEventType.ADD_OR_RETRIEVE_QUESTION));
				serverComm.doHttpPost(reqContext, event);
			} else if (state == ProxyState.NEXT) {
				if (results.isEmpty()) {
				} else {
					ReceivedQuestionEvent receiveEvent = new ReceivedQuestionEvent();
					receiveEvent.setResponse(results.get(0));
					results.remove(0);
				}
			} else {
				// state machine transition
				this.emit(new ConnectionEvent(
						ConnectionEventType.ADD_OR_RETRIEVE_QUESTION));
				// Continue in on(ReceivedQuestionEvent) if server reachable
				// else (IOException) continue in on(GetConnectionErrorEvent)
				serverComm.doHttpGet(reqContext, event);
			}
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

	public void giveQuery(QueryParserResult query) {
		state = ProxyState.SEARCH;
		this.query = query;
	}

	public boolean isOnline() {
		return AppContext.getContext().isOnline();
	}

	private ServerResponse offlineRandomQuestion() {
		QuizQuestion question = cache.getRandomQuestion();

		if (question == null) {
			return new ServerResponse(null, HttpStatus.SC_NOT_FOUND);
		} else {
			ServerResponse response = null;
			try {
				response = new ServerResponse(question.toJSON(),
						HttpStatus.SC_OK);
			} catch (MalformedQuestionException e) {
				throw new RuntimeException(
						"You cached an unvalid question you fool !");
			}

			return response;
		}
	}

	public void on(OnlineEvent event) {
		if (!postQuestion.isEmpty()) {
			RequestContext reqContext = postQuestion.get(0).getReqContext();
			ServerEvent postEvent = postQuestion.get(0).getEvent();
			postQuestion.remove(0);
			doHttpPost(reqContext, postEvent);
		} else {
			this.emit(new SwitchSuccessfulEvent());
			this.emit(new ConnectionEvent(
					ConnectionEventType.COMMUNICATION_SUCCESS));

		}

	}

	public void on(ReceivedQuestionEvent event) {
		// data cannot be null because server answered something
		ServerResponse data = event.getResponse();

		if (data.getStatusCode() < HTTP_ERROR_THRESHOLD) {
			if (data.getStatusCode() >= HTTP_ERROR_INTERMEDIATE_THRESHOLD) {

				event = new ReceivedQuestionEvent();
				event.setResponse(null);

				this.emit(new ConnectionEvent(
						ConnectionEventType.COMMUNICATION_SUCCESS));
			} else {
				String json = "";
				if (state == ProxyState.SEARCH) {
					try {
						JSONObject o = new JSONObject(data.getEntity()
								.toString());
						JSONArray array = o.getJSONArray("questions");
						json = array.getJSONObject(0).toString();
						for (int i = 1; i < array.length(); i++) {
							results.add(new ServerResponse(array.getJSONObject(
									i).toString(), HttpStatus.SC_OK));
						}
						next = o.getString("next");
					} catch (JSONException e) {
						next = "";
					}
					if (next.equals("") || next == null || next.equals("null")) {
						state = ProxyState.NORMAL;
					} else {
						state = ProxyState.NEXT;
					}
				} else {
					json = data.getEntity().toString();
				}
				QuizQuestion quizQuestion = null;
				try {
					quizQuestion = new QuizQuestion(json);
				} catch (JSONException e) {
					throw new RuntimeException(
							"You are trying to cache an unvalid question you fool !");
				}

				cache.cacheQuestion(quizQuestion);

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
	 * 
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
		if (data.getStatusCode() < HTTP_ERROR_THRESHOLD) {
			if (data.getStatusCode() >= HTTP_ERROR_INTERMEDIATE_THRESHOLD) {
				event.setResponse(null);
			} else {
				QuizQuestion question = null;
				try {
					question = new QuizQuestion(data.getEntity());
				} catch (JSONException e) {
					throw new RuntimeException(
							"You are trying to cache an unvalid question you fool !");
				}

				cache.cacheQuestion(question);
			}

			this.emit(event);
			// post other cached questions that wait to be posted
			if (AppContext.getContext().getCurrentConnectionState().getClass() == ServerSyncConnectionState.class) {
				on(new OnlineEvent());
			} else {
				this.emit(new ConnectionEvent(
						ConnectionEventType.COMMUNICATION_SUCCESS));
			}

		} else {

			this.on(new PostConnectionErrorEvent());

		}

	}

	public void on(PostConnectionErrorEvent event) {
		postQuestion.add(0, questionToSubmit);
		// TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
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

	private enum ProxyState {
		NORMAL, SEARCH, NEXT
	}
}
