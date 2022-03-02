package edu.csci340.interpreter;

import edu.csci340.interpreter.expressions.Binary;
import edu.csci340.interpreter.io.Input;
import edu.csci340.interpreter.io.Output;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.*;

import java.io.IOException;

public class StreamerInterpreter {
    private static Binary binary = Binary.getInstance();

    public static Object eval(String s) {
        ASTNode parsed = new StreamerParser().parse(s);
        //System.out.println(parsed);
        return eval(parsed);
    }

    public static Object eval(ASTNode node) {

        switch (node.type()) {
            case PROGRAM -> {
                for (ASTNode child : node.children()) eval(child);
            }
            case BINARY_EXPRESSION -> binary.eval(node);
            case NUMERIC_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL -> {
                return node.value();
            }
            case PRINT -> Output.eval(node);
            case INPUT -> {
                try {
                    Input.eval(node);
                } catch (IOException e) {
                    System.err.println("I/O Error, rip");
                }
            }
            case IDENTIFIER, VARIABLE -> Scope.eval(node);
        }
        return 0;
    }
}
