package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.csci340.Utils.tabs;

public class VariableType extends ASTNode {

    ASTNode subtype;
    List<Type> subTypes;

    public VariableType(Type type) {
        super(type, null);
    }

    public VariableType(Type type, ASTNode subtype) {
        super(type, null);
        this.subtype = subtype;
        if (Objects.nonNull(subtype)) {
            this.subTypes = new ArrayList<>();
            flatten((VariableType) subtype, subTypes);
        }
    }

    @Override
    public String toString(int depth) {
        StringBuilder str = new StringBuilder().append(tabs(depth));
        str.append("Variable Type: ").append(type()).append(" ");
        if (Objects.nonNull(subTypes)) for (Type t: subTypes) str.append(t).append(" ");
        return str.append('\n').toString();
    }

    private void flatten(VariableType type, List<Type> types) {
        if (Objects.isNull(type)) return;
        types.add(type.type());
        flatten((VariableType) type.subtype, types);
    }

}
