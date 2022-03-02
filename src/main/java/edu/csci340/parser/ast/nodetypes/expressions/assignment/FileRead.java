package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class FileRead extends ASTNode {
    public FileRead(ASTNode child) {
        super(Type.FILE_READ, List.of(child));
    }
}
