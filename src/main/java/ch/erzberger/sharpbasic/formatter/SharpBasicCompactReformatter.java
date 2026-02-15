package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.keywords.BasicKeyword;
import ch.erzberger.sharpbasic.keywords.KeywordRegistry;
import ch.erzberger.sharpbasic.lexer.PreprocessingSharpBasicLexer;
import ch.erzberger.sharpbasic.psi.SharpBasicTypes;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

/**
 * Compact reformatter for Sharp BASIC code - minimizes code size.
 * - No spaces after keywords or line numbers
 * - Uses abbreviated keyword forms (P. instead of PRINT)
 * - Keeps comments (user can manually shorten if too long)
 */
public class SharpBasicCompactReformatter {

    /**
     * Reformats code to be as compact as possible.
     *
     * @param code the code to reformat
     * @return the compacted code
     */
    public static String reformat(String code) {
        StringBuilder result = new StringBuilder();

        // Detect line ending style from the original code
        String lineEnding = detectLineEnding(code);

        String[] lines = code.split("\\r\\n|\\r|\\n");

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

            String formattedLine = reformatLine(trimmed);

            // Skip lines that are only comments
            if (!formattedLine.isEmpty()) {
                result.append(formattedLine);

                // Only append line ending if not the last line
                if (i < lines.length - 1) {
                    result.append(lineEnding);
                }
            }
        }

        return result.toString();
    }

    /**
     * Detects the line ending style used in the code.
     */
    private static String detectLineEnding(String code) {
        if (code.contains("\r\n")) {
            return "\r\n";
        } else if (code.contains("\r")) {
            return "\r";
        } else if (code.contains("\n")) {
            return "\n";
        }
        return System.lineSeparator();
    }

    /**
     * Extracts line number and length from the beginning of a line.
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
                continue;
            } else {
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
     * Reformats a single line - compact style (no spaces, abbreviations, no comments).
     */
    private static String reformatLine(String line) {
        StringBuilder result = new StringBuilder();

        // Extract line number
        int[] lineNumData = extractLineNumberAndLength(line);
        int lineNumber = lineNumData[0];
        int charsConsumed = lineNumData[1];
        String restOfLine = line;

        if (lineNumber > 0) {
            // Output line number (no spaces)
            result.append(lineNumber);
            restOfLine = line.substring(charsConsumed).trim();
        }

        // Tokenize and compact the rest of the line
        String compactedCode = compactCode(restOfLine);
        result.append(compactedCode);

        return result.toString();
    }

    /**
     * Compacts code by removing spaces, using abbreviations, keeping comments.
     */
    private static String compactCode(String code) {
        if (code.isEmpty()) {
            return "";
        }

        PreprocessingSharpBasicLexer lexer = new PreprocessingSharpBasicLexer();
        lexer.start(code, 0, code.length(), 0);

        List<TokenInfo> tokens = new ArrayList<>();
        boolean inComment = false;

        while (lexer.getTokenType() != null) {
            IElementType type = lexer.getTokenType();
            String text = code.substring(lexer.getTokenStart(), lexer.getTokenEnd()).trim();

            // Handle keywords
            if (type == SharpBasicTypes.KEYWORD) {
                // Remove internal spaces
                text = text.replaceAll("\\s+", "");

                // Check for REM keyword (enters comment mode)
                if (text.equalsIgnoreCase("REM")) {
                    inComment = true;
                    // Keep REM as abbreviation
                    tokens.add(new TokenInfo(type, "REM"));
                    lexer.advance();
                    continue;
                }

                // Check if keyword has # suffix
                boolean hasHash = text.endsWith("#");
                String textWithoutHash = hasHash ? text.substring(0, text.length() - 1) : text;

                // Convert to abbreviated form only if it saves space
                BasicKeyword keyword = KeywordRegistry.lookup(text.toUpperCase());
                if (keyword != null) {
                    String abbrev = keyword.getRawAbbreviation();
                    String fullName = keyword.getName();

                    // Only use abbreviation if it's actually shorter
                    // Compare: abbreviation+period vs full keyword
                    String abbreviatedForm = hasHash ? (abbrev + "#.") : (abbrev + ".");
                    String fullForm = hasHash ? (fullName + "#") : fullName;

                    if (abbreviatedForm.length() < fullForm.length()) {
                        // Abbreviation saves space - use it
                        text = abbreviatedForm;
                    } else {
                        // Abbreviation doesn't save space - use full keyword
                        text = fullForm;
                    }
                }
            }

            // Keep comments (user can manually shorten if needed)
            if (type == SharpBasicTypes.COMMENT || inComment) {
                tokens.add(new TokenInfo(type, text));
                lexer.advance();
                continue;
            }

            tokens.add(new TokenInfo(type, text));
            lexer.advance();
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo current = tokens.get(i);
            // Append token text with no spacing
            result.append(current.text);
        }

        return result.toString();
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
