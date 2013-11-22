package epfl.sweng.cache;

import java.io.File;
import java.util.Set;

import android.content.Context;

import epfl.sweng.quizquestions.QuizQuestion;

public final class QuestionCache {
	
	/**
	 * Singleton instance of QuestionCache
	 */
	private static QuestionCache instance = null;
	
	/**
	 * Directory where tag file are stored
	 */
	private File tagDir;
	
	/**
	 * Directory where questions are stored
	 */
	private File questionDir;
	
	private QuestionCache(Context context) {
		File mainDir = context.getFilesDir();
		tagDir = new File(mainDir, "tags");
		tagDir.mkdir(); // return false if already exists
		questionDir = new File(mainDir, "questions");
		questionDir.mkdir();
	}

	public static synchronized QuestionCache getInstance(Context context) {
		if (instance == null) {
			instance = new QuestionCache(context);
		}
		return instance;
	}
	
	public void cacheQuestion(QuizQuestion question) {
		// TODO implement this
	}
	
	public Set<Integer> getQuestionSetByTag(String tag) {
		// TODO implement this
		return null;
	}
	
	public QuizQuestion getQuestionById(Integer id) {
		// TODO implement this
		return null;
	}
	
}
