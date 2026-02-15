package ch.erzberger.sharpbasic.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SharpBasicCodeReformatter Tests")
class SharpBasicCodeReformatterTest {

    @Test
    @DisplayName("Format line with line number and keyword")
    void testSimpleLine() {
        String input = "10 PRINT\"Hello\"";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Line number with trailing space, keyword followed by space
        String expected = "10 PRINT \"Hello\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with comparison operator")
    void testComparisonOperator() {
        String input = "10 IF A<>5 THEN 20";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Only space after keywords (IF, THEN)
        String expected = "10 IF A<>5THEN 20";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with assignment")
    void testAssignment() {
        String input = "100 LET A=10";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space after LET keyword
        String expected = "100 LET A=10";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with FOR statement")
    void testForStatement() {
        String input = "70 FOR I=1TO 100";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space after FOR and TO keywords
        String expected = "70 FOR I=1TO 100";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with REM comment")
    void testRemComment() {
        String input = "100 A=10REM This is a test";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space after REM keyword
        String expected = "100 A=10REM This is a test";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with colon separator after expression")
    void testColonSeparator() {
        String input = "180 PRINT I: NEXT I";
        String result = SharpBasicCodeReformatter.reformat(input);
        // No space before colon after expression, no space after colon
        String expected = "180 PRINT I:NEXT I";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with colon separator after keyword")
    void testColonSeparatorAfterKeyword() {
        String input = "8 CLS:WAIT 0:CLEAR:DIM A$(0)";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space before colon after keyword (CLS :, CLEAR :)
        String expected = "8 CLS :WAIT 0:CLEAR :DIM A$(0)";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with function call")
    void testFunctionCall() {
        String input = "170 S=S+INT(A/4)";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space before opening paren in function call
        String expected = "170 S=S+INT (A/4)";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format adjacent keywords")
    void testAdjacentKeywords() {
        String input = "10 IFINKEY$<>\"\"GOTO260";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Keywords separated, space around <>, space before GOTO argument
        String expected = "10 IF INKEY$ <>\"\"GOTO 260";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Preserve LF line endings (Unix/Mac)")
    void testPreserveLFLineEndings() {
        String input = "10 PRINT\"A\"\n20 PRINT\"B\"";
        String result = SharpBasicCodeReformatter.reformat(input);
        String expected = "10 PRINT \"A\"\n20 PRINT \"B\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Preserve CRLF line endings (Windows)")
    void testPreserveCRLFLineEndings() {
        String input = "10 PRINT\"A\"\r\n20 PRINT\"B\"";
        String result = SharpBasicCodeReformatter.reformat(input);
        String expected = "10 PRINT \"A\"\r\n20 PRINT \"B\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Preserve CR line endings (old Mac)")
    void testPreserveCRLineEndings() {
        String input = "10 PRINT\"A\"\r20 PRINT\"B\"";
        String result = SharpBasicCodeReformatter.reformat(input);
        String expected = "10 PRINT \"A\"\r20 PRINT \"B\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Only space after keywords")
    void testOnlySpaceAfterKeywords() {
        String input = "60 IF Z$=\"\"THEN 85";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Only space after IF and THEN keywords
        String expected = "60 IF Z$=\"\"THEN 85";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Expand keyword abbreviations")
    void testExpandAbbreviations() {
        String input = "10 P.\"Hi there\"";
        String result = SharpBasicCodeReformatter.reformat(input);
        // P. expands to PRINT, space after keyword
        String expected = "10 PRINT \"Hi there\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format interleaved INPUT statement")
    void testInterleavedInput() {
        String input = "20 INPUT \"A:\";A,\"B:\";B";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Prompts and variables correctly spaced
        String expected = "20 INPUT \"A:\";A,\"B:\";B";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format PRINT with trailing semicolon")
    void testPrintTrailingSemicolon() {
        String input = "10 PRINT A$;";
        String result = SharpBasicCodeReformatter.reformat(input);
        String expected = "10 PRINT A$;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format DIM with length suffix")
    void testDimWithLength() {
        String input = "5 DIM A$(0)*20";
        String result = SharpBasicCodeReformatter.reformat(input);
        String expected = "5 DIM A$(0)*20";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format coordinate pairs in GLCURSOR")
    void testCoordinatePairs() {
        String input = "130 GLCURSOR(108,-1.1*R)";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space before opening paren
        String expected = "130 GLCURSOR (108,-1.1*R)";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format assignment to keyword")
    void testKeywordAssignment() {
        String input = "40 TIME=0";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space after TIME keyword
        String expected = "40 TIME =0";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format function call without parentheses")
    void testFunctionWithoutParens() {
        String input = "70 H=H-SGN(ASC Z$-10.5)";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space before opening paren for SGN
        String expected = "70 H=H-SGN (ASC Z$-10.5)";
        assertEquals(expected, result);
    }
}
