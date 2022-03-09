package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;

import java.util.Objects;

public class CaseStatement extends ASTNode {

    public CaseStatement(ASTNode constExpression, List statements) {
        super(Type.CASE, constExpression, statements.children());
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        Object value = value();
        if (Objects.nonNull(value)) str.append(tabs).append(String.format("Const Expression: \n%s", ((ASTNode)value).toString(depth + 1)));
        java.util.List<ASTNode> children = children();
        if (Objects.nonNull(children)) {
            str.append(tabs).append("Children:\n");
            depth++;
            tabs.append('\t');
            for (int i = 0; i < children().size(); i++) str.append(tabs).append(String.format("Child %d:\n%s", i, Objects.nonNull(children.get(i)) ? children.get(i).toString(depth + 1) : null));
        }        return str.toString();
    }
}
