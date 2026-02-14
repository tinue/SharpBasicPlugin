package ch.erzberger.sharpbasic.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Formatting model builder for Sharp PC-1500 BASIC.
 * Implements the authentic PC-1500 formatting style.
 */
public class SharpBasicFormattingModelBuilder implements FormattingModelBuilder {

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        PsiElement element = formattingContext.getPsiElement();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();

        SharpBasicBlock rootBlock = new SharpBasicBlock(
            element.getNode(),
            null,
            Alignment.createAlignment(),
            Indent.getNoneIndent(),
            null
        );

        return FormattingModelProvider.createFormattingModelForPsiFile(
            element.getContainingFile(),
            rootBlock,
            settings
        );
    }

    @Override
    public @Nullable TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }
}
