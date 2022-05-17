package edu.csci340.interpreter.loops;

import edu.csci340.Streamer;
import edu.csci340.Utils;
import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;
import java.util.stream.Collectors;

public class WhileLoop {
    public static ASTNode eval(ASTNode node, Scope scope) {
        if (node.type() != ASTNode.Type.WHILE_LOOP) return StreamerInterpreter.eval(node, scope);

        ASTNode condition = (ASTNode) node.value();
        List<ASTNode> statements = node.children();

        ASTNode cond;
       while (true) {
           cond = Utils.copy(condition);
           cond = StreamerInterpreter.eval(cond, scope);
           if (!Utils.isTruthy(cond)) break;
           List<ASTNode> body = statements.stream().map(Utils::copy).collect(Collectors.toList());
           for (ASTNode n: body) {
               if (n.type() == ASTNode.Type.RETURN) return n;
               StreamerInterpreter.eval(n, scope);
           }
       }

        return null;
    }
}
