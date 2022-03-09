package edu.csci340.parser.ast.nodetypes.expressions;

import edu.csci340.Utils;
import edu.csci340.parser.ast.nodetypes.ASTNode;

public class InputExpression extends ASTNode {

    public InputExpression(ASTNode child) {
        super(Type.INPUT, child);
    }

    @Override
    public String toString(int depth) {
        String tabs = Utils.tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value:\n%s", ((ASTNode)value()).toString(depth + 1)));
        return str.toString();
    }
}
