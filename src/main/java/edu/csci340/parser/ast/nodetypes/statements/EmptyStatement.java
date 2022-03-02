package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

public class EmptyStatement extends ASTNode {
    public EmptyStatement() {
        super(Type.EMPTY_STATEMENT);
    }
}
