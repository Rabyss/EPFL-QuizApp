package epfl.sweng.searchquestions.parser.tree;

public class SingleChildTreeNode extends TreeNode {

    @Override
    public String accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "SINGLE:"+super.toString();
    }


}
