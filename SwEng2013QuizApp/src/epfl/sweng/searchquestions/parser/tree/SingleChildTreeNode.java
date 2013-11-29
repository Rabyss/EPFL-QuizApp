package epfl.sweng.searchquestions.parser.tree;

import java.util.Set;

public class SingleChildTreeNode extends TreeNode {
    @Override
    public Set<Integer> getIDs() {
        return getChild(0).getIDs();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "SINGLE:"+super.toString();
    }


}
