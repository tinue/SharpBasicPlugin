## 0.2.0

### New features
- Three additional formatter actions (all in the Code menu and editor right-click menu):
  - **Reformat as Nice PC-1500 BASIC** (`Ctrl+Alt+Shift+N`) — human-readable output with spaces around operators and keywords
  - **Reformat as PC-1500 BASIC (Strip Comments)** (`Ctrl+Alt+Shift+D`) — like the standard reformat but removes `//` and `#` source comments, producing device-ready output
  - **Renumber BASIC** (`Ctrl+Alt+Shift+R`) — renumbers all line numbers to a canonical sequence (10, 20, 30, …) and remaps all `GOTO`/`GOSUB` targets accordingly
- `//` at the start of a line is now recognised as a source-only comment (highlighted as a comment, stripped by the Strip Comments formatter, never sent to the device)
- `#` at the start of a line is also recognised as a source-only comment

### Parser and lexer fixes
- Keyword abbreviations now strictly require a dot suffix (e.g. `P.` for `PRINT`); bare single-letter tokens are no longer misread as keywords
- `IF` conditions with `AND` and `OR` operators now parse correctly (e.g. `IF &7F AND T GOTO 10`)
- Hex numbers immediately followed by a keyword (no space) no longer swallow the first letter of the keyword
- Short decimal literals such as `.5` are now recognised
- Trailing separators such as `PRINT A;` no longer produce false parse errors
- Function calls without parentheses (e.g. `SIN X`) now parse correctly
- Fixed compact-formatter abbreviation for `POKE#`

### Code completion
- Auto-popup is suppressed while typing; completion no longer inserts keywords unexpectedly when pressing Enter
- Multi-character prefix matching now works correctly for keyword tokens

## 0.1.0

First release for testing. While it works locally, this is with my infrastructure and my version of IDEA. Your experience might be different.
