package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;

public class FileWrite extends ASTNode {
    public FileWrite(ASTNode src, ASTNode child) {
        super(Type.FILE_WRITE, List.of(src, child));
    }
}
