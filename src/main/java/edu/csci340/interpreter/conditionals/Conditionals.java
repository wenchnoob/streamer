package edu.csci340.interpreter.conditionals;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.Objects;

public class Conditionals {
    public static ASTNode eval(ASTNode node, Scope scope) {
        node.value(StreamerInterpreter.eval((ASTNode) node.value(), scope));
        Object val = ((ASTNode)node.value()).value();
        boolean hasFalse = node.children().size() == 2;

        if (val instanceof Boolean b) {
            if (b) return StreamerInterpreter.eval(node.firstChild(), scope);
        } else {
            if (Objects.nonNull(val)) return StreamerInterpreter.eval(node.firstChild(), scope);
        }

        if (hasFalse) return StreamerInterpreter.eval(node.lastChild(), scope);
        return null;
    }
}
