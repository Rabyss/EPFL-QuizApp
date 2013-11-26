package epfl.sweng.searchquestions;

import epfl.sweng.searchquestions.parser.tree.ASTVisitor;
import epfl.sweng.searchquestions.parser.tree.TreeNode;

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
