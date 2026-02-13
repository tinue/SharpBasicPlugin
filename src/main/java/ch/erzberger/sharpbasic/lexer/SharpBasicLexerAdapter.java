package ch.erzberger.sharpbasic.lexer;

import com.intellij.lexer.FlexAdapter;

/**
 * Adapter to wrap the JFlex-generated lexer for use in IntelliJ.
 */
public class SharpBasicLexerAdapter extends FlexAdapter {
    public SharpBasicLexerAdapter() {
        super(new SharpBasicLexer(null));
    }
}
