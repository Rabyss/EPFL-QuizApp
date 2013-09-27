package epfl.sweng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.Toast;

import epfl.sweng.servercomm.SwengHttpClientFactory;

/**
 * 
 * Submits a question that the user has created.
 * 
 * @author Rabyss (jeremy.rabasco@epfl.ch)
 * 
 */
public class QuizQuestionSubmit extends AsyncTask<Void, Void, String> {

	private String mQuestion;
	private String[] mAnswers;
	private int mSolutionIndex;
	private String[] mTags;

	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

	public QuizQuestionSubmit(String question, String[] answers,
			int solutionIndex, String[] tags) {
		mQuestion = question;
		mAnswers = answers;
		mSolutionIndex = solutionIndex;
		mTags = tags;
	}

	public String submitViaObject() {
		HashMap<String, Object> question = new HashMap<String, Object>();

		question.put("question", mQuestion);
		question.put("answers", new JSONArray(Arrays.asList(mAnswers)));
		question.put("solutionIndex", mSolutionIndex);
		question.put("tags", new JSONArray(Arrays.asList(mTags)));

		String questionInJSON = new JSONObject(question).toString();

		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");

		try {
			post.setEntity(new StringEntity(questionInJSON));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		post.setHeader("Content-type", "application/json");
		ResponseHandler<String> handler = new BasicResponseHandler();
		String response = "";
		try {
			response = SwengHttpClientFactory.getInstance().execute(post,
					handler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public void submitViaString() {

	}

	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return submitViaObject();
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
		} else {
		}

	}

}
