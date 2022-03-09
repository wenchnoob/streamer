package edu.csci340.parser;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.List;
import edu.csci340.parser.ast.nodetypes.Program;
import edu.csci340.parser.ast.nodetypes.expressions.BinaryExpression;
import edu.csci340.parser.ast.nodetypes.expressions.InputExpression;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.FileRead;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableStatement;
import edu.csci340.parser.ast.nodetypes.expressions.assignment.VariableType;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Identifier;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;
import edu.csci340.parser.ast.nodetypes.functions.FunctionCall;
import edu.csci340.parser.ast.nodetypes.functions.FunctionDefinition;
import edu.csci340.parser.ast.nodetypes.statements.*;
import edu.csci340.utils.ProgramBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class HW3Test {

    private StreamerParser streamerParser;

    @BeforeEach
    public void init() {
        streamerParser = new StreamerParser();
    }

    @Test
    public void test1() {
        ASTNode program = streamerParser.parse("""
                num x = 3.5;
                num y = x + 1;
                print y;
                """);
        Assertions.assertThat(program)
                .as("Should parse assignment, literal, expression, and print statement correctly.")
                .usingRecursiveComparison()
                .isEqualTo(
                        ProgramBuilder.builder()

                                .variableStatement()
                                .type().of(ASTNode.Type.NUMERIC_TYPE).end()
                                .id("x")
                                .expr().literal(3.5)

                                .variableStatement()
                                .type().of(ASTNode.Type.NUMERIC_TYPE).end()
                                .id("y")
                                .expr().op("+").leftId("x").rightLiteral(1).end()

                                .printStatement()
                                .expr().identifier("y")
                                .build()
                );
    }

    @Test
    public void test2() {
        ASTNode program = streamerParser.parse("""
                text myStr = "This is line one with \\"quotes inside\\". \\n This is line two.";
                print myStr;
                """);
        Assertions.assertThat(program)
                .as("Should parse variable assignment, and print statement")
                .usingRecursiveComparison()
                .isEqualTo(
                        ProgramBuilder.builder()

                                .variableStatement()
                                .type().of(ASTNode.Type.STRING_TYPE).end()
                                .id("myStr")
                                .expr().literal("This is line one with \\\"quotes inside\\\". \\n This is line two.")

                                .printStatement().expr().identifier("myStr")
                                .build()
                );
    }

    @Test
    public void test3() {
        ASTNode program = streamerParser.parse("""
                bool val1 = true;
                bool val2 = false;
                bool val3 = false;
                                
                bool result = val1 || val2 && !val3;
                """);

        Assertions.assertThat(program)
                .as("Should parse variable assignment, and print statement")
                .usingRecursiveComparison()
                .isEqualTo(
                        ProgramBuilder.builder()

                                .variableStatement()
                                .type().of(ASTNode.Type.BOOLEAN_TYPE).end()
                                .id("val1")
                                .expr().literal(true)

                                .variableStatement()
                                .type().of(ASTNode.Type.BOOLEAN_TYPE).end()
                                .id("val2")
                                .expr().literal(false)

                                .variableStatement()
                                .type().of(ASTNode.Type.BOOLEAN_TYPE).end()
                                .id("val3")
                                .expr().literal(false)

                                .variableStatement()
                                .type().of(ASTNode.Type.BOOLEAN_TYPE).end()
                                .id("result")
                                .expr()
                                .leftId("val1")
                                .op("||")
                                .right().leftId("val2").op("&&").right().unary("!").identifier("val3")

                                .build()
                );
    }

    @Test
    public void test4() {
        ASTNode program = streamerParser.parse("""
                list<text> arrOfStrings = {"Hello", "there", "friends"};
                """);

        Assertions.assertThat(program)
                .as("Should parse list variable")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new VariableStatement(new VariableType(ASTNode.Type.LIST_TYPE, new VariableType(ASTNode.Type.STRING_TYPE, null)), "arrOfStrings",
                                        new List(ASTNode.Type.EXPRESSION_LIST, new Literal("Hello"), new Literal("there"), new Literal("friends")))
                        ))
                );
    }

    @Test
    public void test5() {
        ASTNode program = streamerParser.parse("""
                text contents << "/path/to/file";
                error
                {
                  print "No such file";
                  exit 1;
                }
                print contents;
                """);
        Assertions.assertThat(program)
                .as("Should parse error statement")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new ErrorStatement(new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE, null), "contents", new FileRead(new Literal("/path/to/file"))),
                                        new List(new PrintStatement(new Literal("No such file")), new ExitStatement(new Literal(ASTNode.Type.NUMERIC_LITERAL, "1")))),
                                new PrintStatement(new Identifier("contents"))
                        ))
                );
    }

    @Test
    public void test6() {
        ASTNode program = streamerParser.parse("""
                func num addThem(num x, num y)
                {
                  num z = x + y;
                  return z;
                }
                                
                print addThem(40, 2);
                """);

        Assertions.assertThat(program)
                .as("Should parse function definition and call")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new FunctionDefinition(new VariableType(ASTNode.Type.NUMERIC_TYPE), "addThem",
                                        new List(ASTNode.Type.FORMAL_PARAMETER_LIST, new VariableStatement(new VariableType(ASTNode.Type.NUMERIC_TYPE), "x", null),
                                                new VariableStatement(new VariableType(ASTNode.Type.NUMERIC_TYPE), "y", null)),
                                        new List(new VariableStatement(new VariableType(ASTNode.Type.NUMERIC_TYPE), "z", new BinaryExpression("+", new Identifier("x"), new Identifier("y"))),
                                                new ReturnStatement(new Identifier("z")))),
                                new PrintStatement(new FunctionCall("addThem", new List(ASTNode.Type.ACTUAL_PARAMETER_LIST, new Literal(ASTNode.Type.NUMERIC_LITERAL, "40"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "2"))))
                        ))
                );
    }

    @Test
    public void test7() {
        ASTNode program = streamerParser.parse("""
                func void printFile(text path)
                {
                  text contents << path;
                  print contents;
                }
                                
                printFile(input "Enter file path: ");
                """);

        Assertions.assertThat(program)
                .as("Should parse function definition and call")
                .usingRecursiveComparison()
                .isEqualTo(new Program(
                        new List(
                                new FunctionDefinition(new VariableType(ASTNode.Type.VOID), "printFile",
                                        new List(ASTNode.Type.FORMAL_PARAMETER_LIST, new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "path", null)),
                                        new List(new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "contents", new FileRead(new Identifier("path"))),
                                                new PrintStatement(new Identifier("contents")))),
                                new FunctionCall("printFile", new List(ASTNode.Type.ACTUAL_PARAMETER_LIST, new InputExpression(new Literal("Enter file path: "))))
                        )
                ));
    }

    @Test
    public void test8() {
        ASTNode program = streamerParser.parse("""
                until text filecontents << input "Enter file: ";
                {
                  print "Invalid file";
                }
                                
                print filecontents;
                """);

        Assertions.assertThat(program)
                .as("Should parse until statement")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(
                                new List(
                                        new UntilLoop(new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "filecontents", new FileRead(new InputExpression(new Literal("Enter file: ")))), new List(new PrintStatement(new Literal("Invalid file")))),
                                        new PrintStatement(new Identifier("filecontents"))
                                )
                        )
                );
    }

    @Test
    public void test9() {
        ASTNode program = streamerParser.parse("""
                text data << args[0];
                list<text> lines = data / "\\n";
                print length(lines);
                """);

        Assertions.assertThat(program)
                .as("Should parse file read, string spilt, and print")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "data", new FileRead(new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")))),
                                new VariableStatement(new VariableType(ASTNode.Type.LIST_TYPE, new VariableType(ASTNode.Type.STRING_TYPE)), "lines", new BinaryExpression("/", new Identifier("data"), new Literal("\\n"))),
                                new PrintStatement(new FunctionCall("length", new List(ASTNode.Type.ACTUAL_PARAMETER_LIST, new Identifier("lines"))))
                        ))
                );
    }

    @Test
    public void test10() {
        ASTNode program = streamerParser.parse("""
                text data << args[0];
                list<text> lines = data / "\\n";
                list<text> matches = lines[? args[1] ?];
                print matches;
                """);
        Assertions.assertThat(program)
                .as("Should parse file read, string spilt, and print")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "data", new FileRead(new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")))),
                                new VariableStatement(new VariableType(ASTNode.Type.LIST_TYPE, new VariableType(ASTNode.Type.STRING_TYPE)), "lines", new BinaryExpression("/", new Identifier("data"), new Literal("\\n"))),
                                new VariableStatement(new VariableType(ASTNode.Type.LIST_TYPE, new VariableType(ASTNode.Type.STRING_TYPE)), "matches", new BinaryExpression("[??]", new Identifier("lines"), new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "1")))),
                                new PrintStatement(new Identifier("matches"))
                        ))
                );
    }

    @Test
    public void test11() {
        ASTNode program = streamerParser.parse("""
                text contents << args[0];
                replaceAll(contents, "teh", "the");
                contents >> args[0];
                """);

        Assertions.assertThat(program)
                .as("Should parse file read, string spilt, and print")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "contents", new FileRead(new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")))),
                                new FunctionCall("replaceAll", new List(ASTNode.Type.ACTUAL_PARAMETER_LIST, new Identifier("contents"), new Literal("teh"), new Literal("the"))),
                                new FileWrite(new Identifier("contents"), new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")))
                        ))
                );
    }

    @Test
    public void test12() {
        ASTNode program = streamerParser.parse("""
                text file1data << args[0];
                text file2data << args[1];
                if (file1data == file2data)
                {
                  print "A match!";
                }
                else
                {
                  print "Different!";
                }
                """);

        Assertions.assertThat(program)
                .as("Should parse file read, if-else-statement, and print")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "file1data", new FileRead(new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")))),
                                new VariableStatement(new VariableType(ASTNode.Type.STRING_TYPE), "file2data", new FileRead(new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "1")))),
                                new ConditionalStatement(new BinaryExpression("==", new Identifier("file1data"), new Identifier("file2data")),
                                        new List(new PrintStatement(new Literal("A match!"))),
                                        new List(new PrintStatement(new Literal("Different!"))))
                        ))
                );
    }

    @Test
    public void test13() {
        ASTNode program = streamerParser.parse("""
                func num sum(list<num> nums)
                {
                  num result = 0;
                  for (num n : nums)
                  {
                    result = result + n;
                  }
                  return result;
                }           
                """);

        Assertions.assertThat(program)
                .as("Should parse file read, if-else-statement, and print")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new FunctionDefinition(new VariableType(ASTNode.Type.NUMERIC_TYPE),
                                        "sum", new List(ASTNode.Type.FORMAL_PARAMETER_LIST, new VariableStatement(new VariableType(ASTNode.Type.LIST_TYPE, new VariableType(ASTNode.Type.NUMERIC_TYPE)), "nums", null)),
                                        new List(
                                                new VariableStatement(new VariableType(ASTNode.Type.NUMERIC_TYPE), "result", new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")),
                                                new ForLoop(new VariableStatement(new VariableType(ASTNode.Type.NUMERIC_TYPE), "n", null), new Identifier("nums"),
                                                        new List(new VariableStatement(new VariableType(ASTNode.Type.PREDEFINED), "result", new BinaryExpression("+", new Identifier("result"), new Identifier("n"))))),
                                                new ReturnStatement(new Identifier("result"))
                                        ))
                        ))
                );
    }

    @Test
    public void test14() {
        ASTNode program = streamerParser.parse("""
                switch (args[0])
                {
                  case "Monday":
                    print "A bit tired; the week is just beginning.";
                  case "Thursday":
                    print "Almost done with the week!";
                  case "Friday":
                    print "The weekend is here!";
                  case "Saturday":
                    print "Still enjoying the weekend!";
                  case "Sunday":
                    print "We start all over tomorrow...";
                  default:
                    print "Just an ordinary day.";
                }
                """);
        Assertions.assertThat(program)
                .as("Should parse file read, if-else-statement, and print")
                .usingRecursiveComparison()
                .isEqualTo(
                        new Program(new List(
                                new SwitchStatement(
                                        new BinaryExpression("[]", new Identifier("args"), new Literal(ASTNode.Type.NUMERIC_LITERAL, "0")),
                                        new List(
                                                new CaseStatement(new Literal("Monday"), new List(new PrintStatement(new Literal("A bit tired; the week is just beginning.")))),
                                                new CaseStatement(new Literal("Thursday"), new List(new PrintStatement(new Literal("Almost done with the week!")))),
                                                new CaseStatement(new Literal("Friday"), new List(new PrintStatement(new Literal("The weekend is here!")))),
                                                new CaseStatement(new Literal("Saturday"), new List(new PrintStatement(new Literal("Still enjoying the weekend!")))),
                                                new CaseStatement(new Literal("Sunday"), new List(new PrintStatement(new Literal("We start all over tomorrow...")))),
                                                new CaseStatement(null, new List(new PrintStatement(new Literal("Just an ordinary day."))))
                                        )
                                )
                        ))
                );
    }
}
