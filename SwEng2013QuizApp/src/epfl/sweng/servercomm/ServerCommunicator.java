package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;
import android.widget.Toast;
import epfl.sweng.QuizQuestion;

/**
 * 
 * Handles communication with the server.
 * 
 * @author Jeremy Rabasco (jeremy.rabasco@epfl.ch), Philemon Favrod
 *         (philemon.favrod@epfl.ch)
 */
public final class ServerCommunicator {

	private static ServerCommunicator mInstance = null;
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

	private ServerCommunicator() {

	}

	public synchronized ServerCommunicator getInstance() {
		if (mInstance == null) {
			mInstance = new ServerCommunicator();
		}
		return mInstance;
	}

	public QuizQuestion getRandomQuestion() throws InterruptedException,
			ExecutionException {
		AsyncTask<Void, Void, QuizQuestion> fetchTask = new AsyncTask<Void, Void, QuizQuestion>() {

			@Override
			protected QuizQuestion doInBackground(Void... params) {

				// Construct the request
				HttpGet questionRequest = new HttpGet(SERVER_URL);

				return null;
			}

		};
		return fetchTask.execute().get();
	}

	public void submitQuizQuestion(QuizQuestion question) throws InterruptedException, ExecutionException {

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
