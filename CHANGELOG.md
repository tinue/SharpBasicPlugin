## [0.2.0]

- Major parser and lexer enhancements
- Keyword abbreviations now require a dot suffix (e.g. `P.` for `PRINT`)
- Fixed: `IF` conditions with `AND` and `OR` operators now parse correctly (e.g. `IF &7F AND T GOTO 10`)
- Fixed: hex numbers followed directly by keywords (no spaces) no longer swallow the first letter of the keyword
- `#` is now recognised as a source-only comment at the start of a line, alongside `//`

## [0.1.0]

First release for testing. While it works locally, this is with my infrastructure and my version of IDEA. Your experience might be different.
