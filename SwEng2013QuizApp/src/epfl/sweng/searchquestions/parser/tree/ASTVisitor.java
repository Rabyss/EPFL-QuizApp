package epfl.sweng.searchquestions.parser.tree;


import epfl.sweng.searchquestions.SingleChildTreeNode;

public interface ASTVisitor {
    void visit(TreeLeaf leaf);

    void visit(TreeAnd andNode);

    void visit(TreeOr orNode);

    void visit(SingleChildTreeNode scNode);
}
