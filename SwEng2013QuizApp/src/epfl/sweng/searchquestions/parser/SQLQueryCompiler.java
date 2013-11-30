package epfl.sweng.searchquestions.parser;

import epfl.sweng.cache.SQLiteCache;
import epfl.sweng.searchquestions.parser.tree.ASTVisitor;
import epfl.sweng.searchquestions.parser.tree.SingleChildTreeNode;
import epfl.sweng.searchquestions.parser.tree.TreeAnd;
import epfl.sweng.searchquestions.parser.tree.TreeLeaf;
import epfl.sweng.searchquestions.parser.tree.TreeNode;
import epfl.sweng.searchquestions.parser.tree.TreeOr;

/**
 * Transforms an AST to a SQL request.
 */
public class SQLQueryCompiler implements ASTVisitor {

    public String toSQL(TreeNode root) {
        return root.accept(this);
    }

    @Override
    public String visit(TreeLeaf leaf) {
        return SQLiteCache.COL_TAG+" = '"+leaf.getTag()+"'";
    }

    @Override
    public String visit(TreeAnd andNode) {
        return "(EXISTS(SELECT 1 FROM table_tag WHERE question_id=tag_question_id AND "+andNode.getChild(0).accept(this)+") AND "+
        "EXISTS(SELECT 1 FROM table_tag WHERE question_id=tag_question_id AND "+andNode.getChild(1).accept(this)+") )";
    }

    @Override
    public String visit(TreeOr orNode) {
        return visitTreeNodeWithTwoChild("OR", orNode);
    }

    @Override
    public String visit(SingleChildTreeNode scNode) {
        return scNode.getChild(0).accept(this);
    }

    private String visitTreeNodeWithTwoChild(String operand, TreeNode node) {
        return "("+node.getChild(0).accept(this)+" "+operand+" "+node.getChild(1).accept(this)+")";
    }
}
