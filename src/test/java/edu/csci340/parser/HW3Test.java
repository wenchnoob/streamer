package edu.csci340.parser;

import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.Program;
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

        System.out.println(ProgramBuilder.builder()

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
                .build());
    }

    @Test
    public void test2() {
        ASTNode program = streamerParser.parse("""
                text myStr = "This is line one with \\"quotes inside\\". \\n This is line two.";
                print myStr;
                """);
    }

    @Test
    public void test3() {
        ASTNode program = streamerParser.parse("""
                bool val1 = true;
                bool val2 = false;
                bool val3 = false;
                                
                bool result = val1 || val2 && !val3;
                """);
    }

    @Test
    public void test4() {
        ASTNode program = streamerParser.parse("""
                list<text> arrOfStrings = {"Hello", "there", "friends"};
                """);
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
    }

    @Test
    public void test9() {
        ASTNode program = streamerParser.parse("""
                text data << args[0];
                list<text> lines = data / "\\n";
                print length(lines);
                """);
    }

    @Test
    public void test10() {
        ASTNode program = streamerParser.parse("""
                text data << args[0];
                list<text> lines = data / "\\n";
                list<text> matches = lines[? args[1] ?];
                print matches;
                """);
    }

    @Test
    public void test11() {
        ASTNode program = streamerParser.parse("""
                text contents << args[0];
                replaceAll(contents, "teh", "the");
                contents >> args[0];
                """);
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
    }
}
