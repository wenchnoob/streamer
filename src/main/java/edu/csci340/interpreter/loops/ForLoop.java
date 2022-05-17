package edu.csci340.interpreter.loops;

import edu.csci340.Utils;
import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableType;

import java.util.List;

public class ForLoop {
    public static ASTNode eval(ASTNode node, Scope scope) {
        if (node.type() != ASTNode.Type.FOR_LOOP) return StreamerInterpreter.eval(node, scope);

        ASTNode forCondition = (ASTNode) node.value();

        Scope loopScope = new Scope();
        ASTNode vst = forCondition.firstChild();
        StreamerInterpreter.eval(vst, loopScope);
        vst.firstChild(new VariableType(VariableType.VarType.PREDEFINED));

        List<ASTNode> collection = StreamerInterpreter.eval(forCondition.lastChild(), scope).children();
        List<ASTNode> statements = node.children();

        for (ASTNode v: collection) {
            vst.lastChild(v);
            StreamerInterpreter.eval(vst, loopScope);
            for (ASTNode s: statements) {
                s = Utils.copy(s);
                if (s.type() == ASTNode.Type.RETURN) return s;
                StreamerInterpreter.eval(s, loopScope);
            }
        }

        return null;
    }
}
