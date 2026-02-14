package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.lexer.PreprocessingSharpBasicLexer;
import ch.erzberger.sharpbasic.psi.SharpBasicTypes;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reformats Sharp BASIC code to match the PC-1500's canonical output format.
 * This matches the format used when:
 * - Viewing code in PRO mode
 * - Printing with CE-150
 * - Saving in ASCII mode with CE-158
 */
public class SharpBasicCodeReformatter {

    private static final Set<String> KEYWORDS_WITH_DOLLAR = new HashSet<>();

    static {
        // Keywords that can have $ suffix
        KEYWORDS_WITH_DOLLAR.add("INKEY$");
        KEYWORDS_WITH_DOLLAR.add("CHR$");
        KEYWORDS_WITH_DOLLAR.add("STR$");
        KEYWORDS_WITH_DOLLAR.add("HEX$");
        KEYWORDS_WITH_DOLLAR.add("MID$");
        KEYWORDS_WITH_DOLLAR.add("LEFT$");
        KEYWORDS_WITH_DOLLAR.add("RIGHT$");
    }

    /**
     * Reformats the given Sharp BASIC code.
     *
     * @param code the code to reformat
     * @return the reformatted code
     */
    public static String reformat(String code) {
        StringBuilder result = new StringBuilder();
        String[] lines = code.split("\\r\\n|\\r|\\n");

        // First pass: find maximum line number width
        int maxLineNumberWidth = 3; // PC-1500 minimum is 3 digits for formatting
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Extract line number if present
            int lineNumber = extractLineNumber(trimmed);
            if (lineNumber > 0) {
                maxLineNumberWidth = Math.max(maxLineNumberWidth, String.valueOf(lineNumber).length());
            }
        }

