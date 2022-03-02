package edu.csci340.parser.ast.nodetypes;

import java.util.List;
import java.util.Objects;

public abstract class ASTNode {
    private Type type;
    private Object value;
    private List<ASTNode> children;

    public ASTNode(Type type) {
        this.type = type;
    }

    public ASTNode(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public ASTNode(Type type, List<ASTNode> children) {
        this.type = type;
        this.children = children;
    }

    public ASTNode(Type type, Object value, List<ASTNode> children) {
        this(type, value);
        this.children = children;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setChildren(List<ASTNode> children) {
        this.children = children;
    }

    public Object value() { return this.value; }

    public Type type() { return this.type; }

    public void setType(Type type) { this.type = type; }

    public List<ASTNode> children() { return this.children; }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        str.append(tabs).append(String.format("Type: %s\n", type));
        if (Objects.nonNull(value)) str.append(tabs).append(String.format("Value: %s\n", value));
        if (Objects.nonNull(children)) {
            str.append(tabs).append("Children:\n");
            depth++;
            tabs.append('\t');
            for (int i = 0; i < children.size(); i++) str.append(tabs).append(String.format("Child %d:\n%s", i, Objects.nonNull(children.get(i)) ? children.get(i).toString(depth + 1) : null));
        }
        return str.toString();
    }

    public enum Type {
        PROGRAM,
        BINARY_EXPRESSION,
        STATEMENT_LIST,
        VARIABLE,
        LIST_TYPE,
        NUMERIC_TYPE,
        STRING_TYPE,
        NUMERIC_LITERAL,
        STRING_LITERAL,
        BOOLEAN_LITERAL,
        BOOLEAN_EXPRESSION,
        BOOLEAN_TYPE,
        EXPRESSION_LIST,
        FILE_READ,
        FILE_WRITE,
        FILE_APPEND,
        INPUT,
        PRINT,
        EMPTY_STATEMENT,
        IDENTIFIER,
        BLOCK,
        CONDITIONAL,
        PREDEFINED,
        EXIT,
        ERROR,
        BREAK,
        RETURN,
        FOR_LOOP,
        WHILE_LOOP,
        UNTIL_LOOP,
        FORMAL_PARAMETER_LIST,
        ACTUAL_PARAMETER_LIST,
        FUNCTION_DEFINITION,
        FUNCTION_CALL,
        SWITCH,
        CASE,
        UNARY_EXPRESSION
    }
}
