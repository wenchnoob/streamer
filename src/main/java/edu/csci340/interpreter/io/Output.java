package edu.csci340.interpreter.io;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.statements.PrintStatement;

public class Output {
    public static ASTNode eval(ASTNode node) {
        if (node instanceof PrintStatement p) {
            p.value(StreamerInterpreter.eval((ASTNode) p.value()));
            Object toPrint = ((ASTNode)p.value()).value();
            System.out.println(toPrint);
            return null;
        }
        return node;
    }
}
