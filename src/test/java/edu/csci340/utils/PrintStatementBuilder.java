package edu.csci340.utils;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.statements.PrintStatement;

public class PrintStatementBuilder implements Builder {

    private Builder parent;

    public PrintStatementBuilder(Builder parent) {
        this.parent = parent;
    }

    public ExpressionBuilder expr() {
        return new ExpressionBuilder(this);
    }

    public ProgramBuilder end(ASTNode toPrint) {
        if (parent instanceof ProgramBuilder pb) {
            pb.addStatement(new PrintStatement(toPrint));
            return pb;
        }
        return null;
    }

    @Override
    public ASTNode build() {
        return parent.build();
    }
}
