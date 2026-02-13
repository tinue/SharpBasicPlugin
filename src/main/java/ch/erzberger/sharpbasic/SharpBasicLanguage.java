package ch.erzberger.sharpbasic;

import com.intellij.lang.Language;

/**
 * Defines the Sharp PC-1500 BASIC language for IntelliJ IDEA.
 */
public class SharpBasicLanguage extends Language {
    public static final SharpBasicLanguage INSTANCE = new SharpBasicLanguage();

    private SharpBasicLanguage() {
        super("SharpBasic");
    }
}
