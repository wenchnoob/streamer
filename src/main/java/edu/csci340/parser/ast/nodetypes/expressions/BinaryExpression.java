package edu.csci340.parser.ast.nodetypes.expressions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class BinaryExpression extends ASTNode {

    ASTNode lhs, rhs;

    public BinaryExpression(String value, ASTNode left, ASTNode right) {
        this(value, List.of(left, right));
        this.lhs = left;
        this.rhs = right;
    }

    private BinaryExpression(String value, List<ASTNode> children) {
        super(Type.BINARY_EXPRESSION, value, children);
    }

    public ASTNode lhs() {
        return this.lhs;
    }

    public ASTNode rhs() {
        return this.rhs;
    }
}
