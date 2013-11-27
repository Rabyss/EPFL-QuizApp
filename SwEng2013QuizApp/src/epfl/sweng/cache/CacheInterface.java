package epfl.sweng.cache;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;

public interface CacheInterface {
	void cacheQuestion(QuizQuestion question);
	
	Set<QuizQuestion> getQuestionSetByTag(String tag);
	
	void clearCache();
}
