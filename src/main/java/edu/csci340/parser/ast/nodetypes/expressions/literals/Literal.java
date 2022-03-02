package edu.csci340.parser.ast.nodetypes.expressions.literals;

import edu.csci340.parser.ast.nodetypes.ASTNode;

public class Literal extends ASTNode {
    Object value;

    public Literal(Type type, String value) {
        super(type);
        parseValue(value);
    }

    public Literal(double value) {
        super(Type.NUMERIC_LITERAL);
        this.value = value;
    }

    public Literal(boolean value) {
        super(Type.BOOLEAN_LITERAL);
        this.value = value;
    }

    public Literal(String value) {
        super(Type.STRING_LITERAL);
        this.value = value;
    }

    private void parseValue(String value) {
        switch (type()) {
            case NUMERIC_LITERAL: {
                this.value = Double.valueOf(value);
                break;
            }
            case BOOLEAN_LITERAL: {
                this.value = Boolean.valueOf(value);
                break;
            }
            case STRING_LITERAL: {
                this.value = value;
                break;
            }
            default: throw new IllegalStateException("The Literal <" + this + "> is not of a valid type: " + type());
        }
    }
}
