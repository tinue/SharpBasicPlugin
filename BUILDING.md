# Building the Sharp BASIC Plugin

## Prerequisites

- **Java Development Kit (JDK) 17 or higher**
  - Download from [Adoptium](https://adoptium.net/) or use your system package manager
  - Verify installation: `java -version`

- **IntelliJ IDEA** (for development and testing)
  - Community Edition or Ultimate Edition 2023.2+
  - Download from [JetBrains](https://www.jetbrains.com/idea/download/)

## Building from Command Line

### 1. Download the Gradle Wrapper JAR

The project uses Gradle 8.5 but the wrapper JAR is not included in the repository. You have two options:

**Option A: Let Gradle download itself (recommended)**

If you have Gradle installed globally:
```bash
gradle wrapper --gradle-version 8.5
```

**Option B: Download manually**

Download the gradle-wrapper.jar from:
https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar

Place it in: `gradle/wrapper/gradle-wrapper.jar`

### 2. Build the Plugin

```bash
# Make the wrapper executable (macOS/Linux)
chmod +x gradlew

# Build the plugin
./gradlew build

# The plugin ZIP will be created in:
# build/distributions/SharpBasicPlugin-0.1.0-SNAPSHOT.zip
```

### 3. Run in Sandbox IDE

```bash
./gradlew runIde
```

This will start a sandboxed IntelliJ IDEA with the plugin installed for testing.

## Building from IntelliJ IDEA

### 1. Open the Project

1. Launch IntelliJ IDEA
2. Select **File** → **Open**
3. Navigate to the `SharpBasicPlugin` directory
4. Click **Open**
5. Wait for IntelliJ to index the project and download dependencies

### 2. Generate Lexer and Parser

The lexer and parser are generated from `.flex` and `.bnf` files:

**Automatic (recommended):**
- The build process automatically generates these files before compilation
- Just build the project and the files will be generated

**Manual generation:**
1. Open the Gradle tool window (View → Tool Windows → Gradle)
2. Navigate to: `SharpBasicPlugin` → `Tasks` → `other`
3. Double-click `generateLexer` to generate the lexer
4. Double-click `generateParser` to generate the parser
5. Refresh the project (File → Reload All from Disk)

Generated files will be in `src/main/gen/`:
- `src/main/gen/ch/erzberger/sharpbasic/lexer/SharpBasicLexer.java`
- `src/main/gen/ch/erzberger/sharpbasic/parser/SharpBasicParser.java`
- `src/main/gen/ch/erzberger/sharpbasic/psi/SharpBasicTypes.java`

### 3. Build the Plugin

**From the menu:**
- Select **Build** → **Build Project**

**From Gradle:**
1. Open the Gradle tool window
2. Navigate to: `SharpBasicPlugin` → `Tasks` → `intellij`
3. Double-click `buildPlugin`

The plugin ZIP will be created in `build/distributions/`.

### 4. Run in Sandbox

**From the menu:**
- Select **Run** → **Run 'Run Plugin'**

**From Gradle:**
1. Open the Gradle tool window
2. Navigate to: `SharpBasicPlugin` → `Tasks` → `intellij`
3. Double-click `runIde`

## Troubleshooting

### Issue: "Cannot find symbol SharpBasicParser"

**Solution:** Generate the parser first:
```bash
./gradlew generateLexer generateParser
```

### Issue: "Cannot find symbol SharpBasicTypes"

**Solution:** The BNF grammar needs to be processed by Grammar-Kit:
```bash
./gradlew generateParser
```

### Issue: "Unsupported class file major version"

**Solution:** You're using the wrong Java version. This project requires Java 17:
```bash
java -version  # Should show version 17 or higher
```

Update JAVA_HOME or use a different JDK.

### Issue: Build fails with "Gradle wrapper not found"

**Solution:** Download the Gradle wrapper JAR as described in step 1 above.

### Issue: "Plugin incompatible with current IntelliJ version"

**Solution:** Check that you're using IntelliJ IDEA 2023.2 or later. Update the `platformVersion` in `gradle.properties` if needed.

## Development Workflow

### Making Changes

1. **Edit source files** in `src/main/java/ch/erzberger/sharpbasic/`
2. **Edit lexer** by modifying `src/main/java/ch/erzberger/sharpbasic/lexer/SharpBasic.flex`
   - Regenerate: `./gradlew generateLexer`
3. **Edit parser** by modifying `src/main/java/ch/erzberger/sharpbasic/parser/SharpBasic.bnf`
   - Regenerate: `./gradlew generateParser`
4. **Rebuild** the project
5. **Test** in sandbox IDE: `./gradlew runIde`

### Testing Changes

1. Make your code changes
2. Build the plugin: `./gradlew buildPlugin`
3. Run sandbox IDE: `./gradlew runIde`
4. Open an example file from `examples/` directory
5. Verify syntax highlighting and code completion work correctly

### Adding Keywords

To add new keywords:

1. Edit the appropriate keyword file:
   - `SharpPc1500Keywords.java` for core keywords
   - `Ce150Keywords.java` for CE-150 keywords
   - `Ce158Keywords.java` for CE-158 keywords

2. Add the keyword with proper category and type:
   ```java
   keywords.add(new BasicKeyword("NEWCMD", "NEW", 0xXXXX, PC1500_CORE, STATEMENT));
   ```

3. Rebuild and test

The keyword will automatically be available for:
- Lexer tokenization (via KeywordRegistry)
- Syntax highlighting
- Code completion

## Project Structure

```
SharpBasicPlugin/
├── build.gradle.kts           # Build configuration
├── settings.gradle.kts        # Gradle settings
├── gradle.properties          # Plugin properties
├── src/main/
│   ├── java/                  # Java source files
│   │   └── ch/erzberger/sharpbasic/
│   │       ├── keywords/      # Keyword definitions
│   │       ├── lexer/        # Lexer specification
│   │       ├── parser/       # Parser specification
│   │       ├── psi/          # PSI elements
│   │       ├── syntax/       # Syntax highlighting
│   │       └── completion/   # Code completion
│   ├── gen/                   # Generated code (gitignored)
│   └── resources/            # Plugin resources
│       ├── META-INF/plugin.xml
│       └── icons/
├── examples/                  # Example BASIC programs
└── build/                     # Build output (gitignored)
    └── distributions/        # Plugin ZIP files
```

## Clean Build

To perform a clean build:

```bash
./gradlew clean build
```

This removes all generated files and builds from scratch.

## Continuous Integration

For CI/CD pipelines:

```bash
# Build without daemon (recommended for CI)
./gradlew build --no-daemon

# Skip tests if needed
./gradlew build --no-daemon -x test

# Build and verify plugin structure
./gradlew buildPlugin verifyPlugin
```

## Next Steps

After successfully building:

1. Install the plugin in your IDE (see README.md)
2. Test with the example files in `examples/`
3. Report any issues or contribute improvements
4. Refer to the [IntelliJ Plugin Development Guide](https://plugins.jetbrains.com/docs/intellij/welcome.html) for advanced features
