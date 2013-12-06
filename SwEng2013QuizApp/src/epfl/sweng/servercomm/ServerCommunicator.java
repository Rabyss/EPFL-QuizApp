package epfl.sweng.servercomm;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpRequest;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.os.AsyncTask;
import android.util.Log;
import epfl.sweng.events.EventEmitter;
import epfl.sweng.proxy.GetConnectionErrorEvent;
import epfl.sweng.proxy.IServer;
import epfl.sweng.proxy.PostConnectionErrorEvent;

/**
 * Handles network communication with the app server. This class implements the
 * Singleton Pattern.
 * 
 * On doHttpGet and doHttpPost emits a ServerEvent (containing ServerResponse)
 * if the server was reachable and answered something. Otherwise emits a
 * Get(/Post)ConnectionErrorEvent if server was unreachable.
 */
public final class ServerCommunicator extends EventEmitter implements IServer {
    private static final String TAG = "ServerCommunicator";

    /**
     * Singleton instance
     */
    private static ServerCommunicator sInstance = null;

    /**
     * Base server URL
     */
    public final static String SWENG_SERVER_URL = "https://sweng-quiz.appspot.com";

    /**
     * Server URL for submitting a new question
     */
    public final static String SWENG_SUBMIT_QUESTION_URL = SWENG_SERVER_URL
            + "/quizquestions/";

    /**
     * Server URL for retrieving a random question
     */
    public final static String SWENG_GET_RANDOM_QUESTION_URL = SWENG_SUBMIT_QUESTION_URL
            + "random";

    /**
     * Get the singleton instance of ServerCommunicator
     * 
     * @return the singleton instance
     */
    public static synchronized ServerCommunicator getInstance() {
        if (sInstance == null) {
            sInstance = new ServerCommunicator();
        }
        return sInstance;
    }

    @Override
    public void doHttpGet(RequestContext reqContext, ServerEvent event) {
        assert !"".equals(reqContext.getServerURL())
                && null != reqContext.getServerURL() : "No URL found !";
        assert null != event : "Event is null";
        // Creates an asynchronous task to send a GET request.
        new GetTask(event).execute(reqContext);
    }

    public void doHttpPost(RequestContext reqContext, ServerEvent event) {
        assert !"".equals(reqContext.getServerURL())
                && null != reqContext.getServerURL() : "No URL found !";
        assert null != reqContext.getEntity() : "No HttpEntity found !";
        assert !reqContext.getHeaders().isEmpty() : "No Header found !";
        assert null != event : "Event is null";
        // Creates an asynchronous task to send a POST request.
        new PostTask(event).execute(reqContext);
    }

    private ServerCommunicator() {

    }

    private void exctractHeaders(HttpRequest request, RequestContext reqContext) {
        Iterator<Entry<String, String>> headersIterator = reqContext
                .getHeaders().entrySet().iterator();
        while (headersIterator.hasNext()) {
            Map.Entry<String, String> header = headersIterator.next();
            request.setHeader(header.getKey(), header.getValue());
            headersIterator.remove();
        }
    }

    /**
     * 
     * Asynchronous task used to send POST requests.
     * 
     */
    private final class PostTask extends
            AsyncTask<RequestContext, Void, ServerResponse> {
        private ServerEvent mEvent;

        /**
         * Create a PostTask with the callback event to call
         * 
         * @param event
         *            the callback event to call
         */
        public PostTask(ServerEvent event) {
            mEvent = event;
        }

        @Override
        protected ServerResponse doInBackground(RequestContext... params) {
            RequestContext reqContext = params[0];
            HttpPost post = new HttpPost(reqContext.getServerURL());
            exctractHeaders(post, reqContext);
            ResponseHandler<ServerResponse> handler = new CustomResponseHandler();
            try {
                post.setEntity(reqContext.getEntity());
                return SwengHttpClientFactory.getInstance().execute(post,
                        handler);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ServerResponse result) {
            super.onPostExecute(result);
            // Server responds something, i.e. server reachable
            if (result != null) {
                mEvent.setResponse(result);
                ServerCommunicator.getInstance().emit(mEvent);
                // In case Of IOException, i.e. Server unreachable
            } else {
                ServerCommunicator.getInstance().emit(
                        new PostConnectionErrorEvent());
            }
        }
    }

    /**
     * 
     * Asynchronous task used to send GET requests.
     * 
     */
    private final class GetTask extends
            AsyncTask<RequestContext, Void, ServerResponse> {
        private ServerEvent mEvent;

        public GetTask(ServerEvent event) {
            mEvent = event;
        }

        @Override
        protected ServerResponse doInBackground(RequestContext... params) {
            RequestContext reqContext = params[0];
            // Construct the request
            HttpGet get = new HttpGet(reqContext.getServerURL());
            ResponseHandler<ServerResponse> questionFetchHandler = new CustomResponseHandler();
            exctractHeaders(get, reqContext);
            try {
                return SwengHttpClientFactory.getInstance().execute(get,
                        questionFetchHandler);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ServerResponse result) {
            super.onPostExecute(result);
            // Server responds something, i.e. was reachable
            if (result != null) {
                mEvent.setResponse(result);
                ServerCommunicator.getInstance().emit(mEvent);
                // In case of IOException, i.e. server unreachable
            } else {
                ServerCommunicator.getInstance().emit(
                        new GetConnectionErrorEvent());
            }
        }
    }

}