package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.antlr.SharpBasicLexer;
import ch.erzberger.sharpbasic.antlr.SharpBasicParser;
import ch.erzberger.sharpbasic.antlr.SpaceNormalizer;
import ch.erzberger.sharpbasic.antlr.visitor.NiceTextVisitor;
import ch.erzberger.sharpbasic.core.keyword.KeywordRegistry;
import ch.erzberger.sharpbasic.core.preprocess.AbbreviationExpander;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Reformats Sharp BASIC code into human-readable "nice" form.
 * Delegates to the ANTLR-based NiceTextVisitor from sharp-basic-antlr.
 */
public class SharpBasicNiceReformatter {

    private static final KeywordRegistry REGISTRY = KeywordRegistry.forPc1500();

    public static String reformat(String code) {
        String lineEnding = detectLineEnding(code);
        String expanded = expandSource(code);
        String normalized = SpaceNormalizer.forSource(expanded).normalize();
        SharpBasicLexer lexer = new SharpBasicLexer(CharStreams.fromString(normalized));
        SharpBasicParser parser = new SharpBasicParser(new CommonTokenStream(lexer));
        SharpBasicParser.ProgramContext tree = parser.program();
        String result = new NiceTextVisitor().visitProgram(tree);
        return restoreLineEndings(result, lineEnding);
    }

    private static String expandSource(String source) {
        String[] lines = source.replace("\r\n", "\n").replace("\r", "\n").split("\n", -1);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) out.append('\n');
            out.append(AbbreviationExpander.expand(lines[i], REGISTRY));
        }
        return out.toString();
    }

    private static String detectLineEnding(String code) {
        if (code.contains("\r\n")) return "\r\n";
        if (code.contains("\r")) return "\r";
        return "\n";
    }

    private static String restoreLineEndings(String result, String lineEnding) {
        if (!"\n".equals(lineEnding)) {
            result = result.replace("\n", lineEnding);
        }
        if (result.endsWith(lineEnding)) {
            result = result.substring(0, result.length() - lineEnding.length());
        }
        return result;
    }
}
