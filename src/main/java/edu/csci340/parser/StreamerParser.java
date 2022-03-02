package edu.csci340.parser;

import edu.csci340.lexer.Token;
import edu.csci340.lexer.StreamerLexer;
import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.functions.FunctionDefinition;
import edu.csci340.parser.ast.nodetypes.statements.*;
import edu.csci340.parser.subparsers.ExpressionParser;
import edu.csci340.parser.subparsers.VariableParser;


import java.util.Stack;

public class StreamerParser {

    private StreamerLexer lexer;
    private Token lookahead;
    private Stack<Token> backlog;

    public ASTNode parse(String s) {
        lexer = new StreamerLexer();
        lexer.init(s);
        lookahead = lexer.nextToken();
        backlog = new Stack<>();
        return program();
    }

    // All caps denote terminals, all others are non-terminals

    /**
     * Program ::=
     * StatementList
     */
    private ASTNode program() {
        return new Program(statementList(Token.Type.EOF));
    }

    /**
     * StatementList ::= Statement SEMICOLON StatementList | ε
     */
    private List statementList(Token.Type terminator) {
        List statements = new List(ASTNode.Type.STATEMENT_LIST);

        while(this.lookahead.type() != terminator && this.lookahead.type() != Token.Type.DEFAULT) {
            ASTNode statement = statement();

            if (this.lookahead.type() != Token.Type.SEMICOLON) fail();
            this.advance(); // ignore semicolon

            if (this.lookahead.type() == Token.Type.ERROR) {
                advance(); // ignore error keyword
                statement = new ErrorStatement(statement, statement());
                advance(); // ignore virtual semicolon
            }

            statements.append(statement);
        }

        return statements;
    }


    /**
     *  Statement ::=
     *      Expression ; |
     *      VariableStatement ; |
     *      PrintStatement ; |
     *      IfStatement ; |
     *      ExitStatement ; |
     *      ReturnStatement ; |
     *      BreakStatement ; |
     *      ErrorBlock ; |
     *      ForLoop ; |
     *      WhileLoop ; |
     *      FunctionDefinition ; |
     *      SwitchStatement ; |
     *      ;
     */
    private ASTNode statement() {
        return switch (this.lookahead.type()) {
            case INPUT, NEGATION, OPENING_PAREN, NUMERIC_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL -> this.expression();
            case ID -> idExpression();
            case IF -> ifStatement();
            case FOR -> forLoop();
            case WHILE -> whileLoop();
            case UNTIL -> untilLoop();
            case SWITCH -> switchStatement();
            case BUILT_IN_TYPE -> new VariableParser(this).variableStatement();
            case PRINT -> printStatement();
            case EXIT -> exitStatement();
            case RETURN -> returnStatement();
            case BREAK -> breakStatement();
            case OPENING_CURLY -> errorBlock();
            case FUNC -> functionDefinition();
            case SEMICOLON -> new EmptyStatement();
            default -> fail();
        };
    }

    private ASTNode idExpression() {
        if (this.lookahead.type() != Token.Type.ID) fail();
        Token id = this.lookahead;
        advance();
        if (this.lookahead.type() == Token.Type.ASSIGNMENT) {
            pushBack(id);
            return new VariableParser(this).variableAssignment();
        }
        pushBack(id);
        return this.expression();
    }

    /**
     *  Expression
     */
    public ASTNode expression() {
        return switch (this.lookahead.type()) {
            case INPUT, ID, OPENING_PAREN, OPENING_CURLY, NEGATION, STRING_LITERAL, NUMERIC_LITERAL, BOOLEAN_LITERAL -> new ExpressionParser(this).functionCall();
            default -> fail();
        };
    }

    /**
     *  PrintStatement ::= PRINT Expression
     */
    public ASTNode printStatement() {
        if (this.lookahead.type() != Token.Type.PRINT) fail();
        advance();
        return new PrintStatement(expression());
    }

    /**
     *  ExitStatement ::= EXIT Expression
     */
    public ASTNode exitStatement() {
        if (this.lookahead.type() != Token.Type.EXIT) fail();
        advance();
        return new ExitStatement(expression());
    }

    /**
     *  ReturnStatement ::= Return expression
     */
    public ASTNode returnStatement() {
        if (this.lookahead.type() != Token.Type.RETURN) fail();
        advance();
        return new ReturnStatement(expression());
    }

