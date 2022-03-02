package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class VariableType extends ASTNode {

    ASTNode subtype;
    List<Type> subTypes;

    public VariableType(Type type) {
        super(type);
    }

    public VariableType(Type type, ASTNode subtype) {
        super(type);
        this.subtype = subtype;
        if (subtype != null) {
            this.subTypes = new ArrayList<>();
            flatten((VariableType) subtype, subTypes);
        }
    }

    @Override
    public String toString(int depth) {
        StringBuilder str = new StringBuilder();
        str.append(type()).append(" ");
        if (subTypes != null) for (Type t: subTypes) str.append(t).append(" ");
        return str.toString();
    }

    private void flatten(VariableType type, List<Type> types) {
        if (type == null) return;
        types.add(type.type());
        flatten((VariableType) type.subtype, types);
    }

}
