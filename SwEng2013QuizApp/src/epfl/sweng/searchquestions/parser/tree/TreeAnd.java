package epfl.sweng.searchquestions.parser.tree;

import java.util.HashSet;
import java.util.Set;

public class TreeAnd extends TreeNode {

    @Override
    public Set<Integer> getIDs() {

        assert getChildCount() >= 2 : "incomplete and node";

        Set<Integer> and = new HashSet<Integer>(getChild(0).getIDs());
        and.retainAll(getChild(1).getIDs());
        return and;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "AND" + super.toString();
    }


}
