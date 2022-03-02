package edu.csci340.parser.ast.nodetypes.statements;

import edu.csci340.parser.ast.nodetypes.ASTNode;

import java.util.List;
import java.util.Objects;

public class ConditionalStatement extends ASTNode {

    private ASTNode condition;
    private ASTNode trueBranch;
    private ASTNode falseBranch;

    public ConditionalStatement(ASTNode condition, ASTNode trueBranch) {
        super(Type.CONDITIONAL, List.of(condition, trueBranch));
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public ConditionalStatement(ASTNode condition, ASTNode trueBranch, ASTNode falseBranch) {
        super(Type.CONDITIONAL, List.of(condition, trueBranch, falseBranch));
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type()));
        str.append(tabs).append(String.format("Condition: \n%s", condition.toString(depth + 1)));
        str.append(tabs).append(String.format("True Branch: \n%s", trueBranch.toString(depth + 1)));
        if (Objects.nonNull(falseBranch)) str.append(tabs).append(String.format("False Branch: \n%s", falseBranch.toString(depth + 1)));
        return str.toString();
    }
}
