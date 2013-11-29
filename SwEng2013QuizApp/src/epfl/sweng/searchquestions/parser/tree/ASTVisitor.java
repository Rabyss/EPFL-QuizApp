package epfl.sweng.searchquestions.parser.tree;

public interface ASTVisitor {
    void visit(TreeLeaf leaf);

    void visit(TreeAnd andNode);

    void visit(TreeOr orNode);

    void visit(SingleChildTreeNode scNode);
}
