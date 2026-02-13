package ch.erzberger.sharpbasic.psi;

import com.intellij.psi.tree.IElementType;

/**
 * Token type constants for Sharp BASIC lexer.
 * These are reused across all tokens to avoid creating too many element types.
 */
public interface SharpBasicTokenTypes {
    IElementType KEYWORD = new SharpBasicTokenType("KEYWORD");
    IElementType LINE_NUMBER = new SharpBasicTokenType("LINE_NUMBER");
    IElementType NUMBER = new SharpBasicTokenType("NUMBER");
    IElementType STRING = new SharpBasicTokenType("STRING");
    IElementType IDENTIFIER = new SharpBasicTokenType("IDENTIFIER");
    IElementType COMMENT = new SharpBasicTokenType("COMMENT");

    // Operators
    IElementType PLUS = new SharpBasicTokenType("PLUS");
    IElementType MINUS = new SharpBasicTokenType("MINUS");
    IElementType MULT = new SharpBasicTokenType("MULT");
    IElementType DIV = new SharpBasicTokenType("DIV");
    IElementType POWER = new SharpBasicTokenType("POWER");
    IElementType EQ = new SharpBasicTokenType("EQ");
    IElementType LT = new SharpBasicTokenType("LT");
    IElementType GT = new SharpBasicTokenType("GT");
    IElementType LE = new SharpBasicTokenType("LE");
    IElementType GE = new SharpBasicTokenType("GE");
    IElementType NE = new SharpBasicTokenType("NE");

    // Separators
    IElementType LPAREN = new SharpBasicTokenType("LPAREN");
    IElementType RPAREN = new SharpBasicTokenType("RPAREN");
    IElementType COMMA = new SharpBasicTokenType("COMMA");
    IElementType SEMICOLON = new SharpBasicTokenType("SEMICOLON");
    IElementType COLON = new SharpBasicTokenType("COLON");
    IElementType HASH = new SharpBasicTokenType("HASH");

    // Special
    IElementType LINE_TERMINATOR = new SharpBasicTokenType("LINE_TERMINATOR");
}
