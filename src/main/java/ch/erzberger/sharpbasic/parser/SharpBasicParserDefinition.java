package ch.erzberger.sharpbasic.parser;

import ch.erzberger.sharpbasic.SharpBasicLanguage;
import ch.erzberger.sharpbasic.lexer.SharpBasicLexerAdapter;
import ch.erzberger.sharpbasic.psi.SharpBasicFile;
import ch.erzberger.sharpbasic.psi.SharpBasicTokenType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * Parser definition for Sharp BASIC language.
 */
public class SharpBasicParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(SharpBasicLanguage.INSTANCE);

    public static final TokenSet WHITE_SPACES = TokenSet.create(
            com.intellij.psi.TokenType.WHITE_SPACE
    );

    public static final TokenSet COMMENTS = TokenSet.create(
            new SharpBasicTokenType("COMMENT")
    );

    public static final TokenSet STRINGS = TokenSet.create(
            new SharpBasicTokenType("STRING")
    );

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new SharpBasicLexerAdapter();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        return new SharpBasicParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        // This will be handled by the generated parser types
        // For now, use a basic wrapper
        return new com.intellij.extapi.psi.ASTWrapperPsiElement(node);
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new SharpBasicFile(viewProvider);
    }
}
