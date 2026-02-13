package ch.erzberger.sharpbasic;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * File type for Sharp PC-1500 BASIC files (.bas, .pc1500).
 */
public class SharpBasicFileType extends LanguageFileType {
    public static final SharpBasicFileType INSTANCE = new SharpBasicFileType();

    private SharpBasicFileType() {
        super(SharpBasicLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Sharp BASIC";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Sharp PC-1500 BASIC file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "bas";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SharpBasicIcons.FILE;
    }
}
