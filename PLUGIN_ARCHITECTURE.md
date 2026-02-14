# Plugin Architecture Guide

**For developers new to lexing/parsing and IntelliJ plugin development**

This document explains how the Sharp PC-1500 BASIC IntelliJ plugin works, from basic concepts to the specific files and packages in this project.

---

## Table of Contents

1. [Core Concepts](#core-concepts)
2. [IntelliJ Plugin Basics](#intellij-plugin-basics)
3. [Project Structure](#project-structure)
4. [How It All Works Together](#how-it-all-works-together)
5. [Package-by-Package Guide](#package-by-package-guide)
6. [Adding New Features](#adding-new-features)

---

## Core Concepts

### What is Lexing?

**Lexing** (lexical analysis) is the process of converting raw text into **tokens**. Think of it like breaking a sentence into words.

**Example:**
```basic
10 PRINT "Hello"
```

The lexer breaks this into tokens:
```
LINE_NUMBER: "10"
KEYWORD: "PRINT"
STRING: "\"Hello\""
```

**Analogy:** If code is a sentence, the lexer identifies the nouns, verbs, and punctuation.

### What is Parsing?

**Parsing** (syntax analysis) is the process of organizing tokens into a **tree structure** based on grammar rules.

**Example:**
The tokens from above become a tree:
```
line
├── LINE_NUMBER: "10"
└── statement
    ├── KEYWORD: "PRINT"
    └── STRING: "\"Hello\""
```

**Analogy:** If lexing identifies words, parsing understands the sentence structure (subject, verb, object).

### What is PSI?

**PSI** (Program Structure Interface) is IntelliJ's representation of code as a tree of elements. Each element knows:
- What it represents (a keyword, a string, a statement, etc.)
- Where it is in the file (line number, character position)
- Its parent and children in the tree

**Why PSI matters:** All IntelliJ features (syntax highlighting, code completion, refactoring, navigation) work by examining or modifying the PSI tree.

---

## IntelliJ Plugin Basics

### How IntelliJ Processes Code

When you open a file in IntelliJ, this happens:

1. **File Type Detection**: IntelliJ checks the file extension (`.bas`, `.pc1500`)
2. **Language Recognition**: Maps the file type to a language (Sharp BASIC)
3. **Lexing**: Converts text → tokens
4. **Parsing**: Converts tokens → PSI tree
5. **Features Activate**: Syntax highlighting, completion, etc. use the PSI tree

### Plugin Extension Points

A plugin extends IntelliJ by registering **extension points** in `plugin.xml`:

- **Language**: Defines a new programming language
- **File Type**: Associates file extensions with the language
- **Lexer**: Provides the lexer for tokenization
- **Parser**: Provides the parser for building the PSI tree
- **Syntax Highlighter**: Colors tokens based on type
- **Completion Contributor**: Suggests keywords/identifiers when typing
- **Actions**: Custom menu items and keyboard shortcuts

---

## Project Structure

```
SharpBasicPlugin/
├── src/main/
│   ├── java/ch/erzberger/sharpbasic/
│   │   ├── SharpBasicLanguage.java           # Language definition
│   │   ├── SharpBasicFileType.java           # File type (.bas, .pc1500)
│   │   ├── SharpBasicIcons.java              # File icons
│   │   ├── keywords/                         # Keyword definitions
│   │   ├── lexer/                            # Tokenization
│   │   ├── parser/                           # Syntax tree building
│   │   ├── psi/                              # PSI elements
│   │   ├── syntax/                           # Syntax highlighting
│   │   ├── completion/                       # Code completion
│   │   └── formatter/                        # Code formatting
│   ├── gen/                                  # Auto-generated code
│   └── resources/
│       ├── META-INF/plugin.xml               # Plugin configuration
│       └── icons/sharp-basic-icon.svg        # Icon file
└── examples/                                 # Sample BASIC programs
```

---

## How It All Works Together

### End-to-End Flow: Opening a `.bas` File

Let's trace what happens when you open `hello.bas` in IntelliJ:

#### Step 1: File Type Recognition
**File**: `SharpBasicFileType.java`

```java
public class SharpBasicFileType extends LanguageFileType {
    public String getDefaultExtension() {
        return "bas";  // Recognizes .bas files
    }
}
```

IntelliJ checks: "Is this file `.bas`?" → Yes → Uses Sharp BASIC language.

---

#### Step 2: Lexing (Text → Tokens)
**Files**:
- `SharpBasic.flex` (lexer specification)
- `PreprocessingSharpBasicLexer.java` (custom preprocessing)
- `SharpBasicLexerAdapter.java` (connects to IntelliJ)

**Input code:**
```basic
10 PRINT "Hi"
```

**Lexer process:**
1. `PreprocessingSharpBasicLexer` removes spaces (Sharp PC-1500 has no whitespace)
2. JFlex-generated lexer reads character-by-character:
   - Sees `10` → Checks: "Is this at line start?" → Yes → Token: `LINE_NUMBER`
   - Sees `PRINT` → Looks up in `KeywordRegistry` → Token: `KEYWORD`
   - Sees `"Hi"` → Matches string pattern → Token: `STRING`

**Output tokens:**
```
LINE_NUMBER: "10"
KEYWORD: "PRINT"
STRING: "\"Hi\""
```

**Key file: `SharpBasic.flex`**
This is a JFlex specification file that defines patterns:

```java
{LINE_NUMBER} { return SharpBasicTokenTypes.LINE_NUMBER; }
{KEYWORD}     { return SharpBasicTokenTypes.KEYWORD; }
{STRING}      { return SharpBasicTokenTypes.STRING; }
```

**Note:** `SharpBasic.flex` is **not** Java code. It's processed by the Gradle task `generateLexer` to produce `SharpBasicLexer.java` (auto-generated).

---

#### Step 3: Parsing (Tokens → PSI Tree)
**Files**:
- `SharpBasic.bnf` (grammar specification)
- `SharpBasicParserDefinition.java` (connects to IntelliJ)

**Input tokens:**
```
LINE_NUMBER: "10"
KEYWORD: "PRINT"
STRING: "\"Hi\""
```

**Parser process:**
1. Parser reads the BNF grammar rules:
   ```
   line ::= LINE_NUMBER? statement_list?
   statement ::= KEYWORD expression?
   ```
2. Builds a tree structure:
   ```
   SharpBasicFile
   └── line
       ├── LINE_NUMBER: "10"
       └── statement
           ├── KEYWORD: "PRINT"
           └── STRING: "\"Hi\""
   ```

**Key file: `SharpBasic.bnf`**
This is a Grammar-Kit BNF specification:

```bnf
file ::= line*
line ::= LINE_NUMBER? statement_list?
statement ::= KEYWORD expression?
```

**Note:** `SharpBasic.bnf` is **not** Java code. It's processed by the Gradle task `generateParser` to produce:
- `SharpBasicParser.java`
- `SharpBasicTypes.java`
- PSI implementation classes

---

#### Step 4: Syntax Highlighting
**Files**:
- `SharpBasicSyntaxHighlighter.java`
- `SharpBasicSyntaxHighlighterFactory.java`

**Input:** PSI tree with tokens

**Process:**
1. IntelliJ calls `getTokenHighlights()` for each token
2. Highlighter maps token type → color:
   ```java
   case KEYWORD: return KEYWORD_KEYS;        // Bold blue
   case STRING:  return STRING_KEYS;         // Green
   case COMMENT: return COMMENT_KEYS;        // Gray italic
   ```

**Output:** Colored code in the editor

---

#### Step 5: Code Completion
**File**: `SharpBasicCompletionContributor.java`

**Trigger:** User types `PR` and presses Ctrl+Space

**Process:**
1. IntelliJ calls `fillCompletionVariants()`
2. Plugin checks `KeywordRegistry` for keywords starting with "PR"
3. Returns suggestions: `PRINT (P.) [PC-1500]`

**User sees:** A popup with `PRINT` as a suggestion

---

## Package-by-Package Guide

### 📦 Root Package: `ch.erzberger.sharpbasic`

**Core language definition files**

| File | Purpose | Key Responsibilities |
|------|---------|---------------------|
| `SharpBasicLanguage.java` | Language singleton | Identifies Sharp BASIC as a language to IntelliJ |
| `SharpBasicFileType.java` | File type definition | Associates `.bas` and `.pc1500` extensions |
| `SharpBasicIcons.java` | Icon provider | Provides the file icon |

**Think of these as:** The "birth certificate" of the Sharp BASIC language in IntelliJ.

---

### 📦 Package: `keywords`

**Keyword definitions and lookup**

| File | Purpose | Contains |
|------|---------|----------|
| `BasicKeyword.java` | Keyword model | Full name, abbreviation, category, type |
| `KeywordCategory.java` | Enum | PC1500_CORE, CE150_EXTENSION, CE158_EXTENSION |
| `KeywordType.java` | Enum | STATEMENT, FUNCTION, OPERATOR, KEYWORD |
| `SharpPc1500Keywords.java` | Keyword list | 97 core PC-1500 keywords |
| `Ce150Keywords.java` | Keyword list | 14 CE-150 graphics keywords |
| `Ce158Keywords.java` | Keyword list | 11 CE-158 communications keywords |
| `KeywordRegistry.java` | Central registry | O(1) HashMap lookup for all 122 keywords |

**Example usage:**
```java
BasicKeyword keyword = KeywordRegistry.lookup("PRINT");
// Returns: BasicKeyword{name="PRINT", abbrev="P", category=PC1500_CORE, type=STATEMENT}
```

**Why this exists:** Sharp BASIC has 122 keywords with abbreviations (e.g., `PRINT` → `P.`). The registry centralizes this knowledge so the lexer, parser, formatter, and completion can all use it.

---

### 📦 Package: `lexer`

**Converts text into tokens**

| File | Purpose | Technology |
|------|---------|-----------|
| `SharpBasic.flex` | Lexer specification | JFlex (generates Java code) |
| `PreprocessingSharpBasicLexer.java` | Whitespace preprocessor | Custom Java code |
| `SharpBasicLexerAdapter.java` | IntelliJ adapter | Connects preprocessing lexer to IntelliJ |

**Flow:**
```
Raw text → PreprocessingSharpBasicLexer (removes spaces)
         → JFlex lexer (tokenizes)
         → SharpBasicLexerAdapter (wraps for IntelliJ)
```

**Why preprocessing?** The Sharp PC-1500 ignores all spaces outside of strings. The preprocessor removes them before tokenization to simplify the lexer.

**Example:**
```
Input:  "10 PRINT   \"Hi\""
After preprocessing: "10PRINT\"Hi\""  (spaces in string preserved!)
Tokens: LINE_NUMBER("10"), KEYWORD("PRINT"), STRING("\"Hi\"")
```

---

### 📦 Package: `parser`

**Builds the PSI tree from tokens**

| File | Purpose | Technology |
|------|---------|-----------|
| `SharpBasic.bnf` | Grammar specification | Grammar-Kit BNF (generates Java code) |
| `SharpBasicParserDefinition.java` | IntelliJ adapter | Connects parser to IntelliJ |

**Grammar rules example:**
```bnf
file ::= line*
line ::= LINE_NUMBER? statement_list?
statement_list ::= statement (COLON statement)*
statement ::= print_stmt | for_stmt | if_stmt | ...
```

**Translation:**
- A file contains zero or more lines
- A line optionally starts with a line number, followed by statements
- Statements are separated by colons (`:`)

**Why BNF?** It's a declarative way to define syntax. Grammar-Kit reads the BNF and auto-generates the parser code.

---

### 📦 Package: `psi`

**PSI element definitions**

| File | Purpose |
|------|---------|
| `SharpBasicFile.java` | Root PSI element (represents the whole file) |
| `SharpBasicElementType.java` | Base class for parser elements (statements, expressions) |
| `SharpBasicTokenType.java` | Base class for lexer tokens |
| `SharpBasicTokenTypes.java` | Token type constants (KEYWORD, STRING, etc.) |

**Note:** Most PSI classes are **auto-generated** by Grammar-Kit in `src/main/gen/`.

**PSI tree example:**
```
SharpBasicFile (represents hello.bas)
└── line
    ├── LINE_NUMBER: "10"
    └── statement
        ├── KEYWORD: "PRINT"
        └── STRING: "\"Hello\""
```

---

### 📦 Package: `syntax`

**Syntax highlighting**

| File | Purpose |
|------|---------|
| `SharpBasicSyntaxHighlighter.java` | Maps token types → colors |
| `SharpBasicSyntaxHighlighterFactory.java` | Factory for creating highlighters |

**Color mappings:**
```java
KEYWORD     → Bold blue
STRING      → Green
COMMENT     → Gray italic
LINE_NUMBER → Gray
NUMBER      → Blue
```

**How it works:** IntelliJ calls the highlighter for each token, asking "What color should this be?"

---

### 📦 Package: `completion`

**Code completion suggestions**

| File | Purpose |
|------|---------|
| `SharpBasicCompletionContributor.java` | Suggests keywords when typing |

**Features:**
- Case-insensitive matching (type `pr` → suggests `PRINT`)
- Shows abbreviations: `PRINT (P.)`
- Shows category: `[PC-1500]`, `[CE-150]`, `[CE-158]`
- Priority: PC-1500 core keywords ranked higher

**How it works:** When the user presses Ctrl+Space, IntelliJ calls `fillCompletionVariants()`. The plugin queries `KeywordRegistry` and returns matching keywords.

---

### 📦 Package: `formatter`

**Code formatting actions**

| File | Purpose |
|------|---------|
| `ReformatAsPC1500Action.java` | Menu action: "Reformat as PC-1500 BASIC" |
| `SharpBasicCodeReformatter.java` | Standard formatter (expands abbreviations, adds spaces) |
| `ReformatAsCompactPC1500Action.java` | Menu action: "Reformat as Compact PC-1500 BASIC" |
| `SharpBasicCompactReformatter.java` | Compact formatter (abbreviates, removes spaces) |
| `SharpBasicFormattingModelBuilder.java` | IntelliJ formatter integration (unused by custom actions) |
| `SharpBasicBlock.java` | IntelliJ formatter block (unused by custom actions) |

**Two formatters:**

1. **Standard (PC-1500)**: Expands abbreviations, adds space after keywords
   - `10P."Hi"` → `10 PRINT "Hi"`
   - Use: Clean, readable source code

2. **Compact**: Abbreviates keywords, removes all spaces
   - `10 PRINT "Hi"` → `10P."Hi"`
   - Use: Minimizes code for PC-1500 emulator input buffer (79-char limit)

---

### 📦 Package: `gen` (Auto-generated)

**Generated by Gradle tasks**

This directory is **not** in the Git repository. It's created when you run:
```bash
./gradlew generateLexer generateParser
```

**Generated files:**
- `SharpBasicLexer.java` (from `SharpBasic.flex`)
- `SharpBasicParser.java` (from `SharpBasic.bnf`)
- `SharpBasicTypes.java` (token/element type constants)
- PSI implementation classes (`SharpBasicLineImpl.java`, etc.)

**Important:** Never edit these files directly! They're regenerated on every build.

---

### 📄 File: `src/main/resources/META-INF/plugin.xml`

**Plugin configuration and registration**

This is the "control center" of the plugin. It tells IntelliJ:

- Plugin name, version, description
- What extensions to register:
  ```xml
  <lang.parserDefinition language="SharpBasic"
      implementationClass="ch.erzberger.sharpbasic.parser.SharpBasicParserDefinition"/>

  <lang.syntaxHighlighterFactory language="SharpBasic"
      implementationClass="ch.erzberger.sharpbasic.syntax.SharpBasicSyntaxHighlighterFactory"/>

  <completion.contributor language="SharpBasic"
      implementationClass="ch.erzberger.sharpbasic.completion.SharpBasicCompletionContributor"/>
  ```

- Actions (menu items):
  ```xml
  <action id="SharpBasic.ReformatAsPC1500"
          class="ch.erzberger.sharpbasic.formatter.ReformatAsPC1500Action">
      <keyboard-shortcut keymap="$default" first-keystroke="ctrl alt shift F"/>
  </action>
  ```

**Analogy:** If the plugin is a machine, `plugin.xml` is the wiring diagram showing how all the parts connect.

---

## How It All Works Together

### Example: Typing `P` and Pressing Ctrl+Space

Let's trace the complete flow:

1. **User types** `P` in the editor
2. **User presses** Ctrl+Space
3. **IntelliJ checks** `plugin.xml` → finds `SharpBasicCompletionContributor`
4. **IntelliJ calls** `SharpBasicCompletionContributor.fillCompletionVariants()`
5. **Plugin queries** `KeywordRegistry.getAll()`
6. **Plugin filters** keywords starting with "P" (case-insensitive)
7. **Plugin finds**: `PRINT`, `POKE`, `POKE#`, etc.
8. **Plugin creates** `LookupElement` objects with:
   - Text: `PRINT`
   - Tail text: `(P.)`
   - Type text: `[PC-1500]`
9. **IntelliJ displays** completion popup
10. **User selects** `PRINT`
11. **IntelliJ inserts** `PRINT` into the editor

**Files involved:**
- `plugin.xml` (registers the contributor)
- `SharpBasicCompletionContributor.java` (handles completion)
- `KeywordRegistry.java` (provides keyword list)
- `SharpPc1500Keywords.java` (defines `PRINT` keyword)

---

### Example: Syntax Highlighting `10 PRINT "Hi"`

1. **File opened** → IntelliJ reads text
2. **Lexer called** → `SharpBasicLexerAdapter` tokenizes:
   - `LINE_NUMBER: "10"`
   - `KEYWORD: "PRINT"`
   - `STRING: "\"Hi\""`
3. **Parser called** → `SharpBasicParser` builds PSI tree
4. **Highlighter called** → For each token:
   - `LINE_NUMBER` → Gray
   - `KEYWORD` → Bold blue
   - `STRING` → Green
5. **Editor displays** colored code

**Files involved:**
- `SharpBasic.flex` (lexer spec)
- `SharpBasic.bnf` (parser spec)
- `SharpBasicSyntaxHighlighter.java` (colors)
- `plugin.xml` (registers all components)

---

## Adding New Features

### Example: Add a New Keyword

**Goal:** Add the keyword `FOOBAR` with abbreviation `FOO.`

**Steps:**

1. **Add to keyword list** (`SharpPc1500Keywords.java`):
   ```java
   new BasicKeyword("FOOBAR", "FOO", KeywordCategory.PC1500_CORE, KeywordType.STATEMENT)
   ```

2. **Rebuild** (regenerate lexer/parser):
   ```bash
   ./gradlew generateLexer generateParser build
   ```

3. **Test:**
   - Type `FOOBAR` → Should be highlighted blue
   - Type `FOO` + Ctrl+Space → Should suggest `FOOBAR`
   - Type `FOO.` → Should recognize as `FOOBAR`

**That's it!** The keyword is now integrated into:
- Lexer (recognized as `KEYWORD` token)
- Syntax highlighting (blue color)
- Code completion (suggests `FOOBAR (FOO.)`)
- Formatter (expands `FOO.` → `FOOBAR`)

---

### Example: Add a New Action

**Goal:** Add a menu item "Convert to Uppercase"

**Steps:**

1. **Create action class** (`src/main/java/.../ConvertToUppercaseAction.java`):
   ```java
   public class ConvertToUppercaseAction extends AnAction {
       @Override
       public void actionPerformed(@NotNull AnActionEvent e) {
           Editor editor = e.getData(CommonDataKeys.EDITOR);
           Document doc = editor.getDocument();
           String text = doc.getText();
           doc.setText(text.toUpperCase());
       }
   }
   ```

2. **Register in `plugin.xml`**:
   ```xml
   <action id="SharpBasic.ConvertToUppercase"
           class="ch.erzberger.sharpbasic.ConvertToUppercaseAction"
           text="Convert to Uppercase">
       <add-to-group group-id="EditorPopupMenu" anchor="last"/>
   </action>
   ```

3. **Build and test**:
   ```bash
   ./gradlew buildPlugin runIde
   ```

**Result:** Right-click in editor → "Convert to Uppercase" menu item appears.

---

## Key Takeaways

### The Plugin's Core Flow

```
User opens .bas file
    ↓
SharpBasicFileType recognizes it
    ↓
SharpBasicLexer tokenizes (text → tokens)
    ↓
SharpBasicParser builds PSI tree (tokens → tree)
    ↓
Features activate:
    - Syntax highlighting (colors tokens)
    - Code completion (suggests keywords)
    - Formatting (reformats code)
```

### Important Concepts

1. **Lexer**: Text → Tokens (defined in `.flex`)
2. **Parser**: Tokens → Tree (defined in `.bnf`)
3. **PSI**: IntelliJ's tree representation of code
4. **Extension Points**: How plugins extend IntelliJ (defined in `plugin.xml`)
5. **Auto-generation**: `.flex` and `.bnf` files generate Java code

### Files You Edit vs. Files You Don't

**✅ Edit these:**
- Keyword lists (`SharpPc1500Keywords.java`, etc.)
- Lexer spec (`SharpBasic.flex`)
- Parser spec (`SharpBasic.bnf`)
- Feature implementations (syntax, completion, formatter)
- Plugin config (`plugin.xml`)

**❌ Don't edit these (auto-generated):**
- `src/main/gen/` directory
- `SharpBasicLexer.java`
- `SharpBasicParser.java`
- `SharpBasicTypes.java`

---

## Further Reading

- **IntelliJ Plugin Development**: https://plugins.jetbrains.com/docs/intellij/welcome.html
- **JFlex Manual** (lexer): https://jflex.de/manual.html
- **Grammar-Kit** (parser): https://github.com/JetBrains/Grammar-Kit
- **Sharp PC-1500 Reference**: See `README.md` for links

---

**Questions?** Check the source code comments, or explore the examples in `examples/` directory.
