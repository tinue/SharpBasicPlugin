package ch.erzberger.sharpbasic.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SharpBasicCompactReformatter Tests")
class SharpBasicCompactReformatterTest {

    @Test
    @DisplayName("Compact format with abbreviations")
    void testCompactWithAbbreviations() {
        String input = "10 PRINT \"Hello\"";
        String result = SharpBasicCompactReformatter.reformat(input);
        // No spaces, use P. abbreviation
        String expected = "10P.\"Hello\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Keep REM comments")
    void testKeepRemComments() {
        String input = "10 PRINT \"Hi\" REM This is a comment";
        String result = SharpBasicCompactReformatter.reformat(input);
        // Comment kept (user can manually shorten if too long)
        String expected = "10P.\"Hi\"REMThis is a comment";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Keep apostrophe comments")
    void testKeepApostropheComments() {
        String input = "10 PRINT \"Hi\" ' This is a comment";
        String result = SharpBasicCompactReformatter.reformat(input);
        // Comment kept
        String expected = "10P.\"Hi\"'This is a comment";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Compact IF THEN with abbreviations")
    void testCompactIfThen() {
        String input = "10 IF A<>5 THEN 20";
        String result = SharpBasicCompactReformatter.reformat(input);
        // No spaces, IF has no dot (already abbreviated), THEN -> T.
        String expected = "10IFA<>5T.20";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Compact FOR loop")
    void testCompactForLoop() {
        String input = "10 FOR I=1 TO 100";
        String result = SharpBasicCompactReformatter.reformat(input);
        // No spaces, F. is shorter than FOR, TO stays TO (TO. is longer)
        String expected = "10F.I=1TO100";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Multiple statements with colon")
    void testMultipleStatements() {
        String input = "10 PRINT I: NEXT I";
        String result = SharpBasicCompactReformatter.reformat(input);
        // No spaces, abbreviations
        String expected = "10P.I:N.I";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Keep comment-only lines")
    void testKeepCommentOnlyLines() {
        String input = "10 PRINT \"Hi\"\nREM Comment line\n20 GOTO 10";
        String result = SharpBasicCompactReformatter.reformat(input);
        // Comment line kept
        String expected = "10P.\"Hi\"\nREMComment line\n20G.10";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Preserve line endings")
    void testPreserveLineEndings() {
        String input = "10 PRINT \"A\"\n20 PRINT \"B\"";
        String result = SharpBasicCompactReformatter.reformat(input);
        String expected = "10P.\"A\"\n20P.\"B\"";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("GOTO and GOSUB abbreviations")
    void testGotoGosub() {
        String input = "10 GOTO 100\n20 GOSUB 200";
        String result = SharpBasicCompactReformatter.reformat(input);
        String expected = "10G.100\n20GOS.200";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Keywords with hash suffix")
    void testKeywordsWithHash() {
        String input = "10 POKE# 64000,255";
        String result = SharpBasicCompactReformatter.reformat(input);
        // POKE# abbreviation is PO# (keep the # suffix)
        String expected = "10PO#.64000,255";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("IF keyword has no abbreviation")
    void testIfKeywordNoAbbreviation() {
        String input = "40 IF A=1 THEN 50";
        String result = SharpBasicCompactReformatter.reformat(input);
        // IF has no abbreviation (IF is already the abbreviation)
        String expected = "40IFA=1T.50";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("LET keyword abbreviation not shorter")
    void testLetKeywordNotShorter() {
        String input = "40 LET V=2";
        String result = SharpBasicCompactReformatter.reformat(input);
        // LET -> LE. is not shorter (3 chars each), so use LET
        String expected = "40LETV=2";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Complex line with IF and LET")
    void testComplexLineIfLet() {
        String input = "40 V=1:IF LEFT$ (V$,1)=\"Y\" LET V=2";
        String result = SharpBasicCompactReformatter.reformat(input);
        // IF stays IF, LEFT$ -> LEF., LET stays LET
        String expected = "40V=1:IFLEF.(V$,1)=\"Y\"LETV=2";
        assertEquals(expected, result);
    }
}
