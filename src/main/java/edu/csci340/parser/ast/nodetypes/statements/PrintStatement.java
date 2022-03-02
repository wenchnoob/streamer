package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class PrintStatement extends ASTNode {
    public PrintStatement(ASTNode child) {
        super(Type.PRINT, List.of(child));
    }
}
