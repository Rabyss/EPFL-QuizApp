package epfl.sweng.searchquestions;

import java.util.HashSet;
import java.util.Set;

public class TreeOr extends TreeNode {
	private TreeNode leftChild;
	private TreeNode rightChild;
	
	@Override
	public Set<Integer> getIDs() {
		Set<Integer> or = new HashSet<Integer>(getLeftChild().getIDs());
		or.addAll(getRightChild().getIDs());
		return or;
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
