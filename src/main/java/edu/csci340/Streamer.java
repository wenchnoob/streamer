package edu.csci340;


import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.lexer.StreamerLexer;
import edu.csci340.parser.StreamerParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Streamer {



    public static void main(String... args) {
        if (args.length >= 1) {
            try {
                String in = Files.readString(Path.of(args[0]));
                StreamerInterpreter.eval(in);
            } catch (IOException e) {
                System.out.println("That file was not found!");
            }
        } else {
            StreamerLexer streamerLexer = new StreamerLexer();
            StreamerInterpreter streamerInterpreter = new StreamerInterpreter();
            StreamerParser streamerParser = new StreamerParser();

            boolean lex = false;
            boolean parse = true;
            boolean eval = false;

            streamerLexer.init("""
                    list<num> nums
                    """);
            if (lex) System.out.println(streamerLexer.tokens());

            boolean debug = true;
            if (parse) System.out.println(streamerParser.parse("""
                    switch ( x ) {
                       case 0: print "it is zero"; print "i will print more stuff";
                       case 1: print "it is one"; break;
                       default: print "it was not zero or one";
                    }
                    """));

       /*
       * func plus(num x, num y) {
                    return x + y;
                }
       *
       * */
            /**
             *  + - / *  % ()
             * */
            if (eval) System.out.println(streamerInterpreter.eval("""
                    64 * 64 * 64 * 64 * 64 * 64 * 64 * 64 * 64;
                    6 * 2 - 12 + ( 2 -3 ) * 2;
                    """));
        }
    }
}
