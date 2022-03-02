package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

public class BreakStatement extends ASTNode {
    public BreakStatement() {
        super(Type.BREAK);
    }
}
