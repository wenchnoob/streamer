package edu.csci340.parser.ast.nodetypes;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ASTNode {

    @NonNull
    private Type type;

    private Object value;

    private List<ASTNode> children;

    public ASTNode(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public ASTNode(Type type, Object value, List<ASTNode> children) {
        this(type, value);
        this.children = children;
    }

    public ASTNode firstChild(ASTNode child) {
        return nthChild(0, child);
    }

    public ASTNode lastChild(ASTNode child) {
        return nthChild(children.size() - 1, child);
    }

    public ASTNode nthChild(int n, ASTNode child) {
        children.set(n, child);
        return child;
    }

    public ASTNode firstChild() {
        return nthChild(0);
    }

    public ASTNode lastChild() {
        return nthChild(children.size() - 1);
    }

    public ASTNode nthChild(int n) {
        return children.get(n);
    }

//    public String toString() {
//        return "Program: \n" + printChildren(1);
//    }

    public String toString(int depth) { return "Not Implemented"; }

    public String printChildren(int depth) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < depth; i++) tabs.append('\t');
        StringBuilder str = new StringBuilder();
        if (Objects.nonNull(children)) {
            str.append(tabs).append("Children:\n");
            depth++;
            tabs.append('\t');
            for (int i = 0; i < children.size(); i++) str.append(tabs).append(String.format("Child %d:\n%s", i, Objects.nonNull(nthChild(i)) ? nthChild(i).toString(depth + 1) : ""));
        }
        return str.toString();
    }

    public enum Type {
        PROGRAM,
        BINARY_EXPRESSION,
        STATEMENT_LIST,
        VARIABLE,
        VARIABLE_TYPE,
        NUMERIC_LITERAL,
        STRING_LITERAL,
        BOOLEAN_LITERAL,
        EXPRESSION_LIST,
        FILE_READ,
        FILE_WRITE,
        FILE_APPEND,
        INPUT,
        PRINT,
        EMPTY_STATEMENT,
        IDENTIFIER,
        FOR_CONDITION,
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
