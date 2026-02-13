package ch.erzberger.sharpbasic.psi;

import ch.erzberger.sharpbasic.SharpBasicLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Token type for Sharp BASIC lexer tokens.
 */
public class SharpBasicTokenType extends IElementType {
    public SharpBasicTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SharpBasicLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "SharpBasicTokenType." + super.toString();
    }
}
