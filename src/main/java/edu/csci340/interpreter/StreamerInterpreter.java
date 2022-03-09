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

    public static ASTNode eval(String s) {
        ASTNode parsed = new StreamerParser().parse(s);
        //System.out.println(parsed);
        return eval(parsed);
    }

    public static ASTNode eval(ASTNode node) {
        return switch (node.type()) {
            case PROGRAM -> {
                for (int i = 0; i < node.children().size(); i++) node.nthChild(i, eval(node.nthChild(i)));
                yield node;
            }
            case BINARY_EXPRESSION -> binary.eval(node);
            case PRINT -> Output.eval(node);
            case INPUT -> {
                try {
                    yield Input.eval(node);
                } catch (IOException e) {
                    System.err.println("I/O Error, rip");
                }
                yield node;
            }
            case IDENTIFIER, VARIABLE -> Scope.eval(node, Scope.global());
            default -> node;
        };
    }
}
