package edu.csci340.parser.subparsers;

import static edu.csci340.lexer.Token.Type.*;
import edu.csci340.lexer.Token;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.*;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;

public class VariableParser {

    private StreamerParser parent;

    public VariableParser(StreamerParser parent) {
        this.parent = parent;
    }

    /**
     *  VariableStatement ::=
     *      TYPE ID |
     *      TYPE ID = Expression |
     *      TYPE ID << Expression |
     *      ID >> Expression |
     *      ID >>+ Expression
     * */
    public ASTNode variableStatement() {
        ASTNode type = parseType();
        if (parent.currentLookAhead().type() != ID) parent.fail();
        Token id = parent.currentLookAhead();
        parent.advance();
        Token la = parent.currentLookAhead();
        return switch (la.type()) {
            case ASSIGNMENT -> {
                parent.advance();
                yield new VariableStatement(type, id.value(), parent.expression());
            }
            case FILE_READ -> {
                parent.advance();
                yield new VariableStatement(type, id.value(), new FileRead(parent.expression()));
            }
            case FILE_WRITE -> {
                parent.advance();
                yield new FileWrite(new Identifier(id.value()), parent.expression());
            }
            case FILE_APPEND -> {
                parent.advance();
                yield new FileAppend(new Identifier(id.value()), parent.expression());
            }
            default -> new VariableStatement(type, id.value(), null);
        };
    }

    public ASTNode variableAssignment() {
        if (parent.currentLookAhead().type() != ID) parent.fail();
        Token id = parent.currentLookAhead();
        parent.advance();
        Token assign = parent.currentLookAhead();
        if (assign.type() != ASSIGNMENT) parent.fail();
        parent.advance();
        return new VariableStatement(new VariableType(ASTNode.Type.PREDEFINED), id.value(), parent.expression());
    }

    private ASTNode parseType() {
        Token la = parent.currentLookAhead();
        if (la.type() != BUILT_IN_TYPE) return null;
        parent.advance();
        return switch (la.value()) {
            case "text" -> new VariableType(ASTNode.Type.STRING_TYPE);
            case "num" -> new VariableType(ASTNode.Type.NUMERIC_TYPE);
            case "bool" -> new VariableType(ASTNode.Type.BOOLEAN_TYPE);
            case "list" -> {
                parent.advance();
                VariableType varType = new VariableType(ASTNode.Type.LIST_TYPE, parseType());
                la = parent.currentLookAhead();
                if (la.value().equals(">")) parent.advance();
                else if (la.value().equals(">>")) {
                    parent.advance();
                    parent.pushBack(new Token(RELATIONAL_OPERATOR, ">", la.line(), la.column() + 1));
                } else parent.fail();
                yield varType;
            }
            default -> parent.fail();
        };
    }
}
