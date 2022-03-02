package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

import java.util.Objects;

public class SwitchStatement extends ASTNode {
    ASTNode expr;

    public SwitchStatement(ASTNode expr, List cases) {
        super(Type.SWITCH, cases);
        this.expr = expr;
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Expression: \n%s", expr.toString(depth + 1)));
        if (Objects.nonNull(children())) {
            str.append(tabs).append("Children:\n");
            depth++;
            tabs.append('\t');
            for (int i = 0; i < children().size(); i++) str.append(tabs).append(String.format("Case %d:\n%s", i, Objects.nonNull(children().get(i)) ? children().get(i).toString(depth + 1) : null));
        }
        return str.toString();
    }
}
