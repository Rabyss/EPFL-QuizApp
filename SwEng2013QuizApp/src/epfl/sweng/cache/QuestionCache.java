package epfl.sweng.cache;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;

public class QuestionCache {
	
	private static QuestionCache instance = null;
	
	public static synchronized QuestionCache getInstance() {
		if (instance == null) {
			instance = new QuestionCache();
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
