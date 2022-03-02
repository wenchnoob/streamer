package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class ExitStatement extends ASTNode {
    public ExitStatement(ASTNode child) {
        super(Type.EXIT, List.of(child));
    }
}
