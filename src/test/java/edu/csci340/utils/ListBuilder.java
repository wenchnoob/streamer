package edu.csci340.utils;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

public class ListBuilder implements Builder{

    Builder parent;
    List items;

    public ListBuilder(Builder parent) {
        this.parent = parent;
    }

    public void addStatement(ASTNode statement) {
        items.append(statement);
    }

    public ProgramBuilder end() {
        if (parent instanceof ProgramBuilder pb) {
            pb.addStatement(build());
        } else if (parent instanceof VariableStatementBuilder vsb) {
            return vsb.end(items);
        } else if (parent instanceof PrintStatementBuilder psb) {
            return psb.end(items);
        }
        return null;
    }

    @Override
    public ASTNode build() {
        return null;
    }
}
