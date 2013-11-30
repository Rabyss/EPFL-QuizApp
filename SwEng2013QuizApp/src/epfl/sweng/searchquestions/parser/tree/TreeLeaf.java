package epfl.sweng.searchquestions.parser.tree;

public class TreeLeaf extends TreeNode {
    private String mTag;

    public TreeLeaf(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    @Override
    public String accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LEAF:" + mTag;
    }
}
