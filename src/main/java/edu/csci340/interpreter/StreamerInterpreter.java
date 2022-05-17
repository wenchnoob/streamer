package edu.csci340.interpreter;

import edu.csci340.interpreter.conditionals.Conditionals;
import edu.csci340.interpreter.errors.ProgramError;
import edu.csci340.interpreter.expressions.Binary;
import edu.csci340.interpreter.functions.Functions;
import edu.csci340.interpreter.io.Input;
import edu.csci340.interpreter.io.Output;
import edu.csci340.interpreter.loops.ForLoop;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.statements.WhileLoop;

import java.io.IOException;
import java.util.Objects;

public class StreamerInterpreter {

    private static Binary binary = Binary.getInstance();

    public static ASTNode eval(String s) {
        ASTNode parsed = new StreamerParser().parse(s);
        //System.out.println(parsed);
        return eval(parsed, Scope.global());
    }

    public static ASTNode eval(ASTNode node, Scope scope) {
        if (Objects.isNull(node)) return null;

        return switch (node.type()) {
            case PROGRAM , STATEMENT_LIST -> {
                for (int i = 0; i < node.children().size(); i++) node.nthChild(i, eval(node.nthChild(i), scope));
                yield node;
            }
            case BINARY_EXPRESSION -> binary.eval(node, scope);
            case PRINT -> Output.eval(node, scope);
            case INPUT, FILE_READ -> {
                try {
                    yield Input.eval(node, scope);
                } catch (IOException e) {
                    throw new ProgramError("Failed to find file");
                }
            }
            case CONDITIONAL -> Conditionals.eval(node, scope);
            case IDENTIFIER, VARIABLE -> Scope.eval(node, scope);
            case FUNCTION_CALL, FUNCTION_DEFINITION -> Functions.eval(node, scope);
            case FOR_LOOP -> ForLoop.eval(node, scope);
            case WHILE_LOOP -> edu.csci340.interpreter.loops.WhileLoop.eval(node, scope);
            default -> node;
        };
    }

}
