package epfl.sweng.searchquestions;

public class TreeLeaf extends TreeNode {
	private String tag;
	
	public TreeLeaf(String tag) {
		this.tag = tag;
	}
	
	public String getTag() {
		return tag;
	}
}
