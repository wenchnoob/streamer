package edu.csci340.interpreter.io;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.InputExpression;

import java.io.IOException;

public class Input {
    public static void eval(ASTNode node) throws IOException {
        if (node instanceof InputExpression p) {
            StreamerInterpreter.eval(p.children().get(0));
            Object prompt = p.children().get(0).value();
            System.out.print(prompt);
            StringBuilder in = new StringBuilder();
            char c;
            while ((c = (char)System.in.read()) != '\n') in.append(c);
            node.setValue(in.toString());
            node.setType(ASTNode.Type.STRING_LITERAL);
            node.setChildren(null);
        }
    }
}
