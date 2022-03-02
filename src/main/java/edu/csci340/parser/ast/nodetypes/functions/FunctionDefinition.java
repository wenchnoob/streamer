package edu.csci340.parser.ast.nodetypes.functions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class FunctionDefinition extends ASTNode {

    String returnType;

    public FunctionDefinition(String returnType, String id, ASTNode formalParameterList,  ASTNode body) {
        super(Type.FUNCTION_DEFINITION, id, List.of(formalParameterList, body));
        this.returnType = returnType;
    }
}
