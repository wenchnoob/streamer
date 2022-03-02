package edu.csci340.parser.ast.nodetypes;

public class Program extends ASTNode {

    private ASTNode statements;

    public Program(List statements) {
        super(Type.PROGRAM, statements.nodes());
        this.statements = statements;
    }

    public ASTNode statements() {
        return this.statements;
    }
}
