package edu.csci340.stdlib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class IO {
    public static String readLine() throws IOException {
        return readLine(System.in);
    }

    public static String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return readLine(System.in);
    }

    public static String readLine(InputStream override) throws IOException {
        if (Objects.isNull(override)) override = System.in;
        StringBuilder in = new StringBuilder();
        char c;
        while ((c = (char)override.read()) != '\n') in.append(c);
        return in.toString();
    }
}
