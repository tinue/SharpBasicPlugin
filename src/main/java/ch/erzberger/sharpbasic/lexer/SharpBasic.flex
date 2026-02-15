package ch.erzberger.sharpbasic.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ch.erzberger.sharpbasic.psi.SharpBasicTypes;
import ch.erzberger.sharpbasic.keywords.KeywordRegistry;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static ch.erzberger.sharpbasic.psi.SharpBasicTypes.*;

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
DECIMAL = ([0-9]+ \. [0-9]* | \. [0-9]+)
SCIENTIFIC = ([0-9]+ (\. [0-9]*)? | \. [0-9]+) [Ee] [+\-]? [0-9]+
STRING = \"([^\"\r\n]|\"\")*\"
IDENTIFIER = [A-Za-z][A-Za-z0-9]*[$#]?
COMMENT_START = REM

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

  // Comment detection - single quote/apostrophe or double-quote (alternatives to REM)
  {APOSTROPHE} {
    atLineStart = false;
    yybegin(IN_COMMENT);
    return COMMENT;
  }

  // Double-quote at start of line or after colon is also a comment marker (PC-1500 feature)
  // Note: This needs to be checked before STRING pattern
  // For now, we'll handle this conservatively - if a quote appears and we're at line/statement start

  // Identifiers and keywords (including those with $ or # suffix like INKEY$, POKE#)
  // Note: REM is handled here via greedy keyword matching, not as a separate pattern
  {IDENTIFIER} ({PERIOD})? {
    atLineStart = false;
    String text = yytext().toString();
    String textUpper = text.toUpperCase();
    boolean hasPeriod = text.endsWith(".");
    boolean hasDollar = !hasPeriod && text.endsWith("$");
    boolean hasHash = !hasPeriod && text.endsWith("#");

    // PC-1500 is case-sensitive: only recognize ALL-UPPERCASE keywords
    // Remove $ or # for case checking (INKEY$ should check INKEY, POKE# should check POKE)
    String textWithoutSuffix = text;
    if (hasDollar || hasHash) {
      textWithoutSuffix = text.substring(0, text.length() - 1);
    } else if (hasPeriod) {
      textWithoutSuffix = text.substring(0, text.length() - 1);
    }
    boolean isAllUppercase = textWithoutSuffix.equals(textWithoutSuffix.toUpperCase());

    if (isAllUppercase) {
      // If we have a period, first check if it's part of an abbreviation (like "P." for PRINT)
      if (hasPeriod && KeywordRegistry.isKeyword(textUpper)) {
        // Period is part of the abbreviation - consume it
        String keywordWithoutPeriod = textUpper.substring(0, textUpper.length() - 1);

        // Special case: REM is a comment keyword
        if (keywordWithoutPeriod.equals("REM")) {
          yybegin(IN_COMMENT);
          return KEYWORD;
        }

        return KEYWORD;
      }

      // Check if the entire token is a keyword (without period, but including $ or # suffix)
      String textUpperWithoutPeriod = hasPeriod ? textUpper.substring(0, textUpper.length() - 1) : textUpper;
      if (KeywordRegistry.isKeyword(textUpperWithoutPeriod)) {
        // Special case: REM is a comment keyword - return as KEYWORD but enter comment mode
        if (textUpperWithoutPeriod.equals("REM")) {
          if (hasPeriod) {
            yypushback(1);  // Period is not part of the keyword
          }
          yybegin(IN_COMMENT);
          return KEYWORD;
        }

        if (hasPeriod) {
          // Period is not part of the keyword - push it back
          yypushback(1);
        }
        return KEYWORD;
      }

      // Try to find the longest matching keyword from the start
      // This implements Sharp BASIC's greedy tokenization (e.g., "QAND" -> "Q" then retry "AND")
      // For keywords with $ or #, we need to check with the suffix included
      int maxKeywordLength = Math.min(textUpper.length(), 8); // Keywords are max 8 chars
      for (int len = maxKeywordLength; len >= 2; len--) {
        String candidate = textUpper.substring(0, len);
        if (KeywordRegistry.isKeyword(candidate)) {
          // Determine if the period should be consumed as part of abbreviation
          boolean periodIsPartOfKeyword = hasPeriod && len == textUpper.length() - 1 && candidate.endsWith(".");

          // Special case: REM is a comment keyword - return as KEYWORD but enter comment mode
          String candidateWithoutPeriod = candidate.endsWith(".") ? candidate.substring(0, candidate.length() - 1) : candidate;
          if (candidateWithoutPeriod.equals("REM")) {
            // Push back everything after REM and enter comment mode
            int pushBackCount = text.length() - len;
            if (!periodIsPartOfKeyword && hasPeriod && pushBackCount == 0) {
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
          if (!periodIsPartOfKeyword && hasPeriod && pushBackCount == 0) {
            // Period is not part of the abbreviation - push it back
            pushBackCount = 1;
          }
          if (pushBackCount > 0) {
            yypushback(pushBackCount);
          }
          return KEYWORD;
        }
      }
    }

    // Even if the whole identifier isn't uppercase, check for uppercase prefix keywords
    // This handles cases like "REMThisisatest" where "REM" is uppercase but "Thisisatest" is mixed case
    if (!isAllUppercase) {
      // Find the length of the uppercase prefix
      int uppercasePrefixLen = 0;
      for (int i = 0; i < textWithoutSuffix.length(); i++) {
        if (Character.isUpperCase(textWithoutSuffix.charAt(i))) {
          uppercasePrefixLen = i + 1;
        } else {
          break;
        }
      }

      // Try to match keywords in the uppercase prefix (minimum 2 chars for a keyword)
      if (uppercasePrefixLen >= 2) {
        String uppercasePrefix = text.substring(0, uppercasePrefixLen).toUpperCase();
        int maxKeywordLength = Math.min(uppercasePrefix.length(), 8);
        for (int len = maxKeywordLength; len >= 2; len--) {
          String candidate = uppercasePrefix.substring(0, len);
          if (KeywordRegistry.isKeyword(candidate)) {
            // Special case: REM is a comment keyword
            if (candidate.equals("REM")) {
              int pushBackCount = text.length() - len;
              if (pushBackCount > 0) {
                yypushback(pushBackCount);
              }
              yybegin(IN_COMMENT);
              return KEYWORD;
            }

            // Found a keyword! Push back the rest for re-lexing
            int pushBackCount = text.length() - len;
            if (pushBackCount > 0) {
              yypushback(pushBackCount);
            }
            return KEYWORD;
          }
        }
      }
    }

    // Not a keyword
    // If it has a $ or # suffix and isn't a keyword, it's a variable identifier
    if (hasDollar || hasHash) {
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
