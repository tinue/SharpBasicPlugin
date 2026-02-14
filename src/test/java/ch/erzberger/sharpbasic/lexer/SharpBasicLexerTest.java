package ch.erzberger.sharpbasic.lexer;

import com.intellij.psi.tree.IElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.erzberger.sharpbasic.psi.SharpBasicTokenTypes.*;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Sharp BASIC lexer.
 * Tests are based on real Sharp PC-1500 BASIC code patterns from test.bas.
 */
@DisplayName("Sharp BASIC Lexer Tests")
class SharpBasicLexerTest {

    private PreprocessingSharpBasicLexer lexer;

    @BeforeEach
    void setUp() {
        lexer = new PreprocessingSharpBasicLexer();
    }

    /**
     * Helper method to tokenize input and return list of token types
     */
    private List<IElementType> tokenize(String input) throws IOException {
        lexer.start(input, 0, input.length(), 0);
        List<IElementType> tokens = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            tokens.add(lexer.getTokenType());
            lexer.advance();
        }
        return tokens;
    }

    /**
     * Helper method to tokenize and return token text pairs
     */
    private List<TokenInfo> tokenizeWithText(String input) throws IOException {
        lexer.start(input, 0, input.length(), 0);
        List<TokenInfo> tokens = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            IElementType tokenType = lexer.getTokenType();
            int start = lexer.getTokenStart();
            int end = lexer.getTokenEnd();
            String text = input.substring(start, end);
            // Trim whitespace since preprocessing may cause tokens to stretch over removed spaces
            text = text.trim();
            tokens.add(new TokenInfo(tokenType, text));
            lexer.advance();
        }
        return tokens;
    }

    private static class TokenInfo {
        final IElementType type;
        final String text;

        TokenInfo(IElementType type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override
        public String toString() {
            return type + ":" + text;
        }
    }

    // ========== Line Numbers ==========

    @Test
    @DisplayName("Line number at start of line")
    void testLineNumberAtStart() throws IOException {
        // Space after line number is preserved in original positions
        List<TokenInfo> tokens = tokenizeWithText("10 PRINT");
        assertEquals(LINE_NUMBER, tokens.get(0).type);
        assertEquals("10", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
        assertEquals("PRINT", tokens.get(1).text);
    }

    @Test
    @DisplayName("Multiple digit line numbers")
    void testMultiDigitLineNumbers() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("32767 END");
        assertEquals(LINE_NUMBER, tokens.get(0).type);
        assertEquals("32767", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
    }

    // ========== Keywords and Identifiers ==========

    @Test
    @DisplayName("Simple keyword recognition")
    void testSimpleKeywords() throws IOException {
        // Note: Spaces are removed by preprocessing, so use separators like colons
        List<TokenInfo> tokens = tokenizeWithText("PRINT:FOR:NEXT:GOTO");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("PRINT", tokens.get(0).text);
        assertEquals(COLON, tokens.get(1).type);
        assertEquals(KEYWORD, tokens.get(2).type);
        assertEquals("FOR", tokens.get(2).text);
    }

    @Test
    @DisplayName("Keywords with $ suffix - INKEY$")
    void testInkeyKeyword() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("INKEY$");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("INKEY$", tokens.get(0).text);
    }

    @Test
    @DisplayName("Adjacent keywords without spaces - IF and INKEY$ (line 10 from test.bas)")
    void testAdjacentKeywordsIfInkey() throws IOException {
        // From test.bas line 10: IFINKEY$
        List<TokenInfo> tokens = tokenizeWithText("IFINKEY$");
        assertEquals(KEYWORD, tokens.get(0).type, "First token should be IF");
        assertEquals("IF", tokens.get(0).text, "First token should be IF");
        assertEquals(KEYWORD, tokens.get(1).type, "Second token should be INKEY$");
        assertEquals("INKEY$", tokens.get(1).text, "Second token should be INKEY$");
    }

    @Test
    @DisplayName("Adjacent keyword and string variable - ANDC$ (line 10 from test.bas)")
    void testAdjacentKeywordAndStringVar() throws IOException {
        // From test.bas line 10: ANDC$
        List<TokenInfo> tokens = tokenizeWithText("ANDC$");
        assertEquals(KEYWORD, tokens.get(0).type, "First token should be AND");
        assertEquals("AND", tokens.get(0).text, "First token should be AND");
        assertEquals(IDENTIFIER, tokens.get(1).type, "Second token should be identifier C$");
        assertEquals("C$", tokens.get(1).text, "Second token should be C$");
    }

    @Test
    @DisplayName("Full line 10 from test.bas")
    void testFullLine10() throws IOException {
        // 10 IFINKEY$<>""ANDC$="BEGIN"GOTO260
        String input = "10 IFINKEY$<>\"\"ANDC$=\"BEGIN\"GOTO260";
        List<TokenInfo> tokens = tokenizeWithText(input);

        int i = 0;
        assertEquals(LINE_NUMBER, tokens.get(i).type);
        assertEquals("10", tokens.get(i++).text);

        // No WHITE_SPACE token - preprocessing removes spaces

        assertEquals(KEYWORD, tokens.get(i).type, "Should be IF");
        assertEquals("IF", tokens.get(i++).text);

        assertEquals(KEYWORD, tokens.get(i).type, "Should be INKEY$");
        assertEquals("INKEY$", tokens.get(i++).text);

        assertEquals(NE, tokens.get(i++).type); // <>

        assertEquals(STRING, tokens.get(i++).type); // ""

        assertEquals(KEYWORD, tokens.get(i).type, "Should be AND");
        assertEquals("AND", tokens.get(i++).text);

        assertEquals(IDENTIFIER, tokens.get(i).type, "Should be C$");
        assertEquals("C$", tokens.get(i++).text);

        assertEquals(EQ, tokens.get(i++).type); // =

        assertEquals(STRING, tokens.get(i++).type); // "BEGIN"

        assertEquals(KEYWORD, tokens.get(i).type, "Should be GOTO");
        assertEquals("GOTO", tokens.get(i++).text);

        assertEquals(NUMBER, tokens.get(i).type, "Should be 260");
        assertEquals("260", tokens.get(i++).text);
    }

    @Test
    @DisplayName("String variable identifiers")
    void testStringVariables() throws IOException {
        // Use commas to separate since spaces are removed
        List<TokenInfo> tokens = tokenizeWithText("A$,B$,C$");
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals("A$", tokens.get(0).text);
        assertEquals(COMMA, tokens.get(1).type);
        assertEquals(IDENTIFIER, tokens.get(2).type);
        assertEquals("B$", tokens.get(2).text);
    }

    @Test
    @DisplayName("Single letter identifiers")
    void testSingleLetterIdentifiers() throws IOException {
        // Test single identifiers separately to avoid space-based separation
        List<TokenInfo> tokens = tokenizeWithText("A");
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals("A", tokens.get(0).text);
    }

    @Test
    @DisplayName("Multi-character non-keywords split into single chars")
    void testMultiCharIdentifiers() throws IOException {
        // Sharp BASIC's greedy tokenization splits non-keyword sequences
        // "ZZ" becomes "Z" + "Z", "X1" becomes "X" + "1"
        List<TokenInfo> tokens = tokenizeWithText("ZZ");
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals("Z", tokens.get(0).text);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("Z", tokens.get(1).text);
    }

    // ========== Adjacent Keywords (Greedy Tokenization) ==========

    @Test
    @DisplayName("FORPRINT - FOR then PRINT (line 30 from test.bas)")
    void testForPrint() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("FORPRINT");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("FOR", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
        assertEquals("PRINT", tokens.get(1).text);
    }

    @Test
    @DisplayName("PRINTFORPRINT - PRINT FOR PRINT (line 50 from test.bas)")
    void testPrintForPrint() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("PRINTFORPRINT");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("PRINT", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
        assertEquals("FOR", tokens.get(1).text);
        assertEquals(KEYWORD, tokens.get(2).type);
        assertEquals("PRINT", tokens.get(2).text);
    }

    @Test
    @DisplayName("PRINTPRINTPRINT - three PRINT keywords (line 60 from test.bas)")
    void testTriplePrint() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("PRINTPRINTPRINT");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("PRINT", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
        assertEquals("PRINT", tokens.get(1).text);
        assertEquals(KEYWORD, tokens.get(2).type);
        assertEquals("PRINT", tokens.get(2).text);
    }

    @Test
    @DisplayName("AFORLET - A FOR LET (line 80 from test.bas)")
    void testAForLet() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("AFORLET");
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals("A", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
        assertEquals("FOR", tokens.get(1).text);
        assertEquals(KEYWORD, tokens.get(2).type);
        assertEquals("LET", tokens.get(2).text);
    }

    @Test
    @DisplayName("FORI - FOR then I (line 90 from test.bas)")
    void testForI() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("FORI");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("FOR", tokens.get(0).text);
        assertEquals(IDENTIFIER, tokens.get(1).type);
        assertEquals("I", tokens.get(1).text);
    }

    // ========== Numbers and Operators ==========

    @Test
    @DisplayName("Integer numbers")
    void testIntegers() throws IOException {
        // Numbers at line start are LINE_NUMBER, not NUMBER
        List<TokenInfo> tokens = tokenizeWithText("10,100,260");
        assertEquals(LINE_NUMBER, tokens.get(0).type);
        assertEquals("10", tokens.get(0).text);
        assertEquals(COMMA, tokens.get(1).type);
        assertEquals(NUMBER, tokens.get(2).type);
        assertEquals("100", tokens.get(2).text);
    }

    @Test
    @DisplayName("Arithmetic operators")
    void testArithmeticOperators() throws IOException {
        // Test operators with expression (A= prefix so first number isn't line number)
        List<TokenInfo> tokens = tokenizeWithText("A=1+2-3*4/5");
        assertEquals(IDENTIFIER, tokens.get(0).type); // A
        assertEquals(EQ, tokens.get(1).type);
        assertEquals(NUMBER, tokens.get(2).type); // 1
        assertEquals(PLUS, tokens.get(3).type);
        assertEquals(NUMBER, tokens.get(4).type); // 2
        assertEquals(MINUS, tokens.get(5).type);
    }

    @Test
    @DisplayName("Comparison operators")
    void testComparisonOperators() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("A<B:C>D:E<=F:G>=H:I<>J:K=L");
        // A < B : C > D ...
        assertEquals(IDENTIFIER, tokens.get(0).type); // A
        assertEquals(LT, tokens.get(1).type);
        assertEquals(IDENTIFIER, tokens.get(2).type); // B
        assertEquals(COLON, tokens.get(3).type);
        assertEquals(IDENTIFIER, tokens.get(4).type); // C
        assertEquals(GT, tokens.get(5).type);
    }

    @Test
    @DisplayName("String literals")
    void testStrings() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("\"Hello\",\"BEGIN\"");
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("\"Hello\"", tokens.get(0).text);
        assertEquals(COMMA, tokens.get(1).type);
        assertEquals(STRING, tokens.get(2).type);
        assertEquals("\"BEGIN\"", tokens.get(2).text);
    }

    @Test
    @DisplayName("Empty string")
    void testEmptyString() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("\"\"");
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("\"\"", tokens.get(0).text);
    }

    // ========== Comments ==========

    @Test
    @DisplayName("REM comment (line 100 from test.bas)")
    void testRemComment() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("10 A=10REMThis is a test");
        // REM should be a KEYWORD, followed by COMMENT tokens
        boolean foundRemKeyword = false;
        boolean foundComment = false;
        for (TokenInfo token : tokens) {
            if (token.type == KEYWORD && token.text.equals("REM")) {
                foundRemKeyword = true;
            }
            if (token.type == COMMENT) {
                foundComment = true;
            }
        }
        assertTrue(foundRemKeyword, "Should find REM as KEYWORD");
        assertTrue(foundComment, "Should find COMMENT token for comment text");
    }

    @Test
    @DisplayName("Single quote (apostrophe) comment")
    void testApostropheComment() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("10 PRINT A 'This is a comment");
        // Find the COMMENT token (apostrophe starts comment immediately)
        boolean foundComment = false;
        for (TokenInfo token : tokens) {
            if (token.type == COMMENT) {
                foundComment = true;
                break;
            }
        }
        assertTrue(foundComment, "Should find a COMMENT token for apostrophe");
    }

    @Test
    @DisplayName("Apostrophe immediately after code")
    void testApostropheNoSpace() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("PRINTA'comment");
        // Should have: PRINT, A, COMMENT
        boolean foundComment = false;
        for (TokenInfo token : tokens) {
            if (token.type == COMMENT) {
                foundComment = true;
                break;
            }
        }
        assertTrue(foundComment, "Should find a COMMENT token starting with apostrophe");
    }

    // ========== Complex Expressions from test.bas ==========

    @Test
    @DisplayName("Line 70: FORI=1TO100")
    void testLine70() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("FORI=1TO100");
        int i = 0;
        assertEquals(KEYWORD, tokens.get(i++).type); // FOR
        assertEquals(IDENTIFIER, tokens.get(i++).type); // I
        assertEquals(EQ, tokens.get(i++).type); // =
        assertEquals(NUMBER, tokens.get(i++).type); // 1
        assertEquals(KEYWORD, tokens.get(i++).type); // TO
        assertEquals(NUMBER, tokens.get(i++).type); // 100
    }

    @Test
    @DisplayName("Line 110: LPRINTAB")
    void testLprintAb() throws IOException {
        // AB is the abbreviation for ABS keyword
        List<TokenInfo> tokens = tokenizeWithText("LPRINTAB");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("LPRINT", tokens.get(0).text);
        assertEquals(KEYWORD, tokens.get(1).type);
        assertEquals("AB", tokens.get(1).text);
    }

    @Test
    @DisplayName("Line 120: LPRINT\"Hi there\"")
    void testLprintString() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("LPRINT\"Hi there\"");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("LPRINT", tokens.get(0).text);
        assertEquals(STRING, tokens.get(1).type);
        assertEquals("\"Hi there\"", tokens.get(1).text);
    }

    @Test
    @DisplayName("CHR$ function keyword")
    void testChrFunction() throws IOException {
        List<TokenInfo> tokens = tokenizeWithText("CHR$");
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("CHR$", tokens.get(0).text);
    }

    @Test
    @DisplayName("INPUT statement with string variable")
    void testInputStatement() throws IOException {
        // Line 20: INPUT"Letter:";L$
        String input = "INPUT\"Letter:\";L$";
        List<TokenInfo> tokens = tokenizeWithText(input);

        int i = 0;
        assertEquals(KEYWORD, tokens.get(i++).type); // INPUT
        assertEquals(STRING, tokens.get(i++).type); // "Letter:"
        assertEquals(SEMICOLON, tokens.get(i++).type); // ;
        assertEquals(IDENTIFIER, tokens.get(i++).type); // L$
        assertEquals("L$", tokens.get(i-1).text);
    }

    // ========== Token Continuity Test (Previously Fixed Issue) ==========

    @Test
    @DisplayName("Token positions are continuous with no gaps")
    void testTokenContinuity() throws IOException {
        String input = "10 PRINT \"Hello\": REM Test";
        lexer.start(input, 0, input.length(), 0);

        int expectedStart = 0;
        int tokenCount = 0;

        while (lexer.getTokenType() != null) {
            int actualStart = lexer.getTokenStart();
            int actualEnd = lexer.getTokenEnd();

            // Verify token starts where expected (no gap from previous token)
            assertEquals(expectedStart, actualStart,
                    "Token " + tokenCount + " should start at position " + expectedStart +
                    " but starts at " + actualStart + " (gap detected!)");

            // Verify end is after start
            assertTrue(actualEnd > actualStart,
                    "Token " + tokenCount + " end (" + actualEnd + ") should be after start (" + actualStart + ")");

            // Next token should start where this one ends
            expectedStart = actualEnd;
            tokenCount++;

            lexer.advance();
        }

        // Verify we covered the entire input
        assertEquals(input.length(), expectedStart,
                "Tokens should cover entire input length");

        assertTrue(tokenCount > 0, "Should have found at least one token");
    }

    // ========== Whitespace Issues (Lines 210-220) ==========

    @Test
    @DisplayName("Line 210: Leading space before line number")
    void testLeadingSpaceBeforeLineNumber() throws IOException {
        // Line 210: " 210FOR" - space before line number
        String input = " 210FOR";
        List<TokenInfo> tokens = tokenizeWithText(input);

        // After preprocessing, leading space is removed
        // Should recognize: LINE_NUMBER (210), KEYWORD (FOR)
        int i = 0;
        assertEquals(LINE_NUMBER, tokens.get(i).type, "Should recognize 210 as line number");
        assertEquals("210", tokens.get(i++).text);
        assertEquals(KEYWORD, tokens.get(i).type, "Should recognize FOR as keyword");
        assertEquals("FOR", tokens.get(i++).text);
    }

    @Test
    @DisplayName("Line 220: REMFOR - REM followed by FOR in comment")
    void testRemFor() throws IOException {
        // Line 220: "220REMFOR I=1 TO 100"
        String input = "220REMFOR I=1 TO 100";
        List<TokenInfo> tokens = tokenizeWithText(input);

        // Should recognize: LINE_NUMBER (220), KEYWORD (REM), COMMENT (FOR I=1 TO 100)
        int i = 0;
        assertEquals(LINE_NUMBER, tokens.get(i).type, "Should recognize 220 as line number");
        assertEquals("220", tokens.get(i++).text);
        assertEquals(KEYWORD, tokens.get(i).type, "Should recognize REM as KEYWORD");
        assertEquals("REM", tokens.get(i++).text);
        // The rest should be COMMENT
        assertTrue(i < tokens.size() && tokens.get(i).type == COMMENT,
                "Should have COMMENT token after REM");
    }

    // ========== New Test Cases (Lines 180-200) ==========

    @Test
    @DisplayName("Line 180: REM inside string should not trigger comment")
    void testRemInsideString() throws IOException {
        String input = "PRINT \"REM This is a test\":NEXTI";
        List<TokenInfo> tokens = tokenizeWithText(input);

        // Should have: PRINT, STRING (not KEYWORD for REM), COLON, NEXT, I
        boolean hasString = false;
        boolean wrongRemKeyword = false;
        for (TokenInfo token : tokens) {
            if (token.type == STRING && token.text.contains("REM")) {
                hasString = true;
            }
            // Check if REM was wrongly recognized as KEYWORD (it shouldn't be in a string)
            if (token.type == KEYWORD && token.text.equals("REM")) {
                wrongRemKeyword = true;
            }
        }
        assertTrue(hasString, "REM inside string should be part of STRING token");
        assertFalse(wrongRemKeyword, "Should not treat REM inside string as KEYWORD");
    }

    @Test
    @DisplayName("Line 190: REM with quoted text in comment")
    void testRemWithQuotes() throws IOException {
        String input = "REM This \"is\" a test";
        List<TokenInfo> tokens = tokenizeWithText(input);

        // REM should be KEYWORD, followed by COMMENT
        assertEquals(KEYWORD, tokens.get(0).type);
        assertEquals("REM", tokens.get(0).text);
        assertTrue(tokens.size() > 1 && tokens.get(1).type == COMMENT);
    }

    @Test
    @DisplayName("Line 200: Keywords with spaces (R  E M)")
    void testKeywordWithSpaces() throws IOException {
        // This is the tricky one: "R  E M" should be recognized as REM
        String input = "PRINT I: R  E M This is a test";
        List<TokenInfo> tokens = tokenizeWithText(input);

        // Expected: PRINT, I, COLON, REM (KEYWORD), COMMENT
        // Note: Token text may be "R  E M" from original, but it should be KEYWORD type
        boolean foundRemKeyword = false;
        boolean foundComment = false;
        for (TokenInfo token : tokens) {
            if (token.type == KEYWORD &&
                (token.text.equals("REM") || token.text.replaceAll("\\s+", "").equals("REM"))) {
                foundRemKeyword = true;
            }
            if (token.type == COMMENT) {
                foundComment = true;
            }
        }

        assertTrue(foundRemKeyword, "R  E M with spaces should be recognized as REM KEYWORD");
        assertTrue(foundComment, "Should have COMMENT token after REM");
    }
}
