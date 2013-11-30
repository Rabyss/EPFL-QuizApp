package epfl.sweng.searchquestions.parser.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class TreeNode {
    private List<TreeNode> childs;

    public TreeNode() {
        childs = new LinkedList<TreeNode>();
    }

    public abstract String accept(ASTVisitor visitor);

    public TreeNode getChild(int index) {
        return childs.get(index);
    }

    public void changeChild(TreeNode toChange, TreeNode byNode) {
        int indexToChange = childs.indexOf(toChange);

        if (indexToChange == -1) {
            throw new NoSuchElementException();
        } else {
            childs.set(indexToChange, byNode);
        }
    }

    public int getChildCount() {
        return childs.size();
    }

    public void addChild(TreeNode child) {
        childs.add(child);
    }

    @Override
    public String toString() {
        return childs.toString();
    }
}
