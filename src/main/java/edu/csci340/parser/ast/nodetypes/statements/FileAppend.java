package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

import static edu.csci340.Utils.tabs;

public class FileAppend extends ASTNode {
    public FileAppend(ASTNode src, ASTNode child) {
        super(Type.FILE_APPEND, src, List.of(child));
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value:\n%s", ((ASTNode)value()).toString(depth + 1)));
        str.append(printChildren(depth));
        return str.toString();
    }
}
