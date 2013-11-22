package epfl.sweng.searchquestions;

import java.util.HashSet;
import java.util.Set;

public class TreeAnd extends TreeNode {
	private TreeNode leftChild;
	private TreeNode rightChild;
	
	@Override
	public Set<Integer> getIDs() {
		Set<Integer> and = new HashSet<Integer>(getLeftChild().getIDs());
		and.retainAll(getRightChild().getIDs());
		return and;
	}

	@Override
	public TreeNode getLeftChild() {
		return leftChild;
	}

	@Override
	public TreeNode getRightChild() {
		return rightChild;
	}
}
