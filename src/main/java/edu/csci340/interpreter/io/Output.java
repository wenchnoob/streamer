package edu.csci340.interpreter.io;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.statements.PrintStatement;

public class Output {
    public static void eval(ASTNode node) {
        if (node instanceof PrintStatement p) {
            StreamerInterpreter.eval(p.children().get(0));
            Object toPrint = p.children().get(0).value();
            System.out.println(toPrint);
            p.setChildren(null);
        }
    }
}
