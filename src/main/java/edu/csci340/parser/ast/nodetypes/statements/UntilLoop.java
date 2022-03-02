package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class UntilLoop extends ASTNode {
    public UntilLoop(ASTNode cond, ASTNode body) {
        super(Type.UNTIL_LOOP, List.of(cond, body));
    }
}
