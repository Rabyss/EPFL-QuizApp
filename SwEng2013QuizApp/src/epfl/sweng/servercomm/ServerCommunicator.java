package epfl.sweng.servercomm;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import epfl.sweng.QuizQuestion;

/**
 * 
 * Handles communication with the server.
 * 
 * @author Jeremy Rabasco (jeremy.rabasco@epfl.ch), Philemon Favrod
 *         (philemon.favrod@epfl.ch)
 */
public final class ServerCommunicator {

    private static ServerCommunicator sInstance = null;
    private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

    private ServerCommunicator() {

    }

    public static synchronized ServerCommunicator getInstance() {
        if (sInstance == null) {
            sInstance = new ServerCommunicator();
        }
        return sInstance;
    }

    public QuizQuestion getRandomQuestion() 
            throws ClientProtocolException, IOException, JSONException 
    {

        // Construct the request
        HttpGet questionFetchRequest = new HttpGet(SERVER_URL
                + "/quizquestions/random");
        ResponseHandler<String> questionFetchHandler = new BasicResponseHandler();

        String strRandomQuestion;
        JSONObject jsonModel;
        strRandomQuestion = SwengHttpClientFactory.getInstance().execute(
                questionFetchRequest, questionFetchHandler);
        jsonModel = new JSONObject(strRandomQuestion);
        return new QuizQuestion(jsonModel);
    }

    public void submitQuizQuestion(QuizQuestion question)
    	throws InterruptedException, ExecutionException, AssertionError {

        AsyncTask<QuizQuestion, Void, String> submitTask = new AsyncTask<QuizQuestion, Void, String>() {

            @Override
            protected String doInBackground(QuizQuestion... params) {
                HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
                String response = "";
                try {
                    post.setEntity(new StringEntity(params[0].toJSON()));
                    post.setHeader("Content-type", "application/json");
                    ResponseHandler<String> handler = new BasicResponseHandler();

                    response = SwengHttpClientFactory.getInstance().execute(
                            post, handler);
                } catch (ClientProtocolException e) {
                    response = null;
                } catch (IOException e) {
                    response = null;
                }

                assert response != null;
                return response;
            }

        };
        submitTask.execute(question).get();
    }
}
