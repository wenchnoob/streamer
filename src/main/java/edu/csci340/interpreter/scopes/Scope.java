package edu.csci340.interpreter.scopes;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.errors.ProgramError;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableType;
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

    public void setVariable(String name, Object value) {
        variables.put(name, Objects.isNull(value) ? "null" : value);
    }

    public Object getVariable(String name) {
        if (!variables.containsKey(name))
            throw new ProgramError("Attempting to access a variable that does not exist");
        return variables.get(name);
    }

    public static ASTNode eval(ASTNode node, Scope scope) {

        if (node instanceof VariableStatement s) {
            s.lastChild(StreamerInterpreter.eval(s.lastChild(), scope));

            if (scope.variables.containsKey(String.valueOf(s.value())) && ((java.util.List<VariableType.VarType>)s.firstChild().value()).get(0) != VariableType.VarType.PREDEFINED) {
                throw new ProgramError("Attempting to redeclare a variable");
            }

            if (s.lastChild() == null) {
                scope.setVariable(String.valueOf(s.value()), null);
            } else {
//                if (!(((java.util.List<VariableType.VarType>)s.firstChild().value()).get(0) != VariableType.VarType.PREDEFINED))
//                        if (!checkTypes((java.util.List<VariableType.VarType>)s.firstChild().value(), s.lastChild(), 0)) throw new ProgramError("Type mismatch");
                switch (s.lastChild().type()) {
                    case STRING_LITERAL -> scope.setVariable(String.valueOf(s.value()), s.lastChild().value());
                    case NUMERIC_LITERAL -> scope.setVariable(String.valueOf(s.value()), Double.valueOf(String.valueOf(s.lastChild().value())));
                    case BOOLEAN_LITERAL -> scope.setVariable(String.valueOf(s.value()), Boolean.valueOf(String.valueOf(s.lastChild().value())));
                    case EXPRESSION_LIST -> scope.setVariable(String.valueOf(s.value()), s.lastChild().children());
                }
            }

            return s.lastChild();
        }

        if (node.type() == ASTNode.Type.VARIABLE) {
            node.lastChild(StreamerInterpreter.eval(node.lastChild(), scope));

            if (scope.variables.containsKey(String.valueOf(node.value())) && !((java.util.List<VariableType.VarType>)node.firstChild().value()).get(0).equals(VariableType.VarType.PREDEFINED)) {
                throw new ProgramError("Attempting to redeclare a variable");
            }

            if (node.lastChild() == null) {
                scope.setVariable(String.valueOf(node.value()), null);
            } else {
                // if (!checkTypes((java.util.List<VariableType.VarType>)node.firstChild().value(), node.lastChild(), 0)) throw new ProgramError("Type mismatch");
                switch (node.lastChild().type()) {
                    case STRING_LITERAL -> scope.setVariable(String.valueOf(node.value()), node.lastChild().value());
                    case NUMERIC_LITERAL -> scope.setVariable(String.valueOf(node.value()), Double.valueOf(String.valueOf(node.lastChild().value())));
                    case BOOLEAN_LITERAL -> scope.setVariable(String.valueOf(node.value()), Boolean.valueOf(String.valueOf(node.lastChild().value())));
                    case EXPRESSION_LIST -> scope.setVariable(String.valueOf(node.value()), node.lastChild().children());
                }
            }

            return node.lastChild();
        }

        if (node instanceof Identifier || node.type() == ASTNode.Type.IDENTIFIER) {
            String id = String.valueOf(node.value());
            Object val = scope.getVariable(id);
            if (val instanceof Integer || val instanceof Double)
                return new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(val));
            if (val instanceof String) return new Literal(String.valueOf(val));
            if (val instanceof Boolean) return new Literal(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(val));
            if (val instanceof List l) return l;
            if (val instanceof java.util.List ls) {
                List l = new List(ASTNode.Type.EXPRESSION_LIST);
                ls.forEach(e -> l.append((ASTNode) e));
                return l;
            }
        }

        return node;
    }

    public static boolean checkTypes(java.util.List<VariableType.VarType> types, Object value, int i) {
        if (types.size() <= i)
            return !(value instanceof ASTNode);

        VariableType.VarType t = types.get(i);
        return switch (t) {
            case TEXT -> {
                if (value instanceof ASTNode n) yield  n.type().equals(ASTNode.Type.STRING_LITERAL) && checkTypes(types, n.value(), i+1);
                yield false;
            }
            case NUM -> {
                if (value instanceof ASTNode n) yield  n.type().equals(ASTNode.Type.NUMERIC_LITERAL) && checkTypes(types, n.value(), i+1);
                yield false;
            }
            case BOOL -> {
                if (value instanceof ASTNode n) yield  n.type().equals(ASTNode.Type.BOOLEAN_LITERAL) && checkTypes(types, n.value(), i+1);
                yield false;
            }
            case LIST -> {
                if (value instanceof ASTNode n) {
                    if (!n.type().equals(ASTNode.Type.EXPRESSION_LIST)) yield false;
                    java.util.List<ASTNode> children = n.children();
                    boolean res = true;
                    for (ASTNode c: children) res = res && checkTypes(types, c, i+1);
                    yield  res;
                }
                yield false;
            }
            case VOID -> Objects.isNull(value);
            default -> false;
        };
    }
}