        // Format each line
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                result.append("\r");
                continue;
            }

            String formattedLine = reformatLine(trimmed, maxLineNumberWidth);
            result.append(formattedLine).append("\r");
        }

        return result.toString();
    }

    /**
     * Extracts the line number from the beginning of a line, or -1 if none.
     * Returns an array: [lineNumber, charsConsumed]
     */
    private static int[] extractLineNumberAndLength(String line) {
        StringBuilder num = new StringBuilder();
        int charsConsumed = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                num.append(c);
                charsConsumed = i + 1;
            } else if (c == ' ' || c == '\t') {
                // Skip spaces, but don't count as end of line number yet
                continue;
            } else {
                // Hit non-digit, non-space - line number ends
                break;
            }
        }

        if (num.length() > 0) {
            try {
                int lineNumber = Integer.parseInt(num.toString());
                return new int[]{lineNumber, charsConsumed};
            } catch (NumberFormatException e) {
                return new int[]{-1, 0};
            }
        }
        return new int[]{-1, 0};
    }

    /**
     * Extracts the line number from the beginning of a line, or -1 if none.
     */
    private static int extractLineNumber(String line) {
        return extractLineNumberAndLength(line)[0];
    }

    /**
     * Reformats a single line according to PC-1500 rules.
     */
    private static String reformatLine(String line, int maxLineNumberWidth) {
        StringBuilder result = new StringBuilder();

        // Extract and format line number
        int[] lineNumData = extractLineNumberAndLength(line);
        int lineNumber = lineNumData[0];
        int charsConsumed = lineNumData[1];
        String restOfLine = line;

        if (lineNumber > 0) {
            // Output line number with single trailing space (no leading spaces)
            result.append(lineNumber);
            result.append(' ');

            // Remove the consumed characters (line number with spaces)
            restOfLine = line.substring(charsConsumed).trim();
        }

        // Tokenize and reformat the rest of the line
        String formattedCode = reformatCode(restOfLine);
        result.append(formattedCode);

        return result.toString();
    }

    /**
     * Reformats the code portion of a line by applying spacing rules between tokens.
     */
    private static String reformatCode(String code) {
        if (code.isEmpty()) {
            return "";
        }

        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start(code, 0, code.length(), 0);

        List<TokenInfo> tokens = new ArrayList<>();
        while (lexer.getTokenType() != null) {
            IElementType type = lexer.getTokenType();
            String text = code.substring(lexer.getTokenStart(), lexer.getTokenEnd()).trim();

            // Normalize keywords: remove internal spaces, but preserve case
            // (PC-1500 is case-sensitive - lowercase "rem" is not a keyword!)
            if (type.toString().contains("KEYWORD")) {
                text = text.replaceAll("\\s+", "");
            }

            tokens.add(new TokenInfo(type, text));
            lexer.advance();
        }

        StringBuilder result = new StringBuilder();
        boolean inComment = false;

        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo current = tokens.get(i);
            TokenInfo next = (i + 1 < tokens.size()) ? tokens.get(i + 1) : null;

            // Check if entering comment mode
            if (current.type == SharpBasicTypes.KEYWORD && current.text.equalsIgnoreCase("REM")) {
                inComment = true;
                result.append("REM");
                // Space after REM
                if (next != null) {
                    result.append(' ');
                }
                continue;
            }

            // In comment mode: preserve everything as-is
            if (inComment) {
                if (current.type == SharpBasicTypes.COMMENT) {
                    // Extract comment text after REM
                    String commentText = current.text;
                    if (commentText.startsWith("REM")) {
                        commentText = commentText.substring(3);
                    }
                    result.append(commentText);
                } else {
                    result.append(current.text);
                }
                continue;
            }

            // Append current token
            result.append(current.text);

            // Determine spacing before next token
            if (next != null) {
                String spacing = getSpacingBetween(current, next);
                result.append(spacing);
            }
        }

        return result.toString();
    }

    /**
     * Determines the spacing between two tokens according to PC-1500 rules.
     */
    private static String getSpacingBetween(TokenInfo current, TokenInfo next) {
        String type1 = current.type.toString();
        String type2 = next.type.toString();
        String text1 = current.text;

        // No space before colon
        if (isType(type2, "COLON")) {
            return "";
        }

        // No space after colon, except before certain keywords
        if (isType(type1, "COLON")) {
            // Space after colon only before specific keywords like REM, NEXT, etc.
            if (isType(type2, "KEYWORD") && (next.text.equalsIgnoreCase("REM") ||
                                              next.text.equalsIgnoreCase("NEXT"))) {
                return "";
            }
            return "";
        }

        // Space BEFORE comparison operators (not after) - check this first!
        if (isComparisonOperator(type2)) {
            // Add space before comparison operator if previous was identifier, keyword, or string
            if (isType(type1, "IDENTIFIER") || isType(type1, "KEYWORD") ||
                isType(type1, "STRING") || isType(type1, "NUMBER") ||
                isType(type1, "RPAREN")) {
                return " ";
            }
        }

        // Space after keyword, but context-dependent
        if (isType(type1, "KEYWORD")) {
            // No space before comma or semicolon
            if (isType(type2, "COMMA") || isType(type2, "SEMICOLON")) {
                return "";
            }
            // Space before opening paren (function calls like "INT (")
            if (isType(type2, "LPAREN")) {
                return " ";
            }
            // Space before line numbers after GOTO/GOSUB/THEN
            if (isType(type2, "LINE_NUMBER")) {
                return " ";
            }
            // No space before = or arithmetic operators
            if (isType(type2, "EQ") || isArithmeticOperator(type2)) {
                return "";
            }
            // Space before identifiers, strings, etc.
            return " ";
        }

        // No space AFTER comparison operators
        if (isComparisonOperator(type1)) {
            return "";
        }

        // Space BEFORE specific keywords (like THEN, STEP) after numbers, identifiers, or operators
        if (isType(type2, "KEYWORD")) {
            // Space before THEN after comparison
            if (next.text.equalsIgnoreCase("THEN")) {
                if (isType(type1, "NUMBER") || isType(type1, "IDENTIFIER") ||
                    isType(type1, "STRING") || isType(type1, "RPAREN")) {
                    return " ";
                }
            }
            // No space before TO, REM, GOTO in most contexts
            // They get handled by the keyword-after-keyword rule
        }

        // No space around = in assignments
        if (isType(type1, "EQ") || isType(type2, "EQ")) {
            return "";
        }

        // No space before/after comma
        if (isType(type2, "COMMA") || isType(type1, "COMMA")) {
            return "";
        }

        // No space around arithmetic operators
        if (isArithmeticOperator(type1) || isArithmeticOperator(type2)) {
            return "";
        }

        // No space around parentheses (except keyword before LPAREN handled above)
        if (isType(type1, "LPAREN") || isType(type2, "LPAREN") ||
            isType(type1, "RPAREN") || isType(type2, "RPAREN")) {
            return "";
        }

        // Default: no space
        return "";
    }

    private static boolean isType(String typeStr, String expectedType) {
        // Match exact type name (handles "SharpBasicTokenType.KEYWORD" format)
        return typeStr.endsWith("." + expectedType) || typeStr.equals(expectedType);
    }

    private static boolean isComparisonOperator(String typeStr) {
        return isType(typeStr, "LT") || isType(typeStr, "GT") ||
               isType(typeStr, "LE") || isType(typeStr, "GE") ||
               isType(typeStr, "NE");
    }

    private static boolean isArithmeticOperator(String typeStr) {
        return isType(typeStr, "PLUS") || isType(typeStr, "MINUS") ||
               isType(typeStr, "MULT") || isType(typeStr, "DIV") ||
               isType(typeStr, "POWER");
    }

    private static class TokenInfo {
        final IElementType type;
        final String text;

        TokenInfo(IElementType type, String text) {
            this.type = type;
            this.text = text;
        }
    }
}
