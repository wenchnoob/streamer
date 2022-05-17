package edu.csci340.utils;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableStatement;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VariableStatementBuilder implements Builder {
    private String id;
    private ASTNode type;
    private Builder parent;

    public VariableStatementBuilder(Builder parent) {
        this.parent = parent;
    }

    public ExpressionBuilder expr() {
        return new ExpressionBuilder(this);
    }

    public TypeBuilder type() {
        return new TypeBuilder(this);
    }

    public void setType(ASTNode type) {
        this.type = type;
    }

    public VariableStatementBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ProgramBuilder end(ASTNode value) {
        if (parent instanceof ProgramBuilder pb) {
            pb.addStatement(new VariableStatement(type, id, value));
            return pb;
        }
        return null;
    }

    @Override
    public ASTNode build() {
        return parent.build();
    }

    public static class TypeBuilder implements Builder {

        private Builder parent;
        private List<VariableType.VarType> types;

        public TypeBuilder(Builder parent) {
            this.parent = parent;
            this.types = new ArrayList<>();
        }

        public TypeBuilder of(VariableType.VarType t) {
            types.add(t);
            return this;
        }

        public TypeBuilder and(VariableType.VarType t) {
            return of(t);
        }

        public ASTNode endType() {
           return new VariableType(types);
        }

        public VariableStatementBuilder end() {
            if (parent instanceof VariableStatementBuilder vsb) {
                vsb.setType(endType());
                return vsb;
            } else if (parent instanceof TypeBuilder tb) {
                return tb.end();
            }
            return null;
        }

        @Override
        public ASTNode build() {
            return parent.build();
        }
    }
}