    /**
     *  BreakStatement ::= BREAK
     */
    public ASTNode breakStatement() {
        if (this.lookahead.type() != Token.Type.BREAK) fail();
        advance();
        return new BreakStatement();
    }

    /**
     *  ErrorBlock ::=
     *      Block error Block |
     *      Statement ; error Block
     */
    public ASTNode errorBlock() {
        if (this.lookahead.type() != Token.Type.OPENING_CURLY) return fail();
        ASTNode tryBlock = block();
        advance(); // ignore virtual curly
        if (this.lookahead.type() != Token.Type.ERROR) return tryBlock; // returns early if just finds error Block -- assumes the statement has already been found.
        advance(); // ignore error
        return new ErrorStatement(tryBlock, block());
    }

    /**
     *  IfStatement ::=
     *      IF ( Expression ) Statement ; |
     *      IF ( Expression ) Statement ; ELSE Statement ; |
     *      IF ( Expression ) Statement ; ELSE Block
     *      IF ( Expression ) Block |
     *      IF ( Expression ) Block ELSE Statement ; |
     *      IF ( Expression ) Block ELSE Block
     */
    public ASTNode ifStatement() {
        ASTNode cond;
        ASTNode trueBranch;
        ASTNode falseBranch;
        if (this.lookahead.type() != Token.Type.IF) fail();
        advance();
        if (this.lookahead.type() != Token.Type.OPENING_PAREN) fail();
        advance();
        cond = expression();
        if (this.lookahead.type() != Token.Type.CLOSING_PAREN) fail();
        advance();

        // IF ( Expression ) Statement ;
        if (this.lookahead.type() != Token.Type.OPENING_CURLY) {
            trueBranch = this.statement();
            Token semicol = this.lookahead;
            advance();

            // IF ( Expression ) Statement ;
            if (this.lookahead.type() != Token.Type.ELSE) {
                pushBack(semicol);
                return new ConditionalStatement(cond, trueBranch);
            }

            advance(); // ignore the 'else'
            // IF ( Expression ) Statement ELSE Statement ;
            if (this.lookahead.type() != Token.Type.OPENING_CURLY) {
                return new ConditionalStatement(cond, trueBranch, statement());
            }

            // IF ( Expression ) Statement ELSE Block
            falseBranch = this.block();
            pushBack(new Token(Token.Type.SEMICOLON, null, -1, -1));
            return new ConditionalStatement(cond, trueBranch, falseBranch);
        }

        // IF ( Expression ) Block
        trueBranch = this.block();
        advance(); // drop the virtual semicolon
        if (this.lookahead.type() != Token.Type.ELSE) {
            pushBack(new Token(Token.Type.SEMICOLON, null, -1, -1));
            return new ConditionalStatement(cond, trueBranch);
        }

        advance(); // ignore else
        // IF ( Expression ) Block ELSE Statement ;
        if (this.lookahead.type() != Token.Type.OPENING_CURLY) {
            falseBranch = this.statement();
            return new ConditionalStatement(cond, trueBranch, falseBranch);
        }

        // IF ( Expression ) Block ELSE Block
        falseBranch = this.block();
        return new ConditionalStatement(cond, trueBranch, falseBranch);
    }

    /**
     *  ForLoop ::=
     *      FOR ( Statement : Expression ) Statement ; |
     *      FOR ( Statement : Expression ) Block
     */
    public ASTNode forLoop() {
        if (lookahead.type() != Token.Type.FOR) fail();
        advance();
        if (lookahead.type() != Token.Type.OPENING_PAREN) fail();
        advance();
        if (lookahead.type() != Token.Type.BUILT_IN_TYPE) fail();
        ASTNode capture = statement();
        if (lookahead.type() != Token.Type.COLON) fail();
        advance();
        ASTNode collection = expression();
        if (lookahead.type() != Token.Type.CLOSING_PAREN) fail();
        advance();
        ASTNode body = this.currentLookAhead().type() == Token.Type.OPENING_CURLY ? block() : statement();
        return new ForLoop(capture, collection, body);
    }

    /**
     *  WhileLoop ::=
     *      WHILE ( Expression ) Statement ; |
     *      WHILE ( Expression ) Block
     */
    public ASTNode whileLoop() {
        if (lookahead.type() != Token.Type.WHILE) fail();
        advance();
        if (lookahead.type() != Token.Type.OPENING_PAREN) fail();
        advance();
        ASTNode cond = statement();
        if (lookahead.type() != Token.Type.CLOSING_PAREN) fail();
        advance();
        ASTNode body = lookahead.type() == Token.Type.OPENING_CURLY ? block() : statement();
        return new WhileLoop(cond, body);
    }

