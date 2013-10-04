package epfl.sweng.servercomm;

import java.io.IOException;
import java.util.Observable;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import epfl.sweng.QuizQuestion;

/**
 * 
 * Handles communication with the server.
 *
 */
public final class ServerCommunicator extends Observable {

    private static ServerCommunicator sInstance = null;
    private final static String SERVER_URL = "https://sweng-quiz.appspot.com";
    private boolean isFetching;
    private boolean isSubmitting;

    private ServerCommunicator() {

    }

    public static synchronized ServerCommunicator getInstance() {
        if (sInstance == null) {
            sInstance = new ServerCommunicator();
        }
        return sInstance;
    }

    public void getRandomQuestion() {

        isFetching = true;

        // Creates an asynchronous task to fetch from the server.
        // and fetch the question
        new FetchTask().execute();
    }

    public void submitQuizQuestion(QuizQuestion question,
            final Activity activity) {

        isSubmitting = true;

        new SubmitTask().execute(question);
    }

    public boolean isFetchingQuestion() {
        return isFetching;
    }

    public boolean isSubmittingQuestion() {
        return isSubmitting;
    }

    /**
     * 
     * Asynchronous task used to submit questions to the server.
     * 
     */
    private final class SubmitTask extends
            AsyncTask<QuizQuestion, Void, String> {
        @Override
        protected String doInBackground(QuizQuestion... params) {
            HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
            //construction of the request
            String response = "";
            try {
                post.setEntity(new StringEntity(params[0].toJSON()));
                post.setHeader("Content-type", "application/json");
                ResponseHandler<String> handler = new BasicResponseHandler();

                response = SwengHttpClientFactory.getInstance().execute(post,
                        handler);
            } catch (ClientProtocolException e) {
                response = null;
            } catch (IOException e) {
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ServerCommunicator.getInstance().setChanged();
            ServerCommunicator.getInstance().notifyObservers(result);
            ServerCommunicator.getInstance().clearChanged();
            isSubmitting = false;
        }
    }

    /**
     * 
     * Asynchronous task used to fetch questions from the server.
     * 
     */
    private final class FetchTask extends AsyncTask<Void, Void, QuizQuestion> {
        @Override
        protected QuizQuestion doInBackground(Void... params) {

            // Construct the request
            HttpGet questionFetchRequest = new HttpGet(SERVER_URL
                    + "/quizquestions/random");
            ResponseHandler<String> questionFetchHandler = new BasicResponseHandler();

            String strRandomQuestion;
            try {
                strRandomQuestion = SwengHttpClientFactory.getInstance()
                        .execute(questionFetchRequest, questionFetchHandler);
                JSONObject jsonModel = new JSONObject(strRandomQuestion);
                return new QuizQuestion(jsonModel);
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(QuizQuestion result) {
            super.onPostExecute(result);
            ServerCommunicator.getInstance().setChanged();
            ServerCommunicator.getInstance().notifyObservers(result);
            ServerCommunicator.getInstance().clearChanged();
            isFetching = false;
        }
    }
}