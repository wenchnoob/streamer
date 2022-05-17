package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.List;

import static edu.csci340.Utils.tabs;

public class ConditionalStatement extends ASTNode {

    public ConditionalStatement(ASTNode condition, ASTNode trueBranch) {
        super(Type.CONDITIONAL, condition, new ArrayList<>(List.of(trueBranch)));
    }

    public ConditionalStatement(ASTNode condition, ASTNode trueBranch, ASTNode falseBranch) {
        super(Type.CONDITIONAL, condition, new ArrayList<>(List.of(trueBranch, falseBranch)));
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Condition:\n%s", ((ASTNode)value()).toString(depth + 1)));
        str.append(printChildren(depth));
        return str.toString();
    }
}
