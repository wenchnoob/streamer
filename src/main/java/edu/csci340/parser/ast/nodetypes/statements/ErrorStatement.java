package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

import java.util.ArrayList;

import static edu.csci340.Utils.tabs;

public class ErrorStatement extends ASTNode {
    public ErrorStatement(ASTNode tryBlock, List catchBlock) {
        super(Type.ERROR, new ArrayList<>(java.util.List.of(tryBlock, catchBlock)));
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value:\n%s", ((ASTNode)value()).toString(depth + 1)));
        str.append(printChildren(depth));
        return str.toString();
    }
}
