package edu.csci340.parser.ast.nodetypes.expressions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class InputExpression extends ASTNode {
    public InputExpression(ASTNode child) {
        super(Type.INPUT, List.of(child));
    }
}
