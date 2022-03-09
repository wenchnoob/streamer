package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import static edu.csci340.Utils.tabs;

public class VariableStatement extends ASTNode {

    public VariableStatement(ASTNode varType, String id, ASTNode varValue) {
        super(Type.VARIABLE, id, new ArrayList<>() {{ add(varType); add(varValue); }});
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        if (value() != null) str.append(tabs).append(String.format("Value: %s\n", value()));
        str.append(printChildren(depth));
        return str.toString();
    }

}