    /**
     *  Until ::=
     *      UNTIL VariableStatement ; Block
     */
    public ASTNode untilLoop() {
        if (lookahead.type() != Token.Type.UNTIL) fail();
        advance();
        ASTNode cond = statement();
        if (lookahead.type() != Token.Type.SEMICOLON) fail();
        advance();
        return new UntilLoop(cond, block());
    }

    /**
     *  FunctionDefinition ::=
     *      FUNC id ( FormalParameterList ) Block
     */
    public ASTNode functionDefinition() {
        if (lookahead.type() != Token.Type.FUNC) fail();
        advance();

        if (lookahead.type() != Token.Type.BUILT_IN_TYPE) fail();
        String returnType = lookahead.value();
        advance();

        if (lookahead.type() != Token.Type.ID) fail();
        String id = lookahead.value();
        advance();

        if (lookahead.type() != Token.Type.OPENING_PAREN) fail();
        advance();

        List formalParameterList = new List(ASTNode.Type.FORMAL_PARAMETER_LIST);
        VariableParser vp = new VariableParser(this);
        while(lookahead.type() != Token.Type.CLOSING_PAREN) {
            formalParameterList.append(vp.variableStatement());
            if (lookahead.type() != Token.Type.COMMA) break;
            advance();
        }
        advance();
        return new FunctionDefinition(returnType, id, formalParameterList,  block());
    }

    /**
     *  SwitchStatement ::=
     *      SWITCH ( Expression ) { CaseList } |
     *      SWITCH ( Expression ) { CaseList DefaultCase }
     * */
    public ASTNode switchStatement() {
        if (this.lookahead.type() != Token.Type.SWITCH) fail();
        advance();
        if (this.lookahead.type() != Token.Type.OPENING_PAREN) fail();
        advance();
        ASTNode expr = expression();
        if (this.lookahead.type() != Token.Type.CLOSING_PAREN) fail();
        advance();
        if (this.lookahead.type() != Token.Type.OPENING_CURLY) fail();
        advance();
        List caseList = caseList();
        if (this.lookahead.type() == Token.Type.DEFAULT) caseList.append(defaultCase());
        if (this.lookahead.type() != Token.Type.CLOSING_CURLY) fail();
        advance();
        pushBack(new Token(Token.Type.SEMICOLON, null, -1, -1));
        return new SwitchStatement(expr, caseList);
    }

    /**
     *  CaseList ::=
     *      Case : StatementList ; CaseList
     * */
    public List caseList() {
        List cases = new List(ASTNode.Type.CASE);
        while (this.currentLookAhead().type() == Token.Type.CASE) {
            advance();
            ASTNode expression = this.expression();
            if (this.lookahead.type() != Token.Type.COLON) fail();
            advance();
            List statements = statementList(Token.Type.CASE);
            cases.append(new CaseStatement(expression, statements));
        }
        return cases;
    }

    /**
     *  DefaultCase ::=
     *      DEFAULT : StatementList ;
     * */
    public ASTNode defaultCase() {
        if (lookahead.type() != Token.Type.DEFAULT) fail();
        advance();
        if (lookahead.type() != Token.Type.COLON) fail();
        advance();
        List statements = statementList(Token.Type.CLOSING_CURLY);
        return new CaseStatement(null, statements);
    }

    /**
     *  Block ::=
     *      { StatementList } |
     *      { }
     */
    private ASTNode block() {
        if (this.lookahead.type() != Token.Type.OPENING_CURLY) return statementList(Token.Type.EOF);
        advance();
        ASTNode b = this.lookahead.type() == Token.Type.CLOSING_CURLY ? new BlockStatement(null) : new BlockStatement(statementList(Token.Type.CLOSING_CURLY));
        advance();
        pushBack(new Token(Token.Type.SEMICOLON, null, -1, -1));
        return b;
    }

    public ASTNode fail() throws IllegalStateException {
        throw new IllegalStateException(String.format("Unexpected token <%s> found at line %d column %d", lookahead.value(), lookahead.line(), lookahead.column()));
    }

    public void pushBack(Token token) {
        backlog.push(lookahead);
        this.lookahead = token;
    }

    public Token currentLookAhead() {
        return this.lookahead;
    }

    public void advance() {
        this.lookahead = backlog.isEmpty() ? lexer.nextToken() : backlog.pop();
    }

}
