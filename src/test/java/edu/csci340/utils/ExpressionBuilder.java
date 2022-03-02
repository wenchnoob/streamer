package edu.csci340.utils;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;
import edu.csci340.parser.ast.nodetypes.expressions.BinaryExpression;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;

public class ExpressionBuilder implements Builder {

    Object value;
    Builder parent;
    ExpressionBuilder left, right;

    public ExpressionBuilder(Builder parent) {
        this.parent = parent;
    }

    public ExpressionBuilder op(String op) {
        this.value = op;
        return this;
    }

    public ExpressionBuilder rightLiteral(double n) {
        return rightLiteral(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(n));
    }

    public ExpressionBuilder leftLiteral(double n) {
        return leftLiteral(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(n));
    }

    public ExpressionBuilder rightLiteral(boolean b) {
        return rightLiteral(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(b));
    }

    public ExpressionBuilder leftLiteral(boolean b) {
        return leftLiteral(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(b));
    }

    public ExpressionBuilder rightLiteral(String s) {
        return rightLiteral(ASTNode.Type.STRING_LITERAL, s);
    }

    public ExpressionBuilder leftLiteral(String s) {
        return leftLiteral(ASTNode.Type.STRING_LITERAL, s);
    }

    private ExpressionBuilder leftLiteral(ASTNode.Type type, String s) {
        this.left = new ExpressionBuilder(this);
        this.left.value = new Literal(type, s);
        return this.check();
    }

    private ExpressionBuilder rightLiteral(ASTNode.Type type, String s) {
        this.right = new ExpressionBuilder(this);
        this.right.value = new Literal(type, s);
        return this.check();
    }

    public ExpressionBuilder leftId(String id) {
        this.left = new ExpressionBuilder(this);
        this.left.value = new Identifier(id);
        return this.check();
    }

    public ExpressionBuilder rightId(String id) {
        this.right = new ExpressionBuilder(this);
        this.right.value = new Identifier(id);
        return this.check();
    }

    public ExpressionBuilder left() {
        this.left = new ExpressionBuilder(this);
        return this.left;
    }

    public ExpressionBuilder right() {
        this.right = new ExpressionBuilder(this);
        return this.right;
    }

    private ExpressionBuilder check() {
        if (this.left != null && this.right != null && this.parent instanceof ExpressionBuilder eb) return eb;
        return this;
    }

    public ProgramBuilder identifier(String n) {
        this.value = new Identifier(n);
        return end();
    }

    public ProgramBuilder literal(String n) {
        this.value = new Literal(ASTNode.Type.STRING_LITERAL, n);
        return end();
    }

    public ProgramBuilder literal(double d) {
        this.value = new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(d));
        return end();
    }

    public ProgramBuilder literal(boolean b) {
        this.value = new Literal(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(b));
        return end();
    }

    public ProgramBuilder end() {
        if (parent instanceof ProgramBuilder pb) {
            pb.addStatement(build());
        } else if (parent instanceof VariableStatementBuilder vsb) {
          return vsb.end(build());
        } else if (parent instanceof PrintStatementBuilder psb) {
            return psb.end(build());
        }
        return null;
    }

    public ASTNode build() {
        if (parent instanceof ProgramBuilder && this.left != null && this.right != null) {
            return new BinaryExpression((String) value, this.left.build(), this.right.build());
        }
        if (value instanceof Literal l) return l;
        if (value instanceof Identifier i) return i;
        if (!(value instanceof String)) throw new IllegalStateException("Attempting to build expression with illegal node!!!: " + value);
        return new BinaryExpression((String) value, this.left.build(), this.right.build());
    }

}
