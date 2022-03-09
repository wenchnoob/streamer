package edu.csci340.parser.ast.nodetypes;


import java.util.ArrayList;
import java.util.Objects;

import static edu.csci340.Utils.tabs;

public class List extends ASTNode {

    public List(Type type) {
        super(type, null, new ArrayList<>());
    }

    public List(Type type, ASTNode ...items) {
        super(type, null, Objects.nonNull(items) ? new ArrayList<>() {{ addAll(java.util.List.of(items));}} : new ArrayList<>());
    }

    public List(ASTNode ...items) {
        this(Type.STATEMENT_LIST, items);
    }

    public void append(ASTNode expr) {
        children().add(expr);
    }

    public void prepend(ASTNode expr) {
        children().add(0, expr);
    }

    @Override
    public String toString(int depth) {
        StringBuilder str = new StringBuilder();
        str.append(tabs(depth)).append(String.format("Type: %s\n", type()));
        str.append(printChildren(depth));
        return str.toString();
    }
}
