package ch.erzberger.sharpbasic.lexer;

import ch.erzberger.sharpbasic.psi.SharpBasicTypes;
import com.intellij.psi.tree.IElementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Lexer Case Sensitivity Tests")
class CaseSensitivityTest {

    @Test
    @DisplayName("Uppercase REM is recognized as keyword")
    void testUppercaseRem() throws IOException {
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start("REM test", 0, 8, 0);

        IElementType tokenType = lexer.getTokenType();
        String tokenText = "REM test".substring(lexer.getTokenStart(), lexer.getTokenEnd());
        System.out.println("Token type for 'REM': " + tokenType);
        System.out.println("Token text: '" + tokenText + "'");
        assertEquals(SharpBasicTypes.KEYWORD, tokenType, "REM should be recognized as KEYWORD");
    }

    @Test
    @DisplayName("Lowercase rem is NOT recognized as keyword")
    void testLowercaseRem() throws IOException {
        // Test with raw SharpBasicLexer
        SharpBasicLexer rawLexer = new SharpBasicLexer((java.io.Reader) null);
        rawLexer.reset("rem test", 0, 8, 0);
        IElementType rawTokenType = rawLexer.advance();
        System.out.println("RAW Lexer - Token type for 'rem': " + rawTokenType);
        System.out.println("RAW Lexer - Token text: '" + rawLexer.yytext() + "'");

        // Test with PreprocessingSharpBasicLexer
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start("rem test", 0, 8, 0);
        IElementType tokenType = lexer.getTokenType();
        System.out.println("Preprocessing Lexer - Token type for 'rem': " + tokenType);

        assertEquals(SharpBasicTypes.IDENTIFIER, tokenType, "rem should be recognized as IDENTIFIER, not KEYWORD");
    }

    @Test
    @DisplayName("Lowercase print is NOT recognized as keyword")
    void testLowercasePrint() throws IOException {
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start("print A", 0, 7, 0);

        IElementType tokenType = lexer.getTokenType();
        System.out.println("Token type for 'print': " + tokenType);
        assertEquals(SharpBasicTypes.IDENTIFIER, tokenType, "print should be recognized as IDENTIFIER, not KEYWORD");
    }

    @Test
    @DisplayName("Mixed case Print is NOT recognized as keyword")
    void testMixedCasePrint() throws IOException {
        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start("Print A", 0, 7, 0);

        IElementType tokenType = lexer.getTokenType();
        System.out.println("Token type for 'Print': " + tokenType);
        assertEquals(SharpBasicTypes.IDENTIFIER, tokenType, "Print should be recognized as IDENTIFIER, not KEYWORD");
    }
}
