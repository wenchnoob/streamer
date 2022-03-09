package edu.csci340.interpreter.scopes;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableStatement;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;

import java.util.Hashtable;
import java.util.Objects;

public class Scope {

    private static Scope self;

    private Hashtable<String, Object> variables;

    public Scope() {
        this.variables = new Hashtable<>();
    }

    public static Scope global() {
        if (self == null) self = new Scope();
        return self;
    }

    public void setVariable(String name, Object value) throws IllegalAccessException {
        if (variables.containsKey(name)) throw new IllegalAccessException("Attempting to override a variable");
        variables.put(name, Objects.isNull(value) ? "null" : value);
    }

    public Object getVariable(String name) throws IllegalAccessException {
        if (!variables.containsKey(name)) throw new IllegalAccessException("Attempting to access a variable that does not exist");
        return variables.get(name);
    }

    public static ASTNode eval(ASTNode node, Scope scope) {

        if (node instanceof VariableStatement s) {
            s.lastChild(StreamerInterpreter.eval(s.lastChild()));
            try {
                switch (s.lastChild().type()) {
                    case STRING_LITERAL -> scope.setVariable(String.valueOf(s.value()), s.lastChild().value());
                    case NUMERIC_LITERAL -> scope.setVariable(String.valueOf(s.value()), Double.valueOf(String.valueOf(s.lastChild().value())));
                    case BOOLEAN_LITERAL -> scope.setVariable(String.valueOf(s.value()), Boolean.valueOf(String.valueOf(s.lastChild().value())));
                }
                return s.lastChild();
            } catch (IllegalAccessException e) {
                System.err.println("Variable assignment has failed");
            }
        }

        if (node instanceof Identifier) {
            String id = String.valueOf(node.value());
            Object val = null;
            try {
                val = scope.getVariable(id);
            } catch (IllegalAccessException e) {
                System.err.println("Variable access has failed");
            }
            if (val instanceof Integer || val instanceof Double) return new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(val));
            if (val instanceof String) return new Literal(String.valueOf(val));
            if (val instanceof Boolean) return new Literal(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(val));
        }

        return node;
    }
}
