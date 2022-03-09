package edu.csci340.parser.ast.nodetypes.expressions;

import static edu.csci340.Utils.tabs;
import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;

public class BinaryExpression extends ASTNode {
    public BinaryExpression(String value, ASTNode left, ASTNode right) {
        super(Type.BINARY_EXPRESSION, value, new ArrayList<>(){{add(left); add(right);}});
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Operator: %s\n", value().toString()));
        str.append(printChildren(depth));
        return str.toString();
    }
}
