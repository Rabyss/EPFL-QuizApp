package epfl.sweng.searchquestions;

import java.util.Set;

import epfl.sweng.quizquestions.QuizQuestion;

public abstract class TreeBranch extends TreeNode {
	private TreeNode leftChild;
	private TreeNode rightChild;
	
	public abstract Set<QuizQuestion> combine();
	
	public TreeNode getLeftChild() {
		return leftChild;
	}
	
	public TreeNode getRightChild() {
		return rightChild;
	}
}
