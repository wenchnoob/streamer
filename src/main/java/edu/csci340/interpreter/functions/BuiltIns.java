package edu.csci340.interpreter.functions;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.errors.ProgramError;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;

import java.util.List;

public class BuiltIns {

    private static String[] builtIns = new String[] {"toText", "toNum", "toBool"};

    public static boolean isBuiltIn(String n) {
        for (String bIn: builtIns) if (n.equals(bIn)) return true;
        return false;
    }

    public static ASTNode eval(String name, List<ASTNode> arguments, Scope scope) {
        return switch (name) {
            case "toText" -> toText(StreamerInterpreter.eval(arguments.get(0), scope), scope);
            case "toNum" -> toNum(StreamerInterpreter.eval(arguments.get(0), scope), scope);
            case "toBool" -> toBool(StreamerInterpreter.eval(arguments.get(0), scope), scope);
            default -> arguments.get(0);
        };
    }

    public static ASTNode toText(ASTNode node, Scope scope) {
        return switch (node.type()) {
            case STRING_LITERAL -> node;
            case BOOLEAN_LITERAL, NUMERIC_LITERAL -> new Literal(String.valueOf(node.value()));
            case IDENTIFIER -> {
                Object val = scope.getVariable((String)node.value());
                if (val instanceof ASTNode n) yield  toText(n, scope);
                yield new Literal(String.valueOf(val));
            }
            //case INPUT -> toNum(StreamerInterpreter.eval(node, scope), scope);
            default -> throw new ProgramError("Casting Exception");
        };
    }

    public static ASTNode toNum(ASTNode node, Scope scope) {
        return switch (node.type()) {
            case STRING_LITERAL -> {
                String s = (String) node.value();
                if (s.equals("true")) yield new Literal(1);
                if (s.equals("false")) yield new Literal(0);
                try {
                    yield new Literal(Double.valueOf(s));
                } catch (NumberFormatException ex) {
                    throw new ProgramError("Casting Exceptions");
                }
            }
            case BOOLEAN_LITERAL -> {
                boolean b = (Boolean)node.value();
                yield new Literal(b ? 1 : 0);
            }
            case NUMERIC_LITERAL -> node;
            case IDENTIFIER -> {
                Object val = scope.getVariable((String)node.value());
                if (val instanceof ASTNode n) yield  toNum(n, scope);
                if (val instanceof Boolean b) yield toNum(new Literal(b), scope);
                if (val instanceof Double d) yield toNum(new Literal(d), scope);
                if (val instanceof Integer i) yield toNum(new Literal(i), scope);
                if (val instanceof String s) yield toNum(new Literal(s), scope);
                yield null;
            }
            //case INPUT -> toNum(StreamerInterpreter.eval(node, scope), scope);
            default -> throw new ProgramError("Casting Exception");
        };
    }

    public static ASTNode toBool(ASTNode node, Scope scope) {
        return switch (node.type()) {
            case STRING_LITERAL -> {
                String val = (String)node.value();
                if (val.equals("false") || val.equals("0")) yield new Literal(false);
                yield new Literal(true);
            }
            case BOOLEAN_LITERAL -> node;
            case NUMERIC_LITERAL -> {
                Double d = (Double)node.value();
                if (d == 0) yield new Literal(false);
                yield new Literal(true);
            }
            case IDENTIFIER -> {
                Object val = scope.getVariable((String)node.value());
                if (val instanceof ASTNode n) yield  toBool(n, scope);
                if (val instanceof Boolean b) yield toBool(new Literal(b), scope);
                if (val instanceof Double d) yield toBool(new Literal(d), scope);
                if (val instanceof Integer i) yield toBool(new Literal(i), scope);
                if (val instanceof String s) yield toBool(new Literal(s), scope);
                yield null;
            }
            //case INPUT -> toNum(StreamerInterpreter.eval(node, scope), scope);
            default -> throw new ProgramError("Casting Exception");
        };
    }
}
