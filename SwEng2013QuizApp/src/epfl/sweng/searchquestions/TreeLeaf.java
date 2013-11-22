package epfl.sweng.searchquestions;

import java.util.HashSet;
import java.util.Set;

public class TreeLeaf extends TreeNode {
	private Integer id;
	
	public TreeLeaf(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}

	@Override
	public Set<Integer> getIDs() {
		return new HashSet<Integer>(id);
	}

	@Override
	public TreeNode getLeftChild() {
		return null;
	}

	@Override
	public TreeNode getRightChild() {
		return null;
	}
}
