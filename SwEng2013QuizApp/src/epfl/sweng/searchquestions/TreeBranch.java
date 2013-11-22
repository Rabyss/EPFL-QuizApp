package epfl.sweng.searchquestions;

import java.util.Set;

public abstract class TreeBranch extends TreeNode {
	private TreeNode leftChild;
	private TreeNode rightChild;
	
	public abstract Set<Integer> combine();
	
	public TreeNode getLeftChild() {
		return leftChild;
	}
	
	public TreeNode getRightChild() {
		return rightChild;
	}
}
