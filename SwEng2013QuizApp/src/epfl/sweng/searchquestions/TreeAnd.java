package epfl.sweng.searchquestions;

import java.util.HashSet;
import java.util.Set;

public class TreeAnd extends TreeBranch {
	@Override
	public Set<Integer> combine() {
		Set<Integer> and = new HashSet<Integer>(getLeftChild().getIDs());
		and.retainAll(getRightChild().getIDs());
		return and;
	}
}
