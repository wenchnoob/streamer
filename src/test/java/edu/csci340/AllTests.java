package edu.csci340;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"edu.csci340.interpreter", "edu.csci340.lexer", "edu.csci340.parser"})
public class AllTests {
}
