package edu.csci340.parser.ast.nodetypes.functions;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionCall extends ASTNode {
    public FunctionCall(String id, ASTNode args) {
        super(Type.FUNCTION_CALL, id, new ArrayList<>(List.of(args)));
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        if (Objects.nonNull(value())) str.append(tabs).append("Value:\n").append(tabs).append('\t').append(String.format("%s\n", value().toString()));
        java.util.List<ASTNode> children = children();
        if (Objects.nonNull(children)) {
            str.append(tabs).append("Children:\n");
            depth++;
            tabs.append('\t');
            for (int i = 0; i < children.size(); i++) str.append(tabs).append(String.format("Child %d:\n%s", i, Objects.nonNull(children.get(i)) ? children.get(i).toString(depth + 1) : null));
        }
        return str.toString();
    }
}
