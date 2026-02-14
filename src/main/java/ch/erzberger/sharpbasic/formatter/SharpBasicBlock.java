package ch.erzberger.sharpbasic.formatter;

import ch.erzberger.sharpbasic.psi.SharpBasicTypes;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Formatting block for Sharp PC-1500 BASIC code.
 * Implements spacing rules that match the PC-1500's canonical output format.
 */
public class SharpBasicBlock extends AbstractBlock {

    private final SpacingBuilder spacingBuilder;

    protected SharpBasicBlock(@NotNull ASTNode node,
                              @Nullable Wrap wrap,
                              @Nullable Alignment alignment,
                              @NotNull Indent indent,
                              SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();

        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                Block block = new SharpBasicBlock(
                    child,
                    null,
                    Alignment.createAlignment(),
                    Indent.getNoneIndent(),
                    spacingBuilder
                );
                blocks.add(block);
            }
            child = child.getTreeNext();
        }

        return blocks;
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        if (spacingBuilder != null) {
            Spacing spacing = spacingBuilder.getSpacing(this, child1, child2);
            if (spacing != null) {
                return spacing;
            }
        }

        // Custom spacing rules for PC-1500 formatting
        if (child1 instanceof SharpBasicBlock && child2 instanceof SharpBasicBlock) {
            IElementType type1 = ((SharpBasicBlock) child1).getNode().getElementType();
            IElementType type2 = ((SharpBasicBlock) child2).getNode().getElementType();

            return getPC1500Spacing(type1, type2);
        }

        return null;
    }

    /**
     * Returns spacing according to PC-1500 formatting rules.
     */
    private Spacing getPC1500Spacing(IElementType type1, IElementType type2) {
        // Line number followed by space
        if (type1 == SharpBasicTypes.LINE_NUMBER) {
            return Spacing.createSpacing(1, 1, 0, false, 0);
        }

        // Space after keywords (except in specific cases)
        if (type1 == SharpBasicTypes.KEYWORD) {
            // No space before certain delimiters
            if (type2 == SharpBasicTypes.COLON || type2 == SharpBasicTypes.COMMA ||
                type2 == SharpBasicTypes.SEMICOLON) {
                return Spacing.createSpacing(0, 0, 0, false, 0);
            }
            // Space before opening parenthesis (function calls)
            if (type2 == SharpBasicTypes.LPAREN) {
                return Spacing.createSpacing(1, 1, 0, false, 0);
            }
            // Space after keyword before most other tokens
            return Spacing.createSpacing(1, 1, 0, false, 0);
        }

        // No space before colon
        if (type2 == SharpBasicTypes.COLON) {
            return Spacing.createSpacing(0, 0, 0, false, 0);
        }

        // No space after colon
        if (type1 == SharpBasicTypes.COLON) {
            return Spacing.createSpacing(0, 0, 0, false, 0);
        }

        // Space around comparison operators
        if (isComparisonOperator(type1) || isComparisonOperator(type2)) {
            return Spacing.createSpacing(1, 1, 0, false, 0);
        }

        // No space around assignment =
        if (type1 == SharpBasicTypes.EQ || type2 == SharpBasicTypes.EQ) {
            // Check context - if in assignment (not comparison), no space
            return Spacing.createSpacing(0, 0, 0, false, 0);
        }

        // Default: no forced spacing
        return null;
    }

    private boolean isComparisonOperator(IElementType type) {
        return type == SharpBasicTypes.LT || type == SharpBasicTypes.GT ||
               type == SharpBasicTypes.LE || type == SharpBasicTypes.GE ||
               type == SharpBasicTypes.NE;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    @Override
    public @NotNull Indent getIndent() {
        return Indent.getNoneIndent();
    }
}
