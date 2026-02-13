package ch.erzberger.sharpbasic.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import ch.erzberger.sharpbasic.psi.SharpBasicTokenType;
import ch.erzberger.sharpbasic.keywords.KeywordRegistry;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%class SharpBasicLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

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
IDENTIFIER = [A-Za-z][A-Za-z0-9]*
STRING_VAR = [A-Za-z][A-Za-z0-9]* \$
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

%state COMMENT

%%

<YYINITIAL> {
  {LINE_TERMINATOR}       { atLineStart = true; return new SharpBasicTokenType("LINE_TERMINATOR"); }
  {WHITE_SPACE}           { return WHITE_SPACE; }

  // Line numbers only at line start
  {LINE_NUMBER} / {WHITE_SPACE} {
    if (atLineStart) {
      atLineStart = false;
      return new SharpBasicTokenType("LINE_NUMBER");
    } else {
      return new SharpBasicTokenType("NUMBER");
    }
  }

  // Numbers
  {SCIENTIFIC}            { atLineStart = false; return new SharpBasicTokenType("NUMBER"); }
  {DECIMAL}               { atLineStart = false; return new SharpBasicTokenType("NUMBER"); }
  {INTEGER}               { atLineStart = false; return new SharpBasicTokenType("NUMBER"); }

  // Strings
  {STRING}                { atLineStart = false; return new SharpBasicTokenType("STRING"); }

  // Comment detection - REM keyword
  {COMMENT_START} / ({WHITE_SPACE} | {LINE_TERMINATOR} | <<EOF>>) {
    atLineStart = false;
    yybegin(COMMENT);
    return new SharpBasicTokenType("COMMENT");
  }

  // Identifiers and keywords
  {STRING_VAR}            {
    atLineStart = false;
    return new SharpBasicTokenType("IDENTIFIER");
  }

  {IDENTIFIER} ({PERIOD})? {
    atLineStart = false;
    String text = yytext().toString();
    // Check if this is a keyword (case-insensitive)
    if (KeywordRegistry.isKeyword(text)) {
      return new SharpBasicTokenType("KEYWORD");
    }
    return new SharpBasicTokenType("IDENTIFIER");
  }

  // Operators
  {PLUS}                  { atLineStart = false; return new SharpBasicTokenType("PLUS"); }
  {MINUS}                 { atLineStart = false; return new SharpBasicTokenType("MINUS"); }
  {MULT}                  { atLineStart = false; return new SharpBasicTokenType("MULT"); }
  {DIV}                   { atLineStart = false; return new SharpBasicTokenType("DIV"); }
  {POWER}                 { atLineStart = false; return new SharpBasicTokenType("POWER"); }
  {LE}                    { atLineStart = false; return new SharpBasicTokenType("LE"); }
  {GE}                    { atLineStart = false; return new SharpBasicTokenType("GE"); }
  {NE}                    { atLineStart = false; return new SharpBasicTokenType("NE"); }
  {EQ}                    { atLineStart = false; return new SharpBasicTokenType("EQ"); }
  {LT}                    { atLineStart = false; return new SharpBasicTokenType("LT"); }
  {GT}                    { atLineStart = false; return new SharpBasicTokenType("GT"); }

  // Separators
  {LPAREN}                { atLineStart = false; return new SharpBasicTokenType("LPAREN"); }
  {RPAREN}                { atLineStart = false; return new SharpBasicTokenType("RPAREN"); }
  {COMMA}                 { atLineStart = false; return new SharpBasicTokenType("COMMA"); }
  {SEMICOLON}             { atLineStart = false; return new SharpBasicTokenType("SEMICOLON"); }
  {COLON}                 { atLineStart = false; return new SharpBasicTokenType("COLON"); }
  {HASH}                  { atLineStart = false; return new SharpBasicTokenType("HASH"); }
}

<COMMENT> {
  {LINE_TERMINATOR}       { yybegin(YYINITIAL); atLineStart = true; return new SharpBasicTokenType("LINE_TERMINATOR"); }
  [^\r\n]+                { return new SharpBasicTokenType("COMMENT"); }
  <<EOF>>                 { yybegin(YYINITIAL); return new SharpBasicTokenType("COMMENT"); }
}

[^]                       { return BAD_CHARACTER; }
