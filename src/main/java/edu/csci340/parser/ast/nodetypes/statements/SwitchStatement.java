package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

import static edu.csci340.Utils.tabs;

public class SwitchStatement extends ASTNode {

    public SwitchStatement(ASTNode expr, List cases) {
        super(Type.SWITCH, expr, cases.children());
    }

    @Override
    public String toString(int depth) {
        String tabs = tabs(depth);
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Value:\n%s", ((ASTNode)value()).toString(depth + 1)));
        str.append(printChildren(depth));
        return str.toString();
    }
}
