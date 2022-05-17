package edu.csci340.parser.subparsers;

import static edu.csci340.lexer.Token.Type.*;
import edu.csci340.lexer.Token;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.*;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.statements.FileAppend;
import edu.csci340.parser.ast.nodetypes.statements.FileWrite;

import java.util.ArrayList;
import java.util.List;

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
        return new VariableStatement(new VariableType(VariableType.VarType.PREDEFINED), id.value(), parent.expression());
    }

    public ASTNode parseType() {
        Token la = parent.currentLookAhead();
        if (la.type() != BUILT_IN_TYPE) return null;
        parent.advance();

        return switch (la.value()) {
            case "void" -> new VariableType(VariableType.VarType.VOID);
            case "text" -> new VariableType(VariableType.VarType.TEXT);
            case "num" -> new VariableType(VariableType.VarType.NUM);
            case "bool" -> new VariableType(VariableType.VarType.BOOL);
            case "list" -> new VariableType(parseListTypeRecursive());
            default -> parent.fail();
        };
    }

    public java.util.List<VariableType.VarType> parseListType() {
        Token la = parent.currentLookAhead();
        if (la.type() != BUILT_IN_TYPE) return List.of();
        parent.advance();

        return switch (la.value()) {
            case "text" -> List.of(VariableType.VarType.TEXT);
            case "num" -> List.of(VariableType.VarType.NUM);
            case "bool" -> List.of(VariableType.VarType.BOOL);
            case "list" -> parseListTypeRecursive();

            default -> {
                parent.fail();
                yield List.of();
            }
        };
    }

    private List<VariableType.VarType> parseListTypeRecursive() {
        Token la = parent.currentLookAhead();
        if (!la.value().equals("<")) parent.fail();
        parent.advance();
        List<VariableType.VarType> types = new ArrayList<>();
        types.add(VariableType.VarType.LIST);
        types.addAll(parseListType());
        la = parent.currentLookAhead();

        if (la.value().equals(">")) {
            parent.advance();
        } else if (la.value().equals(">>")){
            parent.advance();
            parent.pushBack(new Token(RELATIONAL_OPERATOR, ">", -1, -1));
        } else parent.fail();
        return types;
    }
}
