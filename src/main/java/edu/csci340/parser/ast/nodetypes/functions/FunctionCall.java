package edu.csci340.parser.ast.nodetypes.functions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class FunctionCall extends ASTNode {
    public FunctionCall(String id, ASTNode args) {
        super(Type.FUNCTION_CALL, id, List.of(args));
    }
}
