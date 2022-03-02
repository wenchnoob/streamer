package edu.csci340.parser.subparsers;

import edu.csci340.Utils;
import edu.csci340.lexer.Token;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.expressions.BinaryExpression;
import edu.csci340.parser.ast.nodetypes.expressions.InputExpression;
import edu.csci340.parser.ast.nodetypes.List;
import edu.csci340.parser.ast.nodetypes.expressions.UnaryExpression;
import edu.csci340.parser.ast.nodetypes.functions.FunctionCall;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;


public class ExpressionParser {

    StreamerParser parent;

    public ExpressionParser(StreamerParser parent) {
        this.parent = parent;
    }

    /**
     *  FunctionCall ::=
     *      ID ( InputExpressionList ) |
     *      InputExpression
     * */
    public ASTNode functionCall() {
        if (parent.currentLookAhead().type() != Token.Type.ID) return this.inputExpression();
        Token idToken = parent.currentLookAhead();
        parent.advance();
        if (parent.currentLookAhead().type() != Token.Type.OPENING_PAREN) {
            parent.pushBack(idToken);
            return this.inputExpression();
        }
        parent.advance();
        String id = idToken.value();
        List actualParameterList = new List(ASTNode.Type.ACTUAL_PARAMETER_LIST);
        while(parent.currentLookAhead().type() != Token.Type.CLOSING_PAREN) {
            actualParameterList.append(inputExpression());
            if (parent.currentLookAhead().type() != Token.Type.COMMA) break;
            parent.advance();
        }
        parent.advance();
        return new FunctionCall(id, actualParameterList);
    }

    /**
     *  InputExpression ::= INPUT Expression
     * */
    public ASTNode inputExpression() {
        if (parent.currentLookAhead().type() != Token.Type.INPUT) return listAccess();
        parent.advance();
        return new InputExpression(listAccess());
    }

    /**
     *  List Access ::=
     *      List Filter [ ListFilter ]
     * */
    public ASTNode listAccess() {
        ASTNode lhs = listFilter();
        if (parent.currentLookAhead().type() != Token.Type.OPENING_SQUARE) return lhs;
        parent.advance();
        ASTNode rhs = listFilter();
        if (parent.currentLookAhead().type() != Token.Type.CLOSING_SQUARE) parent.fail();
        parent.advance();
        return new BinaryExpression("List Access", lhs, rhs);
    }

    /**
     *  List Filter ::=
     *      List Expression [? List Expression ?]
     * */
    public ASTNode listFilter() {
        ASTNode lhs = listExpression();
        if (parent.currentLookAhead().type() != Token.Type.OPENING_FILTER) return lhs;
        parent.advance();
        ASTNode rhs = listExpression();
        if (parent.currentLookAhead().type() != Token.Type.CLOSING_FILTER) parent.fail();
        parent.advance();
        return new BinaryExpression("List Filter", lhs, rhs);
    }



    /**
     *  List Expression ::=
     *      List Expression , Expression |
     *      Expression
     * */
    public ASTNode listExpression() {
        if (parent.currentLookAhead().type() != Token.Type.OPENING_CURLY) return this.expression();
        parent.advance();

        List expressionList = new List(ASTNode.Type.EXPRESSION_LIST);
        if (parent.currentLookAhead().type() == Token.Type.CLOSING_CURLY) {
            parent.advance();
            return expressionList;
        }

        while(parent.currentLookAhead().type() != Token.Type.CLOSING_CURLY) {
            ASTNode elem = expression();
            if (parent.currentLookAhead().type() != Token.Type.COMMA) break;
            parent.advance(); // ignore comma
            expressionList.append(elem);
        }

        parent.advance();
        return expressionList;
    }

    /**
     *  Expression ::=
     *     Boolean Expression |
     * */
    public ASTNode expression() {
        return this.booleanExpression();
    }

    /**
     *  Boolean Expression ::=
     *      Or Expression
     * */
    public ASTNode booleanExpression() {
        return this.orExpression();
    }

    /**
     *  Or Expression ::=
     *      Or Expression OR And Expression |
     *      And Expression
     * */
    public ASTNode orExpression() {
        ASTNode lhs = andExpression();
        Token la = parent.currentLookAhead();
        while (la.type() == Token.Type.OR) {
            Token op = la;
            parent.advance();
            ASTNode rhs = andExpression();
            lhs = new BinaryExpression(op.value(), lhs, rhs);
            la = parent.currentLookAhead();
        }
        return lhs;
    }

    /**
     *  And Expression ::=
     *      And Expression AND Equality Expression |
     *      Equality Expression
     * */
    public ASTNode andExpression() {
        ASTNode lhs = equalityExpression();
        Token la = parent.currentLookAhead();
        while (la.type() == Token.Type.AND) {
            Token op = la;
            parent.advance();
            ASTNode rhs = equalityExpression();
            lhs = new BinaryExpression(op.value(), lhs, rhs);
            la = parent.currentLookAhead();
        }
        return lhs;
    }

