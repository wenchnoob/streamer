package edu.csci340.interpreter.io;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.InputExpression;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;
import edu.csci340.stdlib.IO;

import java.io.IOException;

public class Input {
    public static ASTNode eval(ASTNode node) throws IOException {
        if (node instanceof InputExpression p) {
            p.value(StreamerInterpreter.eval((ASTNode) p.value()));
            String prompt = String.valueOf(((ASTNode)p.value()).value());
            return new Literal(IO.readLine(prompt));
        }
        return node;
    }
}
