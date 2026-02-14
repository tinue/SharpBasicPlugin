package ch.erzberger.sharpbasic.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ch.erzberger.sharpbasic.psi.SharpBasicTokenTypes;
import ch.erzberger.sharpbasic.keywords.KeywordRegistry;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static ch.erzberger.sharpbasic.psi.SharpBasicTokenTypes.*;

%%

%class SharpBasicLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%{
  private boolean atLineStart = true;
%}

// Token type definitions
LINE_TERMINATOR = \r\n | \r | \n
WHITE_SPACE = [ \t]+
LINE_NUMBER = [0-9]+
INTEGER = [0-9]+
DECIMAL = [0-9]+ \. [0-9]+
SCIENTIFIC = [0-9]+ (\. [0-9]+)? [Ee] [+\-]? [0-9]+
STRING = \"([^\"\r\n]|\"\")*\"
IDENTIFIER = [A-Za-z][A-Za-z0-9]*\$?
COMMENT_START = [Rr][Ee][Mm]

// Operators and separators
PLUS = \+
MINUS = \-
MULT = \*
DIV = \/
POWER = \^
EQ = =
LT = <
GT = >
LE = <=
GE = >=
NE = <> | ><
LPAREN = \(
RPAREN = \)
COMMA = ,
SEMICOLON = ;
COLON = :
HASH = \#
PERIOD = \.
APOSTROPHE = '

%state IN_COMMENT

%%

<YYINITIAL> {
  {LINE_TERMINATOR}       { atLineStart = true; return LINE_TERMINATOR; }
  {WHITE_SPACE}           { return WHITE_SPACE; }

  // Numbers - check if at line start to determine LINE_NUMBER vs NUMBER
  {SCIENTIFIC}            {
    if (atLineStart) {
      atLineStart = false;
      return LINE_NUMBER;
    }
    atLineStart = false;
    return NUMBER;
  }
  {DECIMAL}               {
    if (atLineStart) {
      atLineStart = false;
      return LINE_NUMBER;
    }
    atLineStart = false;
    return NUMBER;
  }
  {INTEGER}               {
    if (atLineStart) {
      atLineStart = false;
      return LINE_NUMBER;
    }
    atLineStart = false;
    return NUMBER;
  }

  // Strings
  {STRING}                { atLineStart = false; return STRING; }

  // Comment detection - REM keyword
  {COMMENT_START} ({WHITE_SPACE} | {LINE_TERMINATOR})? {
    atLineStart = false;
    yybegin(IN_COMMENT);
    return COMMENT;
  }

  // Comment detection - single quote/apostrophe (alternative to REM)
  {APOSTROPHE} {
    atLineStart = false;
    yybegin(IN_COMMENT);
    return COMMENT;
  }

  // Identifiers and keywords (including those with $ suffix like INKEY$)
  {IDENTIFIER} ({PERIOD})? {
    atLineStart = false;
    String text = yytext().toString().toUpperCase();
    boolean hasPeriod = text.endsWith(".");
    boolean hasDollar = !hasPeriod && text.endsWith("$");

    // Remove trailing period if present (for abbreviations)
    if (hasPeriod) {
      text = text.substring(0, text.length() - 1);
    }

    // Check if the entire token is a keyword (including $ suffix)
    if (KeywordRegistry.isKeyword(text)) {
      // Special case: REM is a comment keyword - return as KEYWORD but enter comment mode
      if (text.equals("REM")) {
        if (hasPeriod && yytext().toString().endsWith(".")) {
          yypushback(1);
        }
        yybegin(IN_COMMENT);
        return KEYWORD;  // Return KEYWORD so it gets keyword coloring
      }

      if (hasPeriod && yytext().toString().endsWith(".")) {
        // Don't consume the period if it's after a keyword
        yypushback(1);
      }
      return KEYWORD;
    }

    // Try to find the longest matching keyword from the start
    // This implements Sharp BASIC's greedy tokenization (e.g., "QAND" -> "Q" then retry "AND")
    // For keywords with $, we need to check with the $ included
    int maxKeywordLength = Math.min(text.length(), 8); // Keywords are max 8 chars
    for (int len = maxKeywordLength; len >= 2; len--) {
      String candidate = text.substring(0, len);
      if (KeywordRegistry.isKeyword(candidate)) {
        // Special case: REM is a comment keyword - return as KEYWORD but enter comment mode
        if (candidate.equals("REM")) {
          // Push back everything after REM and enter comment mode
          int pushBackCount = text.length() - len;
          if (hasPeriod && pushBackCount == 0) {
            pushBackCount = 1;
          }
          if (pushBackCount > 0) {
            yypushback(pushBackCount);
          }
          yybegin(IN_COMMENT);
          return KEYWORD;  // Return KEYWORD so it gets keyword coloring
        }

        // Found a keyword! Push back the rest for re-lexing
        int pushBackCount = text.length() - len;
        if (hasPeriod && pushBackCount == 0) {
          // Don't push back the period if it's part of the abbreviation
          pushBackCount = 1;
        }
        if (pushBackCount > 0) {
          yypushback(pushBackCount);
        }
        return KEYWORD;
      }
    }

    // Not a keyword
    // If it has a $ suffix and isn't a keyword, it's a string variable identifier
    if (hasDollar) {
      return IDENTIFIER;
    }

    // Not a keyword - if length > 1, push back all but first char and return first char as identifier
    // This allows "QAND" to become "Q" + (retry "AND")
    if (text.length() > 1) {
      yypushback(yytext().length() - 1);
    }

    return IDENTIFIER;
  }

  // Operators
  {PLUS}                  { atLineStart = false; return PLUS; }
  {MINUS}                 { atLineStart = false; return MINUS; }
  {MULT}                  { atLineStart = false; return MULT; }
  {DIV}                   { atLineStart = false; return DIV; }
  {POWER}                 { atLineStart = false; return POWER; }
  {LE}                    { atLineStart = false; return LE; }
  {GE}                    { atLineStart = false; return GE; }
  {NE}                    { atLineStart = false; return NE; }
  {EQ}                    { atLineStart = false; return EQ; }
  {LT}                    { atLineStart = false; return LT; }
  {GT}                    { atLineStart = false; return GT; }

  // Separators
  {LPAREN}                { atLineStart = false; return LPAREN; }
  {RPAREN}                { atLineStart = false; return RPAREN; }
  {COMMA}                 { atLineStart = false; return COMMA; }
  {SEMICOLON}             { atLineStart = false; return SEMICOLON; }
  {COLON}                 { atLineStart = false; return COLON; }
  {HASH}                  { atLineStart = false; return HASH; }
}

<IN_COMMENT> {
  {LINE_TERMINATOR}       { yybegin(YYINITIAL); atLineStart = true; return LINE_TERMINATOR; }
  [^\r\n]+                { return COMMENT; }
}

[^]                       { return BAD_CHARACTER; }
