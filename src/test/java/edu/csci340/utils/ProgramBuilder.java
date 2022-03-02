package edu.csci340.utils;

import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;


public class ProgramBuilder implements Builder {

    List statements;

    private ProgramBuilder() {
        this.statements = new List();
    }

    public static ProgramBuilder builder() {
        return new ProgramBuilder();
    }

    public ExpressionBuilder expr() {
        return new ExpressionBuilder(this);
    }

    public PrintStatementBuilder printStatement() {
        return new PrintStatementBuilder(this);
    }

    public VariableStatementBuilder variableStatement() {
        return new VariableStatementBuilder(this);
    }

    public ProgramBuilder id(String id) {
        statements.append(new Identifier(id));
        return this;
    }

    public ProgramBuilder literal(int n) {
        statements.append(new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(n)));
        return this;
    }

    public ProgramBuilder literal(boolean b) {
        statements.append(new Literal(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(b)));
        return this;
    }

    public ProgramBuilder literal(String s) {
        statements.append(new Literal(ASTNode.Type.STRING_LITERAL, String.valueOf(s)));
        return this;
    }

    public void addStatement(ASTNode statement) {
        statements.append(statement);
    }

    public Program build() {
        return new Program(statements);
    }
}
