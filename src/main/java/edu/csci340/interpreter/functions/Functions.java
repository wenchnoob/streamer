package edu.csci340.interpreter.functions;

import edu.csci340.Utils;
import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.errors.ProgramError;
import edu.csci340.interpreter.scopes.Scope;
import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Functions {

    private static Hashtable<FunctionDefinition, ASTNode> registeredFunctions = new Hashtable<>();

    private Functions() {
    }

    private static ASTNode addFunction(ASTNode fnDef) {
        registeredFunctions.put(new FunctionDefinition((String) fnDef.value(), fnDef.nthChild(1).children()), fnDef.lastChild());
        return null;
    }

    private static ASTNode callFunction(String name, List<ASTNode> arguments, Scope scope) {
        if (BuiltIns.isBuiltIn(name)) return BuiltIns.eval(name, arguments, scope);

        List<FunctionDefinition> ls = registeredFunctions.keySet().stream().filter(fnd -> fnd.name().equals(name)).collect(Collectors.toList());

        if (ls.size() <= 0) {
            throw new ProgramError("The function <" + name + "> does not exist.");
        }

        FunctionDefinition fnd = ls.get(0);
        List<ASTNode> formalParams = fnd.formalParams();
        ASTNode fn = Utils.copy(registeredFunctions.get(fnd));

        Scope cur = new Scope();
        for (int i = 0; i < formalParams.size(); i++) {
            String argName = (String) formalParams.get(i).value();
            ASTNode arg = StreamerInterpreter.eval(arguments.get(i), scope);
            cur.setVariable(argName, arg.value());
        }


        for (int i = 0; i < fn.children().size(); i++) {
            ASTNode res = fn.nthChild(i, StreamerInterpreter.eval(fn.nthChild(i), cur));
            if (res != null && res.type() == ASTNode.Type.RETURN) return StreamerInterpreter.eval((ASTNode) res.value(), cur);
        }
        return null;
    }

    public static ASTNode eval(ASTNode node, Scope scope) {
        return switch (node.type()) {
            case FUNCTION_DEFINITION -> addFunction(node);
            case FUNCTION_CALL -> callFunction((String) node.value(), node.firstChild().children(), scope);
            default -> null;
        };
    }

}
