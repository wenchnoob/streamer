package edu.csci340.parser;

import static edu.csci340.parser.ast.nodetypes.ASTNode.Type.*;

import edu.csci340.parser.ast.nodetypes.*;
import edu.csci340.parser.ast.nodetypes.expressions.BinaryExpression;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableStatement;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableType;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;
import edu.csci340.parser.ast.nodetypes.statements.*;
import edu.csci340.utils.ProgramBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StreamerParserTest {

    private StreamerParser streamerParser;

    @BeforeEach
    public void init() {
        streamerParser = new StreamerParser();
    }

    @Test
    public void shouldBuildCorrectly() {
    }

    @Test
    public void shouldCorrectlyParseStatementListLiterals() {
        ASTNode program = streamerParser.parse("""
                12;
                "what the";
                true;\
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of a number, string, and boolean")
                .usingRecursiveComparison()
                .isEqualTo(
                        ProgramBuilder.builder()
                                .literal(12)
                                .literal("what the")
                                .literal(true)
                                .build()
                );
    }

    @Test
    public void shouldCorrectlyParseSimpleArithmeticExpressions() {
        ASTNode program = streamerParser.parse("""
                3 +3;
                2 - 1;
                1.3 * 4;
                2/3;
                15%7;
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of arithmetic expressions")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new BinaryExpression("+", new Literal(NUMERIC_LITERAL, "3"), new Literal(NUMERIC_LITERAL, "3")),
                                        new BinaryExpression("-", new Literal(NUMERIC_LITERAL, "2"), new Literal(NUMERIC_LITERAL, "1")),
                                        new BinaryExpression("*", new Literal(NUMERIC_LITERAL, "1.3"), new Literal(NUMERIC_LITERAL, "4")),
                                        new BinaryExpression("/", new Literal(NUMERIC_LITERAL, "2"), new Literal(NUMERIC_LITERAL, "3")),
                                        new BinaryExpression("%", new Literal(NUMERIC_LITERAL, "15"), new Literal(NUMERIC_LITERAL, "7"))
                                )
                        )
                );
    }

    @Test
    public void shouldCorrectlyParseChainedAdditiveExpressions() {
        ASTNode program = streamerParser.parse("""
                2+3-1;
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of arithmetic expressions")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new edu.csci340.parser.ast.nodetypes.List(
                                        new BinaryExpression("-",
                                                new BinaryExpression("+", new Literal(NUMERIC_LITERAL, "2"), new Literal(NUMERIC_LITERAL, "3")),
                                                new Literal(NUMERIC_LITERAL, "1")
                                        )
                                )
                        )
                );

        program = streamerParser.parse("""
                2-3-1;
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of arithmetic expressions")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new BinaryExpression("-",
                                                new BinaryExpression("-", new Literal(NUMERIC_LITERAL, "2"), new Literal(NUMERIC_LITERAL, "3")),
                                                new Literal(NUMERIC_LITERAL, "1")
                                        )
                                )
                        )
                );
    }

    @Test
    public void shouldCorrectlyParseChainedMultiplicativeExpressions() {
        ASTNode program = streamerParser.parse("""
                5*3/2;
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of arithmetic expressions")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new BinaryExpression("/",
                                        new BinaryExpression("*", new Literal(NUMERIC_LITERAL, "5"), new Literal(NUMERIC_LITERAL, "3")),
                                        new Literal(NUMERIC_LITERAL, "2")
                                )
                        ))
                );
    }

    @Test
    public void shouldCorrectlyParseComplexArithmeticExpressions() {
        ASTNode program = streamerParser.parse("""
                3-3-3*5-3+3;
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of a complex arithmetic operation")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new BinaryExpression("+",
                                                new BinaryExpression("-",
                                                        new BinaryExpression("-",
                                                                new BinaryExpression("-", new Literal(NUMERIC_LITERAL, "3"), new Literal(NUMERIC_LITERAL, "3")),
                                                                new BinaryExpression("*", new Literal(NUMERIC_LITERAL, "3"), new Literal(NUMERIC_LITERAL, "5"))
                                                        ),
                                                        new Literal(NUMERIC_LITERAL, "3")
                                                ),
                                                new Literal(NUMERIC_LITERAL, "3")
                                        )
                                )
                        )
                );
    }

    @Test
    public void shouldCorrectlyParseParenthesizedExpressions() {
        ASTNode program = streamerParser.parse("""
                (2+1)*5;
                """);
        Assertions.assertThat(program)
                .as("Should parse a program consisting of arithmetic expressions")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new BinaryExpression("*",
                                                new BinaryExpression("+", new Literal(NUMERIC_LITERAL, "2"), new Literal(NUMERIC_LITERAL, "1")),
                                                new Literal(NUMERIC_LITERAL, "5")
                                        )
                                )
                        )
                );
    }

    @Test
    public void shouldParseVariableDeclarationAndAssignment() {
        ASTNode program = streamerParser.parse("""
                num x = 3.5;
                text words;
                """);

        Assertions.assertThat(program)
                .as("Should parse assignment and declaration")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new VariableStatement(new VariableType(NUMERIC_TYPE), "x", new Literal(NUMERIC_LITERAL, "3.5")),
                                        new VariableStatement(new VariableType(STRING_TYPE), "words", null)
                                )
                        )
                );
    }

    @Test
    public void shouldParseIfStatement() {
        ASTNode program = streamerParser.parse("""
                if (x < 5) print "less than 5";
                """);

        Assertions.assertThat(program)
                .as("Should parse assignment and declaration")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new ConditionalStatement(
                                                new BinaryExpression("<", new Identifier("x"), new Literal(NUMERIC_LITERAL, "5")),
                                                new PrintStatement(new Literal(STRING_LITERAL, "less than 5"))
                                        )
                                )
                        )
                );
    }

    @Test
    public void shouldParseForStatement() {
        ASTNode program = streamerParser.parse("""
                for (num n: nums) print n;
                for (num n: nums) {
                    print n;
                    print n + 1;
                }
                """);

        Assertions.assertThat(program)
                .as("Should parse assignment and declaration")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new ForLoop(
                                                new VariableStatement(new VariableType(NUMERIC_TYPE), "n", null),
                                                new Identifier("nums"),
                                                new PrintStatement(new Identifier("n"))
                                        ),
                                        new ForLoop(
                                                new VariableStatement(new VariableType(NUMERIC_TYPE), "n", null),
                                                new Identifier("nums"),
                                                new BlockStatement(
                                                        new List(new PrintStatement(new Identifier("n")),
                                                                new PrintStatement(
                                                                        new BinaryExpression("+", new Identifier("n"), new Literal(NUMERIC_LITERAL, "1"))
                                                                ))
                                                )
                                        )
                                )
                        )
                );
    }

    @Test
    public void shouldParseIdentifierCorrectly() {
        ASTNode program = streamerParser.parse("""
                x;
                """);

        Assertions.assertThat(program)
                .as("Should parse identifier")
                .usingRecursiveComparison()
                .isEqualTo(
                        ProgramBuilder.builder().id("x").build()
                );
    }

    @Test
    public void shouldParseArithmeticExpressionCorrectly() {
        ASTNode program = streamerParser.parse("""
                5 + 5 + 7;
                5 * 6 + n;
                """);

        Assertions.assertThat(program)
                .as("Should parse identifier")
                .usingRecursiveComparison()
                .isEqualTo(
                        ProgramBuilder.builder()
                                .expr()
                                .left().leftLiteral(5).op("+").rightLiteral(5).op("+").rightLiteral(7)
                                .end()
                                .expr()
                                .left().leftLiteral(5).op("*").rightLiteral(6).op("+").rightId("n")
                                .end()
                                .build()
                );
    }

}
