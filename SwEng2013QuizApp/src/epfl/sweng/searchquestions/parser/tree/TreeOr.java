package epfl.sweng.searchquestions.parser.tree;

import java.util.HashSet;
import java.util.Set;

public class TreeOr extends TreeNode {

    @Override
    public Set<Integer> getIDs() {
        Set<Integer> or = new HashSet<Integer>(getChild(0).getIDs());
        or.addAll(getChild(1).getIDs());
        return or;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(OR" + super.toString() + ")";
    }
}
