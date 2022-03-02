package edu.csci340.parser.ast.nodetypes.expressions.assignment;

import edu.csci340.parser.ast.nodetypes.ASTNode;

public class VariableStatement extends ASTNode {

    private ASTNode varType;
    private ASTNode varValue;

    public VariableStatement(ASTNode varType, String id, ASTNode varValue) {
        super(Type.VARIABLE, id);
        this.varType = varType;
        this.varValue = varValue;
    }

    @Override
    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder(super.toString(depth));
        if (varType != null) str.append(tabs).append(String.format("Variable Type: %s\n", varType.toString(depth + 1)));
        if (varValue != null) str.append(tabs).append(String.format("Variable Value: \n%s\n", varValue.toString(depth + 1)));
        return str.toString();
    }

    public ASTNode varValue() { return this.varValue; }
}
