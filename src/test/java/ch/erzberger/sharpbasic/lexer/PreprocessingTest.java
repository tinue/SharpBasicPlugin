package ch.erzberger.sharpbasic.lexer;

import org.junit.jupiter.api.Test;

public class PreprocessingTest {
    @Test
    public void testPreprocessing() {
        String input = "PRINT I: R  E M This is a test";
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start(input, 0, input.length(), 0);

        System.out.println("=== Testing: " + input + " ===");
        int i = 0;
        while (lexer.getTokenType() != null) {
            int start = lexer.getTokenStart();
            int end = lexer.getTokenEnd();
            String text = input.substring(start, Math.min(end, input.length()));
            System.out.println("Token " + (i++) + ": " + lexer.getTokenType() + " = '" + text + "' [" + start + "-" + end + "]");
            lexer.advance();
        }
    }
}
