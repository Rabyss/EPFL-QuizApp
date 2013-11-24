package epfl.sweng.cache;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;

public interface CacheInterface {
	void cacheQuestion(QuizQuestion question);
	
	Set<Integer> getQuestionSetByTag(String tag);
	
	QuizQuestion getQuestionById(Integer id);
}
