package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

public class BlockStatement extends ASTNode {
    public BlockStatement(List statements) {
        super(Type.BLOCK, statements);
    }
}
