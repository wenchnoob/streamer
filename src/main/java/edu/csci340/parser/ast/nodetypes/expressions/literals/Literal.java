package edu.csci340.parser.ast.nodetypes.expressions.literals;

import static edu.csci340.Utils.tabs;
import edu.csci340.parser.ast.nodetypes.ASTNode;

public class Literal extends ASTNode {

    public Literal(Type type, String value) {
        super(type, parseValue(type, value));
    }

    public Literal(double value) {
        super(Type.NUMERIC_LITERAL, value);
    }

    public Literal(boolean value) {
        super(Type.BOOLEAN_LITERAL, value);
    }

    public Literal(String value) {
        super(Type.STRING_LITERAL, value);
    }

    private static Object parseValue(Type t, String value) {
        return switch (t) {
            case NUMERIC_LITERAL -> Double.valueOf(value);
            case BOOLEAN_LITERAL -> Boolean.valueOf(value);
            case STRING_LITERAL -> value;
            default -> throw new IllegalStateException("The Literal <" + value + "> is not of a valid type: " + t);
        };
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value: %s\n", value()));
        return str.toString();
    }
}
