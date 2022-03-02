package edu.csci340.interpreter.scopes;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableStatement;

import java.util.Hashtable;

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
        variables.put(name, value);
    }

    public Object getVariable(String name) throws IllegalAccessException {
        if (!variables.containsKey(name)) throw new IllegalAccessException("Attempting to access a variable that does not exist");
        return variables.get(name);
    }

    public static void eval(ASTNode node) {
        if (node instanceof VariableStatement s) {
            StreamerInterpreter.eval(s.varValue());
            try {
                switch (s.varValue().type()) {
                    case STRING_LITERAL -> global().setVariable(String.valueOf(s.value()), s.varValue().value());
                    case NUMERIC_LITERAL -> global().setVariable(String.valueOf(s.value()), Double.valueOf(String.valueOf(s.varValue().value())));
                    case BOOLEAN_LITERAL -> global().setVariable(String.valueOf(s.value()), Boolean.valueOf(String.valueOf(s.varValue().value())));
                }
            } catch (IllegalAccessException e) {
                System.err.println("Variable assignment has failed");
            }
        }

        if (node instanceof Identifier) {
            String id = String.valueOf(node.value());
            Object val = null;
            try {
                val = global().getVariable(id);
            } catch (IllegalAccessException e) {
                System.err.println("Variable access has failed");
            }
            if (val instanceof Integer || val instanceof Double) node.setType(ASTNode.Type.NUMERIC_LITERAL);
            if (val instanceof String) node.setType(ASTNode.Type.STRING_LITERAL);
            if (val instanceof Boolean) node.setType(ASTNode.Type.BOOLEAN_LITERAL);
            node.setValue(val);
        }
    }
}
