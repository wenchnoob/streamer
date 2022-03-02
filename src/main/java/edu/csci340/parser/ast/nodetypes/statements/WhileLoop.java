package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class WhileLoop extends ASTNode {
    public WhileLoop(ASTNode cond, ASTNode body) {
        super(Type.WHILE_LOOP, List.of(cond, body));
    }
}
