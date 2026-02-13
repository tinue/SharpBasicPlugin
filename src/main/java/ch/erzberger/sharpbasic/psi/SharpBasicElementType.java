package ch.erzberger.sharpbasic.psi;

import ch.erzberger.sharpbasic.SharpBasicLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Element type for Sharp BASIC parser elements.
 */
public class SharpBasicElementType extends IElementType {
    public SharpBasicElementType(@NotNull @NonNls String debugName) {
        super(debugName, SharpBasicLanguage.INSTANCE);
    }
}
