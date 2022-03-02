package edu.csci340.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamerLexer {

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z_]\\w*");

    private final Spec[] specs = {
            // whitespace -- but not new line
            new Spec(Pattern.compile("^[\\s&&[^\\n]]+"), null, false),
            // new line
            new Spec(Pattern.compile("^\\n"), Token.Type.NEWLINE, false),
            // keywords
            new Spec(Pattern.compile("^print"), Token.Type.PRINT, true),
            new Spec(Pattern.compile("^input"), Token.Type.INPUT, true),
            new Spec(Pattern.compile("^exit"), Token.Type.EXIT, true),
            new Spec(Pattern.compile("^until"), Token.Type.UNTIL, true),
            new Spec(Pattern.compile("^error"), Token.Type.ERROR,true),
            new Spec(Pattern.compile("^func"), Token.Type.FUNC, true),
            new Spec(Pattern.compile("^if"), Token.Type.IF, true),
            new Spec(Pattern.compile("^else"), Token.Type.ELSE, true),
            new Spec(Pattern.compile("^for"), Token.Type.FOR, true),
            new Spec(Pattern.compile("^while"), Token.Type.WHILE, true),
            new Spec(Pattern.compile("^break"), Token.Type.BREAK, true),
            new Spec(Pattern.compile("^return"), Token.Type.RETURN, true),
            new Spec(Pattern.compile("^switch"), Token.Type.SWITCH, true),
            new Spec(Pattern.compile("^case"), Token.Type.CASE, true),
            new Spec(Pattern.compile("^default"), Token.Type.DEFAULT, true),
            // built in types
            new Spec(Pattern.compile("^(num|text|bool|list|void)"), Token.Type.BUILT_IN_TYPE, true),
            // boolean literals
            new Spec(Pattern.compile("^(true|false)"), Token.Type.BOOLEAN_LITERAL, true),
            // operators
            // file read operator
            new Spec(Pattern.compile("^<<"), Token.Type.FILE_READ, false),
            // file append
            new Spec(Pattern.compile("^>>\\+"), Token.Type.FILE_APPEND, false),
            // file write
            new Spec(Pattern.compile("^>>"), Token.Type.FILE_WRITE, false),
            // additive operators
            new Spec(Pattern.compile("^(\\+|-)"), Token.Type.ADDITIVE_OPERATOR, false),
            // multiplicative operators
            new Spec(Pattern.compile("^(\\*|/|%)"), Token.Type.MULTIPLICATIVE_OPERATOR, false),
            // relational operators
            new Spec(Pattern.compile("^(<=|>=|<|>)"), Token.Type.RELATIONAL_OPERATOR, false),
            // equality operators
            new Spec(Pattern.compile("^(==|!=)"), Token.Type.EQUALITY_OPERATOR, false),
            // negation
            new Spec(Pattern.compile("^(!)"), Token.Type.NEGATION, false),
            // boolean and
            new Spec(Pattern.compile("^&&"), Token.Type.AND, false),
            // boolean or
            new Spec(Pattern.compile("^\\|\\|"), Token.Type.OR, false),
            // assignment operator
            new Spec(Pattern.compile("^="), Token.Type.ASSIGNMENT, false),
            // opening curly
            new Spec(Pattern.compile("^\\{"), Token.Type.OPENING_CURLY, false),
            // closing curly
            new Spec(Pattern.compile("^\\}"), Token.Type.CLOSING_CURLY, false),
            // opening paren
            new Spec(Pattern.compile("^\\("), Token.Type.OPENING_PAREN, false),
            // closing paren
            new Spec(Pattern.compile("^\\)"), Token.Type.CLOSING_PAREN, false),
            // opening filter
            new Spec(Pattern.compile("^\\[\\?"), Token.Type.OPENING_FILTER, false),
            // closing filter
            new Spec(Pattern.compile("^\\?\\]"), Token.Type.CLOSING_FILTER, false),
            // opening square
            new Spec(Pattern.compile("^\\["), Token.Type.OPENING_SQUARE, false),
            // closing square
            new Spec(Pattern.compile("^\\]"), Token.Type.CLOSING_SQUARE, false),
            // comma
            new Spec(Pattern.compile("^,"), Token.Type.COMMA, false),
            // colon
            new Spec(Pattern.compile("^:"), Token.Type.COLON, false),
            // semicolon
            new Spec(Pattern.compile("^;"), Token.Type.SEMICOLON, false),
            // number e.g. 12 or 12.3
            new Spec(Pattern.compile("^\\d+(\\.\\d+)?"), Token.Type.NUMERIC_LITERAL, false),
            // quoted string with escape characters \n \\ \"
            new Spec(Pattern.compile("^\"([^\\\"\\\\]|\\\\.)*\""), Token.Type.STRING_LITERAL, false),
            // identifiers eg program
            new Spec(ID_PATTERN, Token.Type.ID, false),
    };

    private String program;

    private int cursor;
    private int line;
    private int column;

    public void init(String program) {
        this.program = program;
        line = 1;
        column = 1;
    }

    public boolean hasNextToken() {
        return Objects.nonNull(program) && cursor < program.length() ;
    }

    public Token nextToken() {
        if (Objects.isNull(program)) throw new IllegalStateException("There is no program to tokenize :/!");
        if (!hasNextToken()) return new Token(Token.Type.EOF, null, line, column);

        for (Spec spec: specs) {
            String value = match(spec.pat);

            // if current pattern is not matched, continue to the next one
            if (Objects.isNull(value)) continue;

            // if this match is of an ignored pattern e.g. whitespace, return the next valid token
            if (Objects.isNull(spec.type)) {
                column += value.length();
                return this.nextToken();
            }

            // if is a new line, track that
            if (spec.type == Token.Type.NEWLINE) {
                line++;
                column = 1;
                return this.nextToken();
            }

            // if is a string, remove the quotes
            if (spec.type == Token.Type.STRING_LITERAL) value = value.substring(1, value.length() -1);

            // if is a keyword, built in type, or boolean try to parse an id instead
            if (spec.conflicts()) {
                this.cursor -= value.length();
                String val = match(ID_PATTERN);
                if (Objects.nonNull(val) && val.length() != value.length()) {
                    int col = column;
                    this.column += val.length();
                    return new Token(Token.Type.ID, val, line, col);
                }
            }

            int col = column;
            this.column += value.length();
            return new Token(spec.type, value, line, col);
        }
        throw new IllegalStateException(String.format("Illegal character < %s > found at cursor: %d line: %d column: %d", program.charAt(cursor), cursor, line, column));
    }

    public List<Token> tokens() {
        if (Objects.isNull(program)) return List.of();
        List<Token> tokens = new ArrayList<>();
        Token tok;
        while((tok = nextToken()).type() != Token.Type.EOF ) tokens.add(tok);
        tokens.add(tok);
        return tokens;
    }

    private String match(Pattern pat) {
        Matcher matcher = pat.matcher(program.substring(this.cursor));
        boolean found = matcher.find();
        if (found) {
            String match = matcher.group();
            this.cursor += match.length();
            return match;
        }
        return null;
    }

    private record Spec(Pattern pat, Token.Type type, boolean conflicts) {}

}