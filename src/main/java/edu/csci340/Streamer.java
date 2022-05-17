package edu.csci340;


import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.lexer.StreamerLexer;
import edu.csci340.parser.StreamerParser;
import edu.csci340.stdlib.IO;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class Streamer {

    private static Pattern yes = Pattern.compile("ye?s?", Pattern.CASE_INSENSITIVE);
    private static Pattern no = Pattern.compile("no?", Pattern.CASE_INSENSITIVE);

    private static String mainFile = null;
    private static String mode = "interpret";
    private static String out = null;
    private static boolean verbose = false;
    private static boolean prompt = true;
    private static final List<String> searchPaths = new ArrayList<>();

    private static InputStream inputStream = System.in;
    private static OutputStream outputStream = System.out;

    public static void main(String... args) {
        if (args.length >= 1) {
            if (args[0].matches("-.*|--.*") || !Files.exists(Path.of(args[0]))) {
                fail(NO_MAIN, null);
                return;
            }
            else mainFile = args[0];

            boolean processed = processOptions(args);
            if (!processed) return;

            //System.out.println(String.format("\nStarting program with following details\n" +
             //       "Main file: %s\nMode: %s\nOut: %s\nVerbose: %s\nSearch Paths: %s", mainFile, mode, out, verbose, searchPaths));

            String in;
            try {
                in = Files.readString(Path.of(mainFile));
            } catch (IOException e) {
                System.out.println("I/O Failure while attempting to read Program file: " + mainFile + "\t Aborting...");
                return;
            }



            switch (mode) {
                case "lex":
                    configureOutputStream();
                    try {
                        outputStream.write(new StreamerLexer().init(in).tokens().stream().map(Object::toString).collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        System.out.println("Error while trying to write results to output stream.");
                    }
                    break;
                case "parse":
                    configureOutputStream();
                    try {
                        outputStream.write(new StreamerParser().parse(in).toString().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        System.out.println("Error while trying to write results to output stream.");
                    }
                    break;
                case "interpret":
                    try {
                        StreamerInterpreter.eval(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            System.out.println("""
                    AST Interpreter for Streamer, a DSL for file processing.
                                       
                    This application takes at least one argument specifying the streamer file to lex, parse, or interpret.
                                       
                    Options
                    -m, --mode string        this is to specify what to do with the file. valid options are lex,parse, interpret. default is interpret.
                    -o, --out string         this is to specify that you want output piped (lexing, parsing) to or copied (interpretation) to a file.
                    -v, --verbose            to indicate that the lexer or parse should share more information about each particular action performed.
                    -s, --search-path        to indicate the additional locations that should be searched for dependencies
                    -dp, --disable-prompts,  to indicate that you do not want to be prompted for confirmation.
                    --permissive
                    """);
        }
    }

    private static final int NO_MAIN = 1, UNREC_OPTION = 2, REQ_PARAM = 3, DANGLING = 4, OTHER = 5;

    private static boolean processOptions(String... args) {
        for (int i = 1; i < args.length; i++) {
            if (args[i].matches("-.*|--.*")) {
                switch (args[i].replaceAll("-|--", "")) {
                    case "m", "mode":
                        i++;
                        if (Objects.isNull(safeAccess(i, args))) return fail(DANGLING, safeAccess(i - 1, args));
                        switch (safeAccess(i, args)) {
                            case "lex", "parse", "interpret":
                                mode = safeAccess(i, args);
                                break;
                            default:
                                return fail(OTHER, "Illegal <" + safeAccess(i, args) + "> argument to mode.");
                        }
                        break;
                    case "o", "out":
                        i++;
                        if (Objects.isNull(safeAccess(i, args))) return fail(DANGLING, safeAccess(i - 1, args));
                        out = safeAccess(i, args);
                        break;
                    case "v", "verbose":
                        verbose = true;
                        break;
                    case "s", "searchpath":
                        i++;
                        if (Objects.isNull(safeAccess(i, args))) return fail(DANGLING, safeAccess(i - 1, args));
                        searchPaths.addAll(Set.of(safeAccess(i, args).split(",|:")));
                        break;
                    case "dp", "disableprompts", "permissive":
                        prompt = false;
                        break;
                    default:
                        return fail(UNREC_OPTION, safeAccess(i, args));
                }
            } else return fail(UNREC_OPTION, safeAccess(i, args));
        }
        return true;
    }

    private static boolean fail(int code, String reason) {
        switch (code) {
            case NO_MAIN:
                System.out.println("Missing main file to lex/parse/interpret.");
                break;
            case UNREC_OPTION:
                System.out.println("Unrecognized option <" + reason + ">");
                break;
            case REQ_PARAM:
                System.out.println("Missing required parameter after option <" + reason + ">");
                break;
            case DANGLING:
                System.out.println("Dangling option: " + reason);
                break;
            case OTHER:
                System.out.println("Other");
                break;
        }
        return false;
    }

    private static String safeAccess(int idx, String... args) {
        if (idx < args.length) return args[idx];
        return null;
    }

    public static void configureOutputStream() {
        if (Objects.nonNull(out)) {
            try {
                if (prompt && Files.exists(Path.of(out))) {
                    String response = "";
                    while (!yes.matcher(response).matches() && !no.matcher(response).matches()) response = IO.readLine("File <" + out + "> already exist do you want to override it? (yes/no): ");
                    if (no.matcher(response).matches()) System.out.println("Failed to override file, falling back to STDOUT");
                    else if (yes.matcher(response).matches()) {
                        Files.deleteIfExists(Path.of(out));
                        outputStream = Files.newOutputStream(Path.of(out), StandardOpenOption.CREATE_NEW);
                    }
                } else {
                    Files.deleteIfExists(Path.of(out));
                    outputStream = Files.newOutputStream(Path.of(out), StandardOpenOption.CREATE_NEW);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("I/O Failure while attempting to open Output file: " + out + "\t Aborting...");
                return;
            }
        }
    }
}
