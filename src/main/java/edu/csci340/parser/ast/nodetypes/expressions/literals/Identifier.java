package edu.csci340.parser.ast.nodetypes.expressions.literals;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import static edu.csci340.Utils.tabs;

public class Identifier extends ASTNode {
    public Identifier(String value) {
        super(Type.IDENTIFIER, value);
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value: %s\n", value()));
        return str.toString();
    }
}
