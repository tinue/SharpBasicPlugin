# Sharp PC-1500 BASIC IntelliJ Plugin

An IntelliJ IDEA plugin providing language support for Sharp PC-1500 BASIC, a vintage programming language from the 1980s Sharp pocket computer.

## Features

- **Syntax Highlighting**: Color-coded syntax for keywords, strings, numbers, comments, and line numbers
- **Code Completion**: Smart keyword suggestions with full and abbreviated forms
- **File Type Recognition**: Automatic recognition of `.bas` and `.pc1500` files
- **Comprehensive Keyword Support**:
  - 97 PC-1500 core keywords
  - 14 CE-150 graphics/printer extension keywords
  - 11 CE-158 communications extension keywords
  - Support for abbreviated command forms (e.g., `P.` for `PRINT`, `G.` for `GOTO`)

## Installation

### From Source

1. Clone this repository:
   ```bash
   git clone https://github.com/erzberger/SharpBasicPlugin.git
   cd SharpBasicPlugin
   ```

2. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

3. Install the plugin in IntelliJ IDEA:
   - Open IntelliJ IDEA
   - Go to **Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk...**
   - Select `build/distributions/SharpBasicPlugin-0.1.0-SNAPSHOT.zip`
   - Restart IntelliJ IDEA

### From JetBrains Marketplace

*(Coming soon)*

## Usage

### Opening Sharp BASIC Files

Simply open any `.bas` or `.pc1500` file in IntelliJ IDEA. The plugin will automatically:
- Recognize the file type
- Apply syntax highlighting
- Enable code completion

### Code Completion

Type any keyword prefix and press `Ctrl+Space` to see completion suggestions:

- **Full keywords**: Type `PR` → suggests `PRINT`
- **Abbreviations**: Type `P.` → expands to `PRINT`
- **Category indicators**: Shows `[PC-1500]`, `[CE-150]`, or `[CE-158]` for each keyword
- **Abbreviation hints**: Shows abbreviated form in gray, e.g., `PRINT (P.)`

### Syntax Highlighting Colors

- **Keywords**: Bold blue (e.g., `PRINT`, `FOR`, `IF`)
- **Line numbers**: Gray (e.g., `10`, `20`, `30`)
- **Strings**: Green (e.g., `"Hello World"`)
- **Numbers**: Blue (e.g., `42`, `3.14`, `1.5E-3`)
- **Comments**: Gray italic (e.g., `REM This is a comment`)
- **Operators**: Default color (e.g., `+`, `-`, `*`, `/`)
- **Identifiers**: Default color (e.g., `X`, `A$`, `COUNT`)

## Sharp BASIC Language Reference

### PC-1500 Core Commands

The plugin supports all 97 core PC-1500 BASIC commands, including:

- **Control Flow**: `GOTO`, `GOSUB`, `RETURN`, `FOR...NEXT`, `IF...THEN`, `ON...GOTO/GOSUB`
- **I/O**: `PRINT`, `INPUT`, `BEEP`, `CURSOR`, `CLS`
- **Data**: `DATA`, `READ`, `RESTORE`, `DIM`
- **Math Functions**: `SIN`, `COS`, `TAN`, `ABS`, `INT`, `SQR`, `EXP`, `LN`, `LOG`
- **String Functions**: `LEFT$`, `RIGHT$`, `MID$`, `CHR$`, `ASC`, `STR$`, `VAL`, `LEN`
- **File Operations**: `CLOAD`, `CSAVE`, `MERGE`, `CHAIN`
- **System**: `NEW`, `RUN`, `STOP`, `END`, `CLEAR`, `MEM`

### CE-150 Graphics/Printer Extension

14 keywords for graphics and printer control:
- `GRAPH`, `TEXT`, `SORGN`, `GCURSOR`, `GLCURSOR`, `LCURSOR`
- `GPRINT`, `LINE`, `RLINE`, `ROTATE`, `COLOR`, `CSIZE`
- `LPRINT`, `LLIST`

### CE-158 Communications Extension

11 keywords for serial communication:
- `SETCOM`, `SETDEV`, `TRANSMIT`, `TERMINAL`, `PROTOCOL`, `DTE`, `RMT`, `TEST`
- `COM$`, `DEV$`, `INSTAT`, `OUTSTAT`, `RINKEY$`

### Abbreviated Forms

Sharp BASIC supports abbreviated command forms with a dot suffix:

| Full Command | Abbreviation | Full Command | Abbreviation |
|-------------|-------------|-------------|-------------|
| PRINT | P. | GOTO | G. |
| FOR | F. | NEXT | N. |
| INPUT | I. | IF | IF |
| GOSUB | GOS. | RETURN | RE. |

## Examples

See the `examples/` directory for sample programs:

- `hello.bas`: Simple "Hello World" program
- `loops.bas`: FOR...NEXT loop examples
- `graphics.bas`: CE-150 graphics commands
- `comm.bas`: CE-158 serial communication

## Development

### Prerequisites

- Java 17 or higher
- IntelliJ IDEA 2023.2 or higher (for development)

### Building from Source

```bash
# Clone the repository
git clone https://github.com/erzberger/SharpBasicPlugin.git
cd SharpBasicPlugin

# Build the plugin
./gradlew buildPlugin

# Run in sandbox IDE for testing
./gradlew runIde
```

### Project Structure

```
SharpBasicPlugin/
├── src/main/java/ch/erzberger/sharpbasic/
│   ├── keywords/          # Keyword definitions and registry
│   ├── lexer/            # Lexer (tokenization)
│   ├── parser/           # Parser (syntax analysis)
│   ├── psi/              # PSI (Program Structure Interface)
│   ├── syntax/           # Syntax highlighting
│   └── completion/       # Code completion
├── src/main/resources/
│   ├── META-INF/plugin.xml  # Plugin descriptor
│   └── icons/            # File type icons
└── examples/             # Example Sharp BASIC programs
```

### Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Resources

- [Sharp PC-1500 Technical Reference Manual](https://sharppocketcomputers.com/)
- [CE-150 Graphics Extension Manual](https://sharppocketcomputers.com/)
- [CE-158 Communications Extension Manual](https://sharppocketcomputers.com/)
- [IntelliJ Plugin Development](https://plugins.jetbrains.com/docs/intellij/welcome.html)

## License

MIT License - see LICENSE file for details

## Acknowledgments

- Sharp Corporation for the original PC-1500 pocket computer
- JetBrains for the IntelliJ Platform SDK
- The vintage computing community for preserving Sharp BASIC documentation

## Version History

### 0.1.0-SNAPSHOT (Current)
- Initial MVP release
- Syntax highlighting for all 122 keywords
- Code completion with abbreviations
- File type recognition for .bas and .pc1500 files
- Support for PC-1500, CE-150, and CE-158 keywords

## Future Enhancements

Planned features for future releases:
- Live error detection and validation
- Refactoring support (rename variables, extract methods)
- Code folding for multi-line structures
- Quick documentation lookup
- Integration with Sharp PC-1500 emulators
- Export to Sharp-compatible formats
