package epfl.sweng.cache;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;

public interface CacheInterface {
	void cacheQuestion(QuizQuestion question);
	
	void clearCache();

	Set<QuizQuestion> getQuestionSetByTag(String tagsMark, String[] tagsSearch);
}
