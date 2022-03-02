package edu.csci340.parser.ast.nodetypes;


import java.util.ArrayList;

public class List extends ASTNode {

    private java.util.List<ASTNode> items;

    public List(Type type) {
        super(type);
        this.items = new ArrayList<>();
    }

    public List(ASTNode ...items) {
        super(Type.STATEMENT_LIST);
        this.items = new ArrayList<>();
        if (items != null) this.items.addAll(java.util.List.of(items));
    }

    public void append(ASTNode expr) {
        this.items.add(expr);
    }

    public void prepend(ASTNode expr) {
        this.items.add(0, expr);
    }

    public java.util.List<ASTNode> nodes() {
        return this.items;
    }
}
