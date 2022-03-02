package edu.csci340.lexer;

import org.assertj.core.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StreamerLexerTest {

    private StreamerLexer lexer;

    @BeforeEach
    public void setUp() {
        lexer = new StreamerLexer();
    }

    @Test
    public void shouldBuildCorrectly() {
        assertAll("Should build the lexer properly",
                () -> Assertions.assertThat(lexer.hasNextToken())
                        .as("Should have no next token when uninitialized")
                        .isFalse(),
                () -> assertThrows(IllegalStateException.class, () -> lexer.nextToken(), "Should throw IllegalStateException when an attempt is made to retrieve a token from an uninitialized tokenizer"));

        lexer.init("");
        Assertions.assertThat(lexer.nextToken())
                .as("Should return an EOF token when an attempt is made to retrieve a token from a lexer that is at the eof")
                .isEqualTo(new Token(Token.Type.EOF, null, 1, 1));
    }

    @Test
    public void shouldLexNumber() {
        lexer.init("10");
        Assertions.assertThat(lexer.nextToken())
                .as("Should parse a number token")
                .isEqualTo(new Token(Token.Type.NUMERIC_LITERAL, "10", 1, 1));
    }

    @Test
    public void shouldIgnoreWhitespace() {
        lexer.init("""
                  10

                \t\r 15
                """);

        assertAll("Should ignore the whitespace.",
                () -> Assertions.assertThat(lexer.nextToken())
                        .isEqualTo(new Token(Token.Type.NUMERIC_LITERAL, "10", 1, 3)),
                () -> Assertions.assertThat(lexer.nextToken())
                        .isEqualTo(new Token(Token.Type.NUMERIC_LITERAL, "15", 3, 4)));
    }

    @Test
    public void shouldLexStringLiterals() {
        lexer.init("""
                  \"hey there buddy\"
                  \"hey there with \\"escapes\\" \\n \\\\ \"
                """);
        assertAll("Should parse strings",
                () -> Assertions.assertThat(lexer.nextToken())
                        .as("Should parse string with no escapes")
                        .isEqualTo(new Token(Token.Type.STRING_LITERAL, "hey there buddy", 1, 3)),
                () -> Assertions.assertThat(lexer.nextToken())
                        .as("Should parse escape characters \\n \\\" and \\\\")
                        .isEqualTo(new Token(Token.Type.STRING_LITERAL, "hey there with \\\"escapes\\\" \\n \\\\ ", 2, 3)));
    }

    @Test
    public void shouldLexIdentifiers() {
        lexer.init("""
                 voxel
                 _voxel
                  pirate1
                  pp_2_ sup\
                """);

        Assertions.assertThat(lexer.tokens()).isEqualTo(List.of(
                new Token(Token.Type.ID, "voxel", 1, 2),
                new Token(Token.Type.ID, "_voxel", 2, 2),
                new Token(Token.Type.ID, "pirate1", 3, 3),
                new Token(Token.Type.ID, "pp_2_", 4, 3),
                new Token(Token.Type.ID, "sup", 4, 9),
                new Token(Token.Type.EOF, null, 4, 12)
        ));
    }

    @Test
    public void shouldLexPrintStatement() {
        lexer.init("""
                print "Hello \\"world\\"";\
                """);

        Assertions.assertThat(lexer.tokens())
                .as("Should parse print statement")
                .isEqualTo(List.of(
                        new Token(Token.Type.PRINT, "print", 1, 1),
                        new Token(Token.Type.STRING_LITERAL, "Hello \\\"world\\\"", 1, 7),
                        new Token(Token.Type.SEMICOLON, ";", 1, 22),
                        new Token(Token.Type.EOF, null, 1, 23)
                ));
    }

    @Test
    public void shouldLexAssignmentStatement() {
        lexer.init("""
                num two = 2.3;\
                """);
        Assertions.assertThat(lexer.tokens())
                .as("Should parse assignment statement")
                .isEqualTo(List.of(
                        new Token(Token.Type.BUILT_IN_TYPE, "num", 1, 1),
                        new Token(Token.Type.ID, "two", 1, 5),
                        new Token(Token.Type.ASSIGNMENT, "=", 1, 9),
                        new Token(Token.Type.NUMERIC_LITERAL, "2.3", 1, 11),
                        new Token(Token.Type.SEMICOLON, ";", 1, 14),
                        new Token(Token.Type.EOF, null, 1, 15)
                ));
    }

    @Test
    public void shouldLexBuiltInTypes() {
        lexer.init("""
                num text bool list void
                """);
        Assertions.assertThat(lexer.tokens())
                .as("Should parse assignment statement")
                .isEqualTo(List.of(
                        new Token(Token.Type.BUILT_IN_TYPE, "num", 1, 1),
                        new Token(Token.Type.BUILT_IN_TYPE, "text", 1, 5),
                        new Token(Token.Type.BUILT_IN_TYPE, "bool", 1, 10),
                        new Token(Token.Type.BUILT_IN_TYPE, "list", 1, 15),
                        new Token(Token.Type.BUILT_IN_TYPE, "void", 1, 20),
                        new Token(Token.Type.EOF, null, 2, 1)
                ));
    }

    @Test
    public void shouldLexOperators() {
        lexer.init("""
                <<>>+>>==<><=>=+-*/%&&||!!=
                """);
        Assertions.assertThat(lexer.tokens())
                .as("Should parse all basic operator types")
                .isEqualTo(List.of(
                        new Token(Token.Type.FILE_READ, "<<", 1, 1),
                        new Token(Token.Type.FILE_APPEND, ">>+", 1, 3),
                        new Token(Token.Type.FILE_WRITE, ">>", 1, 6),
                        new Token(Token.Type.EQUALITY_OPERATOR, "==", 1, 8),
                        new Token(Token.Type.RELATIONAL_OPERATOR, "<", 1, 10),
                        new Token(Token.Type.RELATIONAL_OPERATOR, ">", 1, 11),
                        new Token(Token.Type.RELATIONAL_OPERATOR, "<=", 1, 12),
                        new Token(Token.Type.RELATIONAL_OPERATOR, ">=", 1, 14),
                        new Token(Token.Type.ADDITIVE_OPERATOR, "+", 1, 16),
                        new Token(Token.Type.ADDITIVE_OPERATOR, "-", 1, 17),
                        new Token(Token.Type.MULTIPLICATIVE_OPERATOR, "*", 1, 18),
                        new Token(Token.Type.MULTIPLICATIVE_OPERATOR, "/", 1, 19),
                        new Token(Token.Type.MULTIPLICATIVE_OPERATOR, "%", 1, 20),
                        new Token(Token.Type.AND, "&&", 1, 21),
                        new Token(Token.Type.OR, "||", 1, 23),
                        new Token(Token.Type.NEGATION, "!", 1, 25),
                        new Token(Token.Type.EQUALITY_OPERATOR, "!=", 1, 26),
                        new Token(Token.Type.EOF, null, 2, 1)
                ));
    }

    @Test
    public void shouldLexSpecialChars() {
        lexer.init("{}()[][??]\n");
        Assertions.assertThat(lexer.tokens())
                .as("Should parse special chars")
                .isEqualTo(List.of(
                        new Token(Token.Type.OPENING_CURLY, "{", 1, 1),
                        new Token(Token.Type.CLOSING_CURLY, "}", 1, 2),
                        new Token(Token.Type.OPENING_PAREN, "(", 1, 3),
                        new Token(Token.Type.CLOSING_PAREN, ")", 1, 4),
                        new Token(Token.Type.OPENING_SQUARE, "[", 1, 5),
                        new Token(Token.Type.CLOSING_SQUARE, "]", 1, 6),
                        new Token(Token.Type.OPENING_FILTER, "[?", 1, 7),
                        new Token(Token.Type.CLOSING_FILTER, "?]", 1, 9),
                        new Token(Token.Type.EOF, null, 2, 1)
                ));
    }

    @Test
    public void shouldLexABasicFunction() {
        lexer.init("""
                func num addThem(num x, num y)
                {
                  num z = x + y;
                  return z;
                }

                print addThem(40, 2);
                """);
        Assertions.assertThat(lexer.tokens())
                .as("Should parse a basic function")
                .isEqualTo(List.of(
                        new Token(Token.Type.FUNC, "func", 1, 1),
                        new Token(Token.Type.BUILT_IN_TYPE, "num", 1, 6),
                        new Token(Token.Type.ID, "addThem", 1, 10),
                        new Token(Token.Type.OPENING_PAREN, "(", 1, 17),
                        new Token(Token.Type.BUILT_IN_TYPE, "num", 1, 18),
                        new Token(Token.Type.ID, "x", 1, 22),
                        new Token(Token.Type.COMMA, ",", 1, 23),
                        new Token(Token.Type.BUILT_IN_TYPE, "num", 1, 25),
                        new Token(Token.Type.ID, "y", 1, 29),
                        new Token(Token.Type.CLOSING_PAREN, ")", 1, 30),
                        new Token(Token.Type.OPENING_CURLY, "{", 2, 1),
                        new Token(Token.Type.BUILT_IN_TYPE, "num", 3, 3),
                        new Token(Token.Type.ID, "z", 3, 7),
                        new Token(Token.Type.ASSIGNMENT, "=", 3, 9),
                        new Token(Token.Type.ID, "x", 3, 11),
                        new Token(Token.Type.ADDITIVE_OPERATOR, "+", 3, 13),
                        new Token(Token.Type.ID, "y", 3, 15),
                        new Token(Token.Type.SEMICOLON, ";", 3, 16),
                        new Token(Token.Type.RETURN, "return", 4, 3),
                        new Token(Token.Type.ID, "z", 4, 10),
                        new Token(Token.Type.SEMICOLON, ";", 4, 11),
                        new Token(Token.Type.CLOSING_CURLY, "}", 5, 1),
                        new Token(Token.Type.PRINT, "print", 7, 1),
                        new Token(Token.Type.ID, "addThem", 7, 7),
                        new Token(Token.Type.OPENING_PAREN, "(", 7, 14),
                        new Token(Token.Type.NUMERIC_LITERAL, "40", 7, 15),
                        new Token(Token.Type.COMMA, ",", 7, 17),
                        new Token(Token.Type.NUMERIC_LITERAL, "2", 7, 19),
                        new Token(Token.Type.CLOSING_PAREN, ")", 7, 20),
                        new Token(Token.Type.SEMICOLON, ";", 7, 21),
                        new Token(Token.Type.EOF, null, 8, 1)
                ));
    }

}
