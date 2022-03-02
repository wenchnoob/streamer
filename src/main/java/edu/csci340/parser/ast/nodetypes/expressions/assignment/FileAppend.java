package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class FileAppend extends ASTNode {
    public FileAppend(ASTNode src, ASTNode child) {
        super(Type.FILE_APPEND, List.of(src, child));
    }
}
