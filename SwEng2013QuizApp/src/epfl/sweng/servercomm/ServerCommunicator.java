package epfl.sweng.servercomm;

import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import epfl.sweng.QuizQuestionModel;

/**
 * 
 * Handles communication with the server.
 * 
 * @author Jérémy Rabasco (jeremy.rabasco@epfl.ch) & Philémon Favrod
 *         (philemon.favrod@epfl.ch)
 * 
 */
public final class ServerCommunicator {

	private static ServerCommunicator mInstance = null;
	
	private ServerCommunicator() {
		
	}
	
	public synchronized ServerCommunicator getInstance() {
		if (mInstance == null) {
			mInstance = new ServerCommunicator();
		}
		return mInstance;
	}
	
	public QuizQuestionModel getRandomQuestion() throws InterruptedException,
			ExecutionException {
		AsyncTask<Void, Void, QuizQuestionModel> fetchTask = new AsyncTask<Void, Void, QuizQuestionModel>() {

			@Override
			protected QuizQuestionModel doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		return fetchTask.execute().get();
	}
	
	public void submitQuizQuestion(QuizQuestionModel question) throws InterruptedException, ExecutionException {
		
		AsyncTask<QuizQuestionModel, Void, Void> submitTask = new AsyncTask<QuizQuestionModel, Void, Void>() {

			@Override
			protected Void doInBackground(QuizQuestionModel... params) {
				// TODO Auto-generated method stub
				return null;
			}


		};
		submitTask.execute(question).get();
	}
}
