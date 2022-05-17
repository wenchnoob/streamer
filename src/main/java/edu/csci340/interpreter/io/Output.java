package edu.csci340.interpreter.io;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.statements.PrintStatement;

import java.util.function.BinaryOperator;

public class Output {
    public static ASTNode eval(ASTNode node, Scope scope) {
        if (node instanceof PrintStatement || node.type() == ASTNode.Type.PRINT) {
            node.value(StreamerInterpreter.eval((ASTNode) node.value(), scope));
            Object toPrint = nodeToString((ASTNode)node.value());
            System.out.print(toPrint);
            return null;
        }
        return node;
    }

    static BinaryOperator<Object> write = (l, r) -> {
        return null;
    };

    static BinaryOperator<Object> append = (l, r) -> {
        return null;
    };

    public static String nodeToString(ASTNode node) {
        return switch (node.type()) {
            case EXPRESSION_LIST -> {
                StringBuilder sb = new StringBuilder();
                sb.append("{ ");
                java.util.List<ASTNode> children = node.children();
                for (int i = 0; i < children.size() - 1; i++) sb.append(nodeToString(children.get(i))).append(", ");
                if (children.size() >= 1) sb.append(nodeToString(children.get(children.size() - 1)));
                sb.append(" }");
                yield sb.toString();
            }
            case NUMERIC_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL -> String.valueOf(node.value());
            default -> node.toString(0);
        };
    }
}
