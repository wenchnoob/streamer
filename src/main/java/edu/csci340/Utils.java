package edu.csci340;

import edu.csci340.lexer.Token;
import edu.csci340.parser.StreamerParser;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static String tabs(int n) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < n; i++) tabs.append('\t');
        return tabs.toString();
    }

    public static ASTNode copy(ASTNode src) {
        if (src == null) return null;
        ASTNode.ASTNodeBuilder nsrc = src.toBuilder();
        if (src.value() instanceof ASTNode vn) nsrc.value(copy(vn));
        List<ASTNode> children = new ArrayList<>();
        if (Objects.nonNull(src.children()))
            for (ASTNode c : src.children()) {
                children.add(copy(c));
            }
        return nsrc.children(children).build();
    }

    public static boolean isTruthy(ASTNode node) {
        if (Objects.isNull(node)) return false;
        return switch (node.type()) {
            case BOOLEAN_LITERAL -> Boolean.parseBoolean(String.valueOf(node.value()));
            case STRING_LITERAL -> {
                String s = String.valueOf(node.value());
                if (s.equals("true") || s.equals("false")) yield Boolean.parseBoolean(s);
                if (s.equals("")) yield false;
                yield true;
            }
            case NUMERIC_LITERAL -> {
                double num = Double.parseDouble(String.valueOf(node.value()));
                yield !(num == 0);
            }
            default -> false;
        };
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
