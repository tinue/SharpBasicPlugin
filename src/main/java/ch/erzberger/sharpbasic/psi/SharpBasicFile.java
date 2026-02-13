package ch.erzberger.sharpbasic.psi;

import ch.erzberger.sharpbasic.SharpBasicFileType;
import ch.erzberger.sharpbasic.SharpBasicLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * PSI representation of a Sharp BASIC file.
 */
public class SharpBasicFile extends PsiFileBase {
    public SharpBasicFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SharpBasicLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SharpBasicFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Sharp BASIC File";
    }
}
