package edu.csci340.lexer;

public record Token(Type type, String value, int line, int column) {
    public enum Type {
        EOF,
        NEWLINE,
        BUILT_IN_TYPE,
        BOOLEAN_LITERAL,
        OPERATOR,
        ADDITIVE_OPERATOR,
        MULTIPLICATIVE_OPERATOR,
        NEGATION,
        RELATIONAL_OPERATOR,
        EQUALITY_OPERATOR,
        AND,
        OR,
        ASSIGNMENT,
        OPENING_CURLY,
        CLOSING_CURLY,
        OPENING_PAREN,
        CLOSING_PAREN,
        OPENING_FILTER,
        CLOSING_FILTER,
        OPENING_SQUARE,
        CLOSING_SQUARE,
        COMMA,
        COLON,
        SEMICOLON,
        NUMERIC_LITERAL,
        STRING_LITERAL,
        ID,
        PRINT,
        INPUT,
        EXIT,
        UNTIL,
        ERROR,
        FUNC,
        IF,
        ELSE,
        FOR,
        WHILE,
        BREAK,
        RETURN,
        SWITCH,
        CASE,
        DEFAULT,
        FILE_READ,
        FILE_WRITE,
        FILE_APPEND
    }
}