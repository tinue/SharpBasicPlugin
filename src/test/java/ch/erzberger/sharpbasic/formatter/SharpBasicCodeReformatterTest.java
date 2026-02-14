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
        // Line number right-aligned with trailing space, keyword followed by space
        String expected = "   10 PRINT \"Hello\"\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with comparison operator")
    void testComparisonOperator() {
        String input = "10 IF A<>5 THEN 20";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space around comparison operators
        String expected = "   10 IF A <>5 THEN 20\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with assignment")
    void testAssignment() {
        String input = "100 A=10";
        String result = SharpBasicCodeReformatter.reformat(input);
        // No space around = in assignment
        String expected = "  100 A=10\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with FOR statement")
    void testForStatement() {
        String input = "70 FOR I=1TO 100";
        String result = SharpBasicCodeReformatter.reformat(input);
        // No space around = or TO
        String expected = "   70 FOR I=1TO 100\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with REM comment")
    void testRemComment() {
        String input = "100 A=10REMThis is a test";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space after REM
        String expected = "  100 A=10REM This is a test\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with colon separator")
    void testColonSeparator() {
        String input = "180 PRINT I: NEXT I";
        String result = SharpBasicCodeReformatter.reformat(input);
        // No space around colon
        String expected = "  180 PRINT I:NEXT I\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format line with function call")
    void testFunctionCall() {
        String input = "170 S=S+INT(A/4)";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Space before opening paren in function call
        String expected = "  170 S=S+INT (A/4)\r";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Format adjacent keywords")
    void testAdjacentKeywords() {
        String input = "10 IFINKEY$<>\"\"GOTO260";
        String result = SharpBasicCodeReformatter.reformat(input);
        // Keywords separated, space around <>, space before GOTO argument
        String expected = "   10 IF INKEY$ <>\"\"GOTO 260\r";
        assertEquals(expected, result);
    }
}
