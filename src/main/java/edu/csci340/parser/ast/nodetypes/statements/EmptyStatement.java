package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import static edu.csci340.Utils.tabs;

public class EmptyStatement extends ASTNode {
    public EmptyStatement() {
        super(Type.EMPTY_STATEMENT, null);
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        return str.toString();
    }
}
