package epfl.sweng.searchquestions.parser;

import epfl.sweng.searchquestions.parser.tree.ASTVisitor;
import epfl.sweng.searchquestions.parser.tree.SingleChildTreeNode;
import epfl.sweng.searchquestions.parser.tree.TreeAnd;
import epfl.sweng.searchquestions.parser.tree.TreeLeaf;
import epfl.sweng.searchquestions.parser.tree.TreeOr;

/**
 * Transforms an AST to a SQL request.
 */
public class SQLQueryCompiler implements ASTVisitor {
    @Override
    public String visit(TreeLeaf leaf) {
        return leaf.getTag();
    }

    @Override
    public String visit(TreeAnd andNode) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String visit(TreeOr orNode) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String visit(SingleChildTreeNode scNode) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
