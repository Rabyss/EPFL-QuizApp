package epfl.sweng.searchquestions;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import epfl.sweng.cache.QuestionCache;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.parser.tree.TreeNode;

public class TagStorage {
	private TreeNode tree;
	private Context context;
	
	public TagStorage(Context context) {
		this.context = context;
	}
	
	public Set<Integer> query(/* TODO */) {
		return null; // TODO return something
	}
	
	public void createTree() {
		QuestionCache cache = QuestionCache.getInstance(context);
		// TODO Parcourir l'arbre de Phil:
		// cache.getQuestionSetByTag(tag);
		// Cr�er mon arbre avec les questionIDs.
	}
	
	public Set<QuizQuestion> getQuestions() {
		QuestionCache cache = QuestionCache.getInstance(context);
		Set<QuizQuestion> questions = new HashSet<QuizQuestion>();
		
		for (Integer id : tree.getIDs()) {
			QuizQuestion question = cache.getQuestionById(id);
			
			if (question != null) {
				questions.add(question);
			}
		}
		
		return questions;
	}
}
