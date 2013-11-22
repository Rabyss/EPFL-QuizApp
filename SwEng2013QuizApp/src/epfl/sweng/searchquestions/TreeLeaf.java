package epfl.sweng.searchquestions;

import java.util.Set;

public class TreeLeaf extends TreeNode {
	private String tag;
	
	public TreeLeaf(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}
	
	public Set<Integer> getIDs() {
		return null;
	}
}
