package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class ErrorStatement extends ASTNode {
    public ErrorStatement(ASTNode tryBlock, ASTNode catchBlock) {
        super(Type.ERROR, List.of(tryBlock, catchBlock));
    }
}
