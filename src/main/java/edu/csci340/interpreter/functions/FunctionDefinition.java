package edu.csci340.interpreter.functions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public record FunctionDefinition(String name, List<ASTNode> formalParams) {
}
