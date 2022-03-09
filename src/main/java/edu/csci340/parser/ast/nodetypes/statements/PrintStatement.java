package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import static edu.csci340.Utils.tabs;

public class PrintStatement extends ASTNode {
    public PrintStatement(ASTNode child) {
        super(Type.PRINT, child);
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value:\n%s", ((ASTNode)value()).toString(depth + 1)));
        return str.toString();
    }
}
