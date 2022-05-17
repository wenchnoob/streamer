
# This is an Lexer, Parser, and Interpreter for Lexer -- A domain specific string processing language.

## How to run
java -jar parser.jar <code><var>file</var></code>

### Recommended run
java -jar parser.jar <code><var>file</var></code> -o  <code><var>file to pipe output to</var></code> -m <code><var>desired mode</var></code>

### Options
<pre>
-m, --mode string        this is to specify what to do with the file. valid options are lex,parse, interpret. default is interpret.

-o, --out string         this is to specify that you want output piped (lexing, parsing) to or copied (interpretation) to a file.

-v, --verbose            to indicate that the lexer or parse should share more information about each particular action performed.

-s, --search-path        to indicate the additional locations that should be searched for dependencies

-dp, --disable-prompts,  to indicate that you do not want to be prompted for confirmation.
--permissive
</pre>
