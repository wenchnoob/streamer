package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.List;

import static edu.csci340.Utils.tabs;

public class ForCondition extends ASTNode {

    public ForCondition(ASTNode capture, ASTNode collection) {
        super(Type.FOR_CONDITION, null, new ArrayList<>(List.of(capture, collection)));
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(printChildren(depth));
        return str.toString();
    }
}
