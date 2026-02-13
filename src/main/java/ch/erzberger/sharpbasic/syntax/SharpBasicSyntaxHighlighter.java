package ch.erzberger.sharpbasic.syntax;

import ch.erzberger.sharpbasic.lexer.SharpBasicLexerAdapter;
import ch.erzberger.sharpbasic.psi.SharpBasicTokenType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Syntax highlighter for Sharp BASIC.
 */
public class SharpBasicSyntaxHighlighter extends SyntaxHighlighterBase {
    // Define color keys for different token types
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("SHARP_BASIC_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey LINE_NUMBER =
            createTextAttributesKey("SHARP_BASIC_LINE_NUMBER", DefaultLanguageHighlighterColors.METADATA);

    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("SHARP_BASIC_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("SHARP_BASIC_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("SHARP_BASIC_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("SHARP_BASIC_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("SHARP_BASIC_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);

    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("SHARP_BASIC_SEPARATOR", DefaultLanguageHighlighterColors.COMMA);

    // Empty keys array for tokens that don't need highlighting
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    // Arrays for different token types
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] LINE_NUMBER_KEYS = new TextAttributesKey[]{LINE_NUMBER};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SharpBasicLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType == null) {
            return EMPTY_KEYS;
        }

        String tokenName = tokenType.toString();
        if (tokenName.contains("KEYWORD")) {
            return KEYWORD_KEYS;
        } else if (tokenName.contains("LINE_NUMBER")) {
            return LINE_NUMBER_KEYS;
        } else if (tokenName.contains("NUMBER")) {
            return NUMBER_KEYS;
        } else if (tokenName.contains("STRING")) {
            return STRING_KEYS;
        } else if (tokenName.contains("COMMENT")) {
            return COMMENT_KEYS;
        } else if (tokenName.contains("IDENTIFIER")) {
            return IDENTIFIER_KEYS;
        } else if (isOperator(tokenName)) {
            return OPERATOR_KEYS;
        } else if (isSeparator(tokenName)) {
            return SEPARATOR_KEYS;
        }

        return EMPTY_KEYS;
    }

    private boolean isOperator(String tokenName) {
        return tokenName.contains("PLUS") || tokenName.contains("MINUS") ||
               tokenName.contains("MULT") || tokenName.contains("DIV") ||
               tokenName.contains("POWER") || tokenName.contains("EQ") ||
               tokenName.contains("LT") || tokenName.contains("GT") ||
               tokenName.contains("LE") || tokenName.contains("GE") ||
               tokenName.contains("NE");
    }

    private boolean isSeparator(String tokenName) {
        return tokenName.contains("LPAREN") || tokenName.contains("RPAREN") ||
               tokenName.contains("COMMA") || tokenName.contains("SEMICOLON") ||
               tokenName.contains("COLON") || tokenName.contains("HASH");
    }
}
