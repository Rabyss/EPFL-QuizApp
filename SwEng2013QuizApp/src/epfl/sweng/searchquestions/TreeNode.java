package epfl.sweng.searchquestions;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;

public abstract class TreeNode {
	private Set<QuizQuestion> questions;
	
	public Set<QuizQuestion> getQuestions() {
		return this.questions;
	}
}
