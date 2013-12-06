package epfl.sweng.cache;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.tree.TreeNode;

public interface CacheInterface {
	void cacheQuestion(QuizQuestion question);
	
	void clearCache();

	Set<QuizQuestion> getQuestionSetByTag(TreeNode ast);
}
