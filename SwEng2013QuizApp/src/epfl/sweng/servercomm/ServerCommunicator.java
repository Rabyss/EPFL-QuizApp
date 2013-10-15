package epfl.sweng.servercomm;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;

import epfl.sweng.events.EventEmitter;

import android.os.AsyncTask;

/**
 * 
 * Handles communication with the server.
 * 
 */
public final class ServerCommunicator extends EventEmitter {

	private static ServerCommunicator sInstance = null;
	public final static String SWENG_SERVER_URL = "https://sweng-quiz.appspot.com";
	public final static String SWENG_SUBMIT_QUESTION_URL = SWENG_SERVER_URL
			+ "/quizquestions/";
	public final static String SWENG_GET_RANDOM_QUESTION_URL = SWENG_SUBMIT_QUESTION_URL
			+ "random";

	private ServerCommunicator() {

	}

	public static synchronized ServerCommunicator getInstance() {
		if (sInstance == null) {
			sInstance = new ServerCommunicator();
		}
		return sInstance;
	}

	public void doHttpGet(RequestContext reqContext, ServerEvent event) {
		assert !reqContext.getServerURL().equals("")
				&& reqContext.getServerURL() != null : "No URL found !";
		assert event != null : "Event is null";
		// Creates an asynchronous task to send a GET request.
		new GetTask(event).execute(reqContext);
	}

	public void doHttpPost(RequestContext reqContext, ServerEvent event) {
		assert !reqContext.getServerURL().equals("")
				&& reqContext.getServerURL() != null : "No URL found !";
		assert reqContext.getEntity() != null : "No HttpEntity found !";
		assert !reqContext.getHeaders().isEmpty() : "No Header found !";
		assert event != null : "Event is null";
		// Creates an asynchronous task to send a POST request.
		new PostTask(event).execute(reqContext);
	}

	/**
	 * 
	 * Asynchronous task used to send POST requests.
	 * 
	 */
	private final class PostTask extends
			AsyncTask<RequestContext, Void, String> {
		private ServerEvent mEvent;

		public PostTask(ServerEvent event) {
			mEvent = event;
		}

		@Override
		protected String doInBackground(RequestContext... params) {
			RequestContext reqContext = params[0];
			HttpPost post = new HttpPost(reqContext.getServerURL());
			// Gets an iterator to iterate over each header
			Iterator<Entry<String, String>> headersIterator = params[0]
					.getHeaders().entrySet().iterator();
			while (headersIterator.hasNext()) {
				Map.Entry<String, String> header = headersIterator.next();
				post.setHeader(header.getKey(), header.getValue());
				headersIterator.remove();
			}
			ResponseHandler<String> handler = new BasicResponseHandler();
			try {
				post.setEntity(reqContext.getEntity());
				return SwengHttpClientFactory.getInstance().execute(post,
						handler);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			mEvent.setResponse(result);
			ServerCommunicator.getInstance().emit(mEvent);
		}
	}

	/**
	 * 
	 * Asynchronous task used to send GET requests.
	 * 
	 */
	private final class GetTask extends AsyncTask<RequestContext, Void, String> {
		private ServerEvent mEvent;

		public GetTask(ServerEvent event) {
			mEvent = event;
		}

		@Override
		protected String doInBackground(RequestContext... params) {
			RequestContext reqContext = params[0];
			// Construct the request
			HttpGet questionFetchRequest = new HttpGet(
					reqContext.getServerURL());
			ResponseHandler<String> questionFetchHandler = new BasicResponseHandler();

			try {
				return SwengHttpClientFactory.getInstance().execute(
						questionFetchRequest, questionFetchHandler);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mEvent.setResponse(result);
			ServerCommunicator.getInstance().emit(mEvent);
		}
	}

}