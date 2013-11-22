package epfl.sweng.searchquestions;

import java.util.Set;

public abstract class TreeNode {
	public abstract Set<Integer> getIDs();
	public abstract TreeNode getLeftChild();
	public abstract TreeNode getRightChild();
}
