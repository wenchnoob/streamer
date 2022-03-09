package edu.csci340.parser.ast.nodetypes;

import static edu.csci340.Utils.tabs;

public class Program extends ASTNode {
    public Program(List statements) {
        super(Type.PROGRAM, null, statements.children());
    }

    @Override
    public String toString(int depth) {
        StringBuilder str = new StringBuilder();
        str.append(tabs(depth)).append(String.format("Type: %s\n", type()));
        str.append(printChildren(depth));
        return str.toString();
    }
}
