package edu.csci340.interpreter.io;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.InputExpression;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.FileRead;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;
import edu.csci340.stdlib.IO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Input {
    public static ASTNode eval(ASTNode node, Scope scope) throws IOException {
        if (node instanceof InputExpression p) {
            p.value(StreamerInterpreter.eval((ASTNode) p.value(), scope));
            String prompt = String.valueOf(((ASTNode)p.value()).value());
            return new Literal(IO.readLine(prompt));
        }

        if (node instanceof FileRead f) {
            String src = String.valueOf(StreamerInterpreter.eval((ASTNode) f.value(), scope).value());
            try {
                return new Literal(Files.readString(Path.of(src)));
            } catch (IOException e) {
                throw e;
            }
        }

        return node;
    }

}