    /**
     *  Equality Expression ::=
     *      Equality Expression == Relational Expression |
     *      Equality Expression != Relational Expression |
     *      Relational Expression
     * */
    public ASTNode equalityExpression() {
        ASTNode lhs = relationalExpression();
        Token la = parent.currentLookAhead();
        while (la.type() == Token.Type.EQUALITY_OPERATOR) {
            Token op = la;
            parent.advance();
            ASTNode rhs = relationalExpression();
            lhs = new BinaryExpression(op.value(), lhs, rhs);
            la = parent.currentLookAhead();
        }
        return lhs;
    }

    /**
     *  Relational Expression ::=
     *      Relational Expression > Primary Expression |
     *      Relational Expression < Primary Expression |
     *      Relational Expression <= Primary Expression |
     *      Relational Expression >= Primary Expression |
     *      Primary Expression
     * */
    public ASTNode relationalExpression() {
        ASTNode lhs = additiveExpression();
        Token la = parent.currentLookAhead();
        while (la.type() == Token.Type.RELATIONAL_OPERATOR) {
            Token op = la;
            parent.advance();
            ASTNode rhs = additiveExpression();
            lhs = new BinaryExpression(op.value(), lhs, rhs);
            la = parent.currentLookAhead();
        }
        return lhs;
    }

    /**
     *  AdditiveExpression ::=
     *      MultiplicativeExpression |
     *      AdditiveExpression + MultiplicativeExpression |
     *      AdditiveExpression - MultiplicativeExpression
     * */
    public ASTNode additiveExpression() {
        ASTNode lhs = multiplicativeExpression();
        Token lookahead = parent.currentLookAhead();
        while (lookahead.type() == Token.Type.ADDITIVE_OPERATOR) {
            Token op = lookahead;
            parent.advance();
            ASTNode rhs = multiplicativeExpression();
            lhs = new BinaryExpression(op.value(), lhs, rhs);
            lookahead = parent.currentLookAhead();
        }
        return lhs;
    }

    /**
     *  MultiplicativeExpression ::=
     *      UnaryExpression |
     *      MultiplicativeExpression * UnaryExpression |
     *      MultiplicativeExpression / UnaryExpression |
     *      MultiplicativeExpression % UnaryExpression
     * */
    public ASTNode multiplicativeExpression() {
        ASTNode lhs = unaryExpression();
        Token lookahead = parent.currentLookAhead();
        while (lookahead.type() == Token.Type.MULTIPLICATIVE_OPERATOR) {
            Token op = lookahead;
            parent.advance();
            ASTNode rhs = unaryExpression();
            lhs = new BinaryExpression(op.value(), lhs, rhs);
            lookahead = parent.currentLookAhead();
        }
        return lhs;
    }

    /**
     *  UnaryExpression ::=
     *      ! PrimaryExpression |
     *      - PrimaryExpression
     * */
    public ASTNode unaryExpression() {
        Token la = parent.currentLookAhead();
        return switch (la.type()) {
            case NEGATION, ADDITIVE_OPERATOR -> {
                if (la.value().equals("-")) yield this.primaryExpression();
                parent.advance();
                yield  new UnaryExpression(la.value(), this.unaryExpression());
            }
            default -> this.primaryExpression();
        };
    }


    /**
     *  PrimaryExpression ::=
     *      ListExpression |
     *      ParenthesizedExpression |
     *      ID |
     *      Literal
     * */
    public ASTNode primaryExpression() {
        Token la = parent.currentLookAhead();
        return switch (la.type()) {
            case OPENING_CURLY, INPUT -> parent.expression();
            case OPENING_PAREN -> parenthesizedExpression();
            case ID -> {
                parent.advance();
                if (parent.currentLookAhead().type() == Token.Type.OPENING_PAREN) {
                    parent.pushBack(la);
                    yield functionCall();
                }

                if (parent.currentLookAhead().type() == Token.Type.OPENING_SQUARE) {
                    parent.advance();
                    ASTNode rhs = parent.expression();
                    if (parent.currentLookAhead().type() != Token.Type.CLOSING_SQUARE) parent.fail();
                    parent.advance();
                    yield new BinaryExpression("List Access", new Identifier(la.value()), rhs);
                }

                if (parent.currentLookAhead().type() == Token.Type.OPENING_FILTER) {
                    parent.advance();
                    ASTNode rhs = parent.expression();
                    if (parent.currentLookAhead().type() != Token.Type.CLOSING_FILTER) parent.fail();
                    parent.advance();
                    yield new BinaryExpression("List Filter", new Identifier(la.value()), rhs);
                }

                if (parent.currentLookAhead().type() == Token.Type.FILE_WRITE || parent.currentLookAhead().type() == Token.Type.FILE_APPEND) {
                    parent.pushBack(la);
                    yield new VariableParser(parent).variableStatement();
                }

                yield new Identifier(la.value());
            }
            case BOOLEAN_LITERAL, NUMERIC_LITERAL, STRING_LITERAL -> literal();
            default -> parent.fail();
        };
    }


    /**
     *  ParenthesizedExpression ::= ( Expression )
     * */
    public ASTNode parenthesizedExpression() {
        parent.advance();
        ASTNode pExpression = parent.expression();
        parent.advance();
        return pExpression;
    }


    /**
     *  Literal ::=
     *      NUMERIC_LITERAL |
     *      BOOLEAN_LITERAL |
     *      STRING_LITERAL
     * */
    public ASTNode literal() {
        Token la = parent.currentLookAhead();
        return Utils.getLiteral(parent, la.type());
    }
}
