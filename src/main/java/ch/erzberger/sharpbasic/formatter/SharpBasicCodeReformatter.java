package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.keywords.BasicKeyword;
import ch.erzberger.sharpbasic.keywords.KeywordRegistry;
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

        // Detect line ending style from the original code
        String lineEnding = detectLineEnding(code);

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
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                // Only append line ending if not the last line
                if (i < lines.length - 1) {
                    result.append(lineEnding);
                }
                continue;
            }

            String formattedLine = reformatLine(trimmed, maxLineNumberWidth);
            result.append(formattedLine);

            // Only append line ending if not the last line
            if (i < lines.length - 1) {
                result.append(lineEnding);
            }
        }

        return result.toString();
    }

    /**
     * Detects the line ending style used in the code.
     * Returns "\r\n" (CRLF), "\r" (CR), or "\n" (LF).
     * Defaults to system line separator if no line endings found.
     */
    private static String detectLineEnding(String code) {
        if (code.contains("\r\n")) {
            return "\r\n";  // Windows CRLF
        } else if (code.contains("\r")) {
            return "\r";     // Old Mac CR
        } else if (code.contains("\n")) {
            return "\n";     // Unix/Mac LF
        }
        // No line endings found, use system default
        return System.lineSeparator();
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

            // Normalize and expand keywords
            if (type.toString().contains("KEYWORD")) {
                // Remove internal spaces
                text = text.replaceAll("\\s+", "");

                // Expand abbreviations to full keyword names
                // E.g., "P." -> "PRINT", "G." -> "GOTO"
                BasicKeyword keyword = KeywordRegistry.lookup(text.toUpperCase());
                if (keyword != null) {
                    text = keyword.getName();
                }
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
            if (current.type == SharpBasicTypes.COMMENT || (current.type == SharpBasicTypes.KEYWORD && current.text.equalsIgnoreCase("REM"))) {
                inComment = true;
                if (current.type == SharpBasicTypes.KEYWORD) {
                    result.append("REM");
                    // Ensure a space after REM if the next token (comment) doesn't start with one
                    if (next != null && next.type == SharpBasicTypes.COMMENT && !next.text.startsWith(" ") && !next.text.startsWith("\t")) {
                        result.append(" ");
                    }
                } else {
                    result.append(current.text);
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
     * PC-1500 formatting is simple: only add space AFTER certain tokens, never BEFORE.
     */
    private static String getSpacingBetween(TokenInfo current, TokenInfo next) {
        String type1 = current.type.toString();

        // Rule 1: ALWAYS space after keyword
        if (isType(type1, "KEYWORD")) {
            return " ";
        }

        // Rule 2: Space after line number (handled in reformatLine, not here)

        // Default: no space
        // PC-1500 never adds space BEFORE anything
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
