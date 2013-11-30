package epfl.sweng.searchquestions.parser.tree;

public interface ASTVisitor {
    String visit(TreeLeaf leaf);

    String visit(TreeAnd andNode);

    String visit(TreeOr orNode);

    String visit(SingleChildTreeNode scNode);
}
