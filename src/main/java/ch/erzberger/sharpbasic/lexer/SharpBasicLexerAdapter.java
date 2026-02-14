package ch.erzberger.sharpbasic.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Adapter to wrap the JFlex-generated lexer with preprocessing for use in IntelliJ.
 * Uses PreprocessingSharpBasicLexer to handle Sharp PC-1500's unique behavior where
 * keywords can have spaces between letters (e.g., "R  E M" is recognized as "REM").
 */
public class SharpBasicLexerAdapter extends LexerBase {
    private final PreprocessingSharpBasicLexer lexer;

    public SharpBasicLexerAdapter() {
        this.lexer = new PreprocessingSharpBasicLexer();
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        lexer.start(buffer, startOffset, endOffset, initialState);
    }

    @Override
    public int getState() {
        return lexer.getState();
    }

    @Override
    public IElementType getTokenType() {
        return lexer.getTokenType();
    }

    @Override
    public int getTokenStart() {
        return lexer.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        return lexer.getTokenEnd();
    }

    @Override
    public void advance() {
        lexer.advance();
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return lexer.getBufferSequence();
    }

    @Override
    public int getBufferEnd() {
        return lexer.getBufferEnd();
    }
}
