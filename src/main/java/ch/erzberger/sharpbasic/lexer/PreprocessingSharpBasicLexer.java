package ch.erzberger.sharpbasic.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Preprocessing wrapper for SharpBasicLexer that removes whitespace outside of strings and comments.
 * This implements the authentic Sharp PC-1500 behavior where keywords can have spaces between letters.
 * For example: "P R I N T" is recognized as "PRINT", "R  E M" is recognized as "REM".
 */
public class PreprocessingSharpBasicLexer extends LexerBase {
    private final SharpBasicLexer lexer;
    private CharSequence originalBuffer;
    private int originalStart;
    private int originalEnd;

    private List<TokenInfo> tokens;
    private int currentTokenIndex;

    public PreprocessingSharpBasicLexer() {
        this.lexer = new SharpBasicLexer((java.io.Reader) null);
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.originalBuffer = buffer;
        this.originalStart = startOffset;
        this.originalEnd = endOffset;
        this.tokens = new ArrayList<>();
        this.currentTokenIndex = 0;

        // Preprocess the buffer: remove whitespace except in strings and comments
        String substring = buffer.subSequence(startOffset, endOffset).toString();
        PreprocessResult result = preprocessBuffer(substring);

        // Lex the preprocessed buffer
        lexer.reset(result.text, 0, result.text.length(), initialState);

        try {
            int currentOriginalPos = startOffset;
            IElementType tokenType;

            while ((tokenType = lexer.advance()) != null) {
                int preprocStart = lexer.getTokenStart();
                int preprocEnd = lexer.getTokenEnd();

                // Map back to original positions
                int origStart = currentOriginalPos;
                int origEnd;

                // Find the original end position
                if (preprocEnd < result.preprocessedToOriginal.length) {
                    origEnd = startOffset + result.preprocessedToOriginal[preprocEnd - 1] + 1;
                } else {
                    origEnd = endOffset;
                }

                // Ensure the next token starts where this one ends
                currentOriginalPos = origEnd;

                tokens.add(new TokenInfo(tokenType, origStart, origEnd));
            }
        } catch (java.io.IOException e) {
            // Handle exception
        }
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public IElementType getTokenType() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex).type;
        }
        return null;
    }

    @Override
    public int getTokenStart() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex).start;
        }
        return originalEnd;
    }

    @Override
    public int getTokenEnd() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex).end;
        }
        return originalEnd;
    }

    @Override
    public void advance() {
        currentTokenIndex++;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return originalBuffer;
    }

    @Override
    public int getBufferEnd() {
        return originalEnd;
    }

    /**
     * Preprocesses the buffer by removing whitespace outside of strings and comments.
     */
    private PreprocessResult preprocessBuffer(String input) {
        StringBuilder result = new StringBuilder();
        int[] preprocToOrig = new int[input.length()];

        boolean inString = false;
        boolean inComment = false;
        int preprocessedPos = 0;

        for (int i = 0; i < input.length(); ) {
            char c = input.charAt(i);

            // Check for string start/end
            if (c == '"' && !inComment) {
                inString = !inString;
                result.append(c);
                preprocToOrig[preprocessedPos++] = i;
                i++;
                continue;
            }

            // Check for comment start (apostrophe only)
            // REM is handled by the underlying lexer as a keyword that enters comment mode
            if (!inString && !inComment) {
                // Check for REM keyword (with or without spaces)
                // PC-1500 is case-sensitive: only recognize uppercase REM
                if (c == 'R') {  // Only uppercase R
                    // Check for "R E M" with spaces OR "REM" without spaces
                    // PC-1500 is case-sensitive: only uppercase REM is a keyword
                    int pos = i + 1;
                    while (pos < input.length() && (input.charAt(pos) == ' ' || input.charAt(pos) == '\t')) {
                        pos++;
                    }
                    if (pos < input.length() && input.charAt(pos) == 'E') {  // Only uppercase E
                        int ePos = pos;
                        pos++;
                        while (pos < input.length() && (input.charAt(pos) == ' ' || input.charAt(pos) == '\t')) {
                            pos++;
                        }
                        if (pos < input.length() && input.charAt(pos) == 'M') {  // Only uppercase M
                            // Found "R E M" with spaces - collapse to "REM"
                            result.append("REM");
                            preprocToOrig[preprocessedPos++] = i;
                            preprocToOrig[preprocessedPos++] = ePos;
                            preprocToOrig[preprocessedPos++] = pos;
                            // Enter comment mode - preserve everything after REM
                            inComment = true;
                            i = pos + 1;
                            continue;
                        }
                    } else if (pos == i + 1 && pos < input.length() && input.charAt(pos) == 'E') {
                        // "RE" without space after R
                        if (pos + 1 < input.length() && input.charAt(pos + 1) == 'M') {
                            // Found "REM" without spaces - just pass through and enter comment mode
                            result.append("REM");
                            preprocToOrig[preprocessedPos++] = i;
                            preprocToOrig[preprocessedPos++] = pos;
                            preprocToOrig[preprocessedPos++] = pos + 1;
                            inComment = true;
                            i = pos + 2;
                            continue;
                        }
                    }
                }

                // Check for apostrophe comment
                if (c == '\'') {
                    inComment = true;
                    result.append(c);
                    preprocToOrig[preprocessedPos++] = i;
                    i++;
                    continue;
                }
            }

            // Check for line terminator (ends comment)
            if ((c == '\n' || c == '\r') && inComment) {
                inComment = false;
                result.append(c);
                preprocToOrig[preprocessedPos++] = i;
                i++;
                continue;
            }

            // Inside string or comment: preserve everything
            if (inString || inComment) {
                result.append(c);
                preprocToOrig[preprocessedPos++] = i;
                i++;
                continue;
            }

            // Outside string and comment: skip whitespace
            if (c == ' ' || c == '\t') {
                i++;
                continue;
            }

            // Everything else: keep it
            result.append(c);
            preprocToOrig[preprocessedPos++] = i;
            i++;
        }

        // Trim array
        int[] finalPreprocToOrig = new int[preprocessedPos];
        System.arraycopy(preprocToOrig, 0, finalPreprocToOrig, 0, preprocessedPos);

        return new PreprocessResult(result.toString(), finalPreprocToOrig);
    }

    private static class PreprocessResult {
        final String text;
        final int[] preprocessedToOriginal;

        PreprocessResult(String text, int[] preprocessedToOriginal) {
            this.text = text;
            this.preprocessedToOriginal = preprocessedToOriginal;
        }
    }

    private static class TokenInfo {
        final IElementType type;
        final int start;
        final int end;

        TokenInfo(IElementType type, int start, int end) {
            this.type = type;
            this.start = start;
            this.end = end;
        }
    }
}
