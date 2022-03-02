package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

import java.util.Objects;

public class CaseStatement extends ASTNode {
    public ASTNode constExpression;
    public CaseStatement(ASTNode constExpression, List statements) {
        super(Type.CASE, statements);
        this.constExpression = constExpression;
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        if (Objects.nonNull(constExpression)) str.append(tabs).append(String.format("Const Expression: \n%s", constExpression.toString(depth + 1)));
        if (Objects.nonNull(children())) {
            str.append(tabs).append("Children:\n");
            depth++;
            tabs.append('\t');
            for (int i = 0; i < children().size(); i++) str.append(tabs).append(String.format("Child %d:\n%s", i, Objects.nonNull(children().get(i)) ? children().get(i).toString(depth + 1) : null));
        }        return str.toString();
    }
}
