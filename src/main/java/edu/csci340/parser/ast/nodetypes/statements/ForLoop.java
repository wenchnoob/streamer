package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class ForLoop extends ASTNode {
    public ForLoop(ASTNode capture, ASTNode collection, ASTNode body) {
        super(Type.FOR_LOOP, List.of(capture, collection, body));
    }
    public ForLoop(List<ASTNode> children) {
        super(Type.FOR_LOOP, children);
    }
}
