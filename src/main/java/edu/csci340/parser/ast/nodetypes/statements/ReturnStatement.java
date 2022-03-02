package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class ReturnStatement extends ASTNode {
    public ReturnStatement(ASTNode child) {
        super(Type.RETURN, List.of(child));
    }
}
