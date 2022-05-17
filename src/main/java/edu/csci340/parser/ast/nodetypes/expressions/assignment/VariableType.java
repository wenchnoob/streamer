package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import static edu.csci340.Utils.tabs;

public class VariableType extends ASTNode {

    public VariableType(VarType type) {
        super(Type.VARIABLE_TYPE, new ArrayList<VarType>() {{ add(type); }});
    }

    public VariableType(List<VarType> types) {
        super(Type.VARIABLE_TYPE, types);
    }

    @Override
    public String toString(int depth) {
        StringBuilder str = new StringBuilder().append(tabs(depth));
        str.append("Variable Type: ").append(type()).append(" ");
        if (Objects.nonNull(value())) for (VarType t: (List<VarType>)value()) str.append(t).append(" ");
        return str.append('\n').toString();
    }

    public enum VarType {
        LIST,
        NUM,
        TEXT,
        BOOL,
        VOID,
        PREDEFINED
    }
}
