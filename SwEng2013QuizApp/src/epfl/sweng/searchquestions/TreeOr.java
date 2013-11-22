package epfl.sweng.searchquestions;

import java.util.HashSet;
import java.util.Set;

public class TreeOr extends TreeBranch {
	@Override
	public Set<Integer> combine() {
		Set<Integer> or = new HashSet<Integer>(getLeftChild().getIDs());
		or.addAll(getRightChild().getIDs());
		return or;
	}
}
