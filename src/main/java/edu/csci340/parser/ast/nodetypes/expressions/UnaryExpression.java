package edu.csci340.parser.ast.nodetypes.expressions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class UnaryExpression extends ASTNode {

    public UnaryExpression(String op, ASTNode operand) {
        super(Type.UNARY_EXPRESSION, op, new ArrayList<>(){{ add(operand); }});
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value: %s\n", value().toString()));
        str.append(printChildren(depth));
        return str.toString();
    }
}
