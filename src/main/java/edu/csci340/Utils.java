package edu.csci340;

import edu.csci340.lexer.Token;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;

public class Utils {

    public static String tabs(int n) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < n; i++) tabs.append('\t');
        return tabs.toString();
    }

    public static Literal parseLiteralTokenToNode(Token tok) {
        return switch (tok.type()) {
            case NUMERIC_LITERAL -> new Literal(ASTNode.Type.NUMERIC_LITERAL, tok.value());
            case STRING_LITERAL -> new Literal(ASTNode.Type.STRING_LITERAL, tok.value());
            case BOOLEAN_LITERAL -> new Literal(ASTNode.Type.BOOLEAN_LITERAL, tok.value());
            default -> null;
        };
    }

    public static ASTNode getLiteral(StreamerParser parent, Token.Type p) {
        if (parent.currentLookAhead().type() != p) parent.fail();
        Token cur = parent.currentLookAhead();
        parent.advance();
        Literal lit = parseLiteralTokenToNode(cur);
        if (lit == null) parent.fail();
        return lit;
    }

}
