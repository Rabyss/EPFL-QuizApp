package epfl.sweng.searchquestions.parser.tree;

import java.util.HashSet;
import java.util.Set;

public class TreeOr extends TreeNode {

    @Override
    public String accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(OR" + super.toString() + ")";
    }
}
