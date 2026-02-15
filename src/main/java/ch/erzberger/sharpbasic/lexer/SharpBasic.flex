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
      // 1. Exact match (Full keyword or Dotted Abbreviation)
      if (KeywordRegistry.isKeyword(textUpper)) {
        String keywordForRemCheck = hasPeriod ? textUpper.substring(0, textUpper.length() - 1) : textUpper;
        if (keywordForRemCheck.equals("REM")) {
          yybegin(IN_COMMENT);
        }
        return KEYWORD;
      }

      // 2. Greedy match for keywords starting from longest prefix
      int maxLen = textUpper.length();
      for (int len = maxLen; len >= 2; len--) {
        String candidate = textUpper.substring(0, len);
        if (KeywordRegistry.isKeyword(candidate)) {
           // Skip if it's a keyword prefix followed by a variable suffix (e.g., SG followed by $)
           if ((hasDollar || hasHash) && len == maxLen - 1) {
             continue;
           }

           // If it's a dotted abbreviation, it's only a keyword if the dot is consumed.
           // If we are here, it means the whole token wasn't a keyword (checked in step 1).
           // So if candidate is "P." and whole token is "P.Q", we should match "P.".
           
           boolean periodIsPartOfKeyword = hasPeriod && len == maxLen - 1 && candidate.endsWith(".");
           
           String candidateWithoutPeriod = candidate.endsWith(".") ? candidate.substring(0, candidate.length() - 1) : candidate;
           if (candidateWithoutPeriod.equals("REM")) {
             int pushBackCount = text.length() - len;
             if (!periodIsPartOfKeyword && hasPeriod && pushBackCount == 0) pushBackCount = 1;
             if (pushBackCount > 0) yypushback(pushBackCount);
             yybegin(IN_COMMENT);
             return KEYWORD;
           }

           int pushBackCount = text.length() - len;
           if (!periodIsPartOfKeyword && hasPeriod && pushBackCount == 0) pushBackCount = 1;
           if (pushBackCount > 0) yypushback(pushBackCount);
           return KEYWORD;
        }
      }
    }

    // --- CASE-INSENSITIVE MATCHING FOR REM ---
    // Match REM regardless of case if it's followed by something or is the whole token
    if (textUpper.equals("REM") || textUpper.startsWith("REM")) {
        int len = 3;
        yypushback(text.length() - len);
        yybegin(IN_COMMENT);
        return KEYWORD;
    }

    // 3. Not a keyword. Check if it's a valid identifier.
    // 2-character identifiers (MC, ND, A1, etc.) or suffixes ($/#)
    if (text.length() <= 2 || hasDollar || hasHash) {
      return IDENTIFIER;
    }
    
    // Not a keyword and longer than a valid identifier -> push back all but first char
    yypushback(text.length() - 1);
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
