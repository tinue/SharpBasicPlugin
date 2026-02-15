package ch.erzberger.sharpbasic.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import ch.erzberger.sharpbasic.psi.SharpBasicTypes;

/**
 * External parser utilities for Sharp BASIC grammar.
 * These methods are called from the generated parser via external predicates.
 */
public class SharpBasicParserUtil extends GeneratedParserUtilBase {

    /**
     * Checks if the current token can start an expression.
     * This is used to avoid calling the expression parser when we know it will fail,
     * preventing spurious error messages.
     *
     * Valid expression start tokens:
     * - NUMBER: numeric literal
     * - STRING: string literal
     * - IDENTIFIER: variable or function name
     * - KEYWORD: keyword function or operator (like NOT)
     * - LPAREN: parenthesized expression
     * - PLUS: unary plus
     * - MINUS: unary minus
     *
     * @param builder the PSI builder
     * @param level current parsing level (unused but required by GeneratedParserUtilBase)
     * @return true if the current token can start an expression
     */
    public static boolean isExpressionStart(PsiBuilder builder, int level) {
        return builder.getTokenType() == SharpBasicTypes.NUMBER
            || builder.getTokenType() == SharpBasicTypes.STRING
            || builder.getTokenType() == SharpBasicTypes.IDENTIFIER
            || builder.getTokenType() == SharpBasicTypes.KEYWORD
            || builder.getTokenType() == SharpBasicTypes.LPAREN
            || builder.getTokenType() == SharpBasicTypes.PLUS
            || builder.getTokenType() == SharpBasicTypes.MINUS;
    }
}
