package edu.csci340.parser.ast.nodetypes.expressions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class UnaryExpression extends ASTNode {

    ASTNode operand;

    public UnaryExpression(String op, ASTNode operand) {
        super(Type.UNARY_EXPRESSION, op, List.of(operand));
        this.operand = operand;
    }
}
