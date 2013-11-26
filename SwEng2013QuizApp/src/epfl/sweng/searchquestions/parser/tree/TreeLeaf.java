package epfl.sweng.searchquestions.parser.tree;

import java.util.HashSet;
import java.util.Set;

public class TreeLeaf extends TreeNode {
    private Integer id;

    public TreeLeaf(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public Set<Integer> getIDs() {
        return new HashSet<Integer>(id);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LEAF:" + id;
    }
}
