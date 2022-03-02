package edu.csci340.parser.ast.nodetypes.expressions.literals;

import edu.csci340.parser.ast.nodetypes.ASTNode;

public class Identifier extends ASTNode {

    private String id;

    public Identifier(String value) {
        super(Type.IDENTIFIER);
        this.id = value;
    }

    public String getId() {
        return this.id;
    }
}
