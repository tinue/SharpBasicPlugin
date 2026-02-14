package ch.erzberger.sharpbasic.lexer;

import org.junit.jupiter.api.Test;

/**
 * Detailed verification of whitespace handling for lines 210 and 220
 */
public class WhitespaceVerificationTest {

    @Test
    public void verifyLine210() {
        String input = " 210FOR";
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start(input, 0, input.length(), 0);

        System.out.println("=== Line 210: Leading space before line number ===");
        System.out.println("Input: '" + input + "'");
        System.out.println("Tokens:");
        int i = 0;
        while (lexer.getTokenType() != null) {
            int start = lexer.getTokenStart();
            int end = lexer.getTokenEnd();
            String text = input.substring(start, Math.min(end, input.length()));
            System.out.println("  " + (i++) + ": " + lexer.getTokenType() +
                             " = '" + text + "' [" + start + "-" + end + "]");
            lexer.advance();
        }
        System.out.println();
    }

    @Test
    public void verifyLine220() {
        String input = "220REMFOR I=1 TO 100";
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start(input, 0, input.length(), 0);

        System.out.println("=== Line 220: REMFOR I=1 TO 100 ===");
        System.out.println("Input: '" + input + "'");
        System.out.println("Tokens:");
        int i = 0;
        while (lexer.getTokenType() != null) {
            int start = lexer.getTokenStart();
            int end = lexer.getTokenEnd();
            String text = input.substring(start, Math.min(end, input.length()));
            System.out.println("  " + (i++) + ": " + lexer.getTokenType() +
                             " = '" + text + "' [" + start + "-" + end + "]");
            lexer.advance();
        }
        System.out.println();
    }

    @Test
    public void verifySpacesInKeywords() {
        String input = "P R I N T \"Hello\"";
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start(input, 0, input.length(), 0);

        System.out.println("=== Bonus: P R I N T with spaces ===");
        System.out.println("Input: '" + input + "'");
        System.out.println("Tokens:");
        int i = 0;
        while (lexer.getTokenType() != null) {
            int start = lexer.getTokenStart();
            int end = lexer.getTokenEnd();
            String text = input.substring(start, Math.min(end, input.length()));
            System.out.println("  " + (i++) + ": " + lexer.getTokenType() +
                             " = '" + text + "' [" + start + "-" + end + "]");
            lexer.advance();
        }
    }
}
